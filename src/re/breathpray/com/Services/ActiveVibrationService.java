package re.breathpray.com.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import re.breathpray.com.BreathPrayConstants;

/**
 * Date: 16.05.14
 */
public class ActiveVibrationService extends Service {

    private static final String TAG = "ActiveVibrationService";
    private int pattern;
    private float volume;
    private boolean volumeActive;
    private boolean acousticNotificationActive;
    private String acousticNotificationUri;
    private int duration;
    private DateTime nextVibration;
    private Vibrator vibrator;

    @Override
    public void onCreate() {
        super.onCreate();
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final AudioManager audioManager = ((AudioManager) getSystemService(Context.AUDIO_SERVICE));
        if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT && audioManager.getMode() != AudioManager.MODE_IN_CALL) {
            pattern =  intent.getIntExtra(BreathPrayConstants.patternIntentExtraFieldName, 150);
            duration = intent.getIntExtra(BreathPrayConstants.durationIntentExtraFieldName, 10);
            volume = intent.getFloatExtra(BreathPrayConstants.acousticVolumeIntentExtraFieldName, 0.5f);
            volumeActive = intent.getBooleanExtra(BreathPrayConstants.acousticUniqueVolumeActiveIntentExtraFieldName, false);
            acousticNotificationActive = intent.getBooleanExtra(BreathPrayConstants.acousticNotificationActiveIntentExtraFieldName, false);
            nextVibration = DateTime.now().plusMinutes(intent.getIntExtra(BreathPrayConstants.repeatTimeIntentExtraFieldName,15));

            if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE)
                acousticNotificationActive = false;
            acousticNotificationUri = intent.getStringExtra(BreathPrayConstants.acousticUriIntentExtraFieldName);

            //is still in time?
            final String stringExtra = intent.getStringExtra(BreathPrayConstants.endVibrationAtIntentExtraFieldName);
            if (stringExtra == null) {
                stopSelf();
                return START_NOT_STICKY;
            }

            final DateTime endVibrationServiceAt = DateTime.parse(stringExtra, ISODateTimeFormat.dateTime());

            if (endVibrationServiceAt.isAfterNow()) {
                run();
            } else
                ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_NO_CREATE));
        }
        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void run() {
        Log.d(TAG, "enter - run");

        //+1 due to first element == 0
        //*2 because every toogle requires a inverse pattern and real pattern time
        //*100 because the duration is in 100ms steps and the vibration is counted in 1ms steps
        final int arraySize = (duration * 100 * 2) / BreathPrayConstants.vibrationInterval + 1;
        final long[] array = new long[arraySize];
        array[0] = 0;
        long togglingIntervalToInverseinterval = pattern;
        for (int i = 1; i < arraySize; i++) {
            array[i] = togglingIntervalToInverseinterval;
            togglingIntervalToInverseinterval = BreathPrayConstants.vibrationInterval - togglingIntervalToInverseinterval;
        }

        if (acousticNotificationActive) {
            if (!volumeActive) {
                final AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
                volume = ((float) audioManager.getStreamVolume(AudioManager.STREAM_RING)) / ((float) audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
            }
            final MediaPlayer mp = new MediaPlayer();
            try {
                mp.setDataSource(this, Uri.parse(acousticNotificationUri));
                mp.setLooping(false);
                mp.setVolume(volume, volume);
                mp.prepare();
                mp.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Intent i = new Intent(BreathPrayConstants.updateNextVibrationTimeAction);
        i.putExtra(BreathPrayConstants.nextVibrationAtIntentExtraFieldName, ISODateTimeFormat.dateTime().print(nextVibration));
        sendBroadcast(i);

        vibrator.vibrate(array, -1);


        Log.d(TAG, "exit - run");
    }
}
