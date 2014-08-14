package re.breathpray.com;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Date: 16.05.14
 */
public class ActiveVibrationService extends Service {

    private static final String TAG = "ActiveVibrationService";
    private int pattern;
    private int volume;
    private int duration;
    private Vibrator vibrator;
    private IBinder mBinder;

    public void setPattern(int pattern) {
        this.pattern = pattern;
        run();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onCreate(){
        super.onCreate();
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        this.pattern = BreathPrayConstants.vibrationInterval;
        this.duration = 10;
        this.mBinder = new LocalBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        pattern = intent.getIntExtra(BreathPrayConstants.patternIntentExtraFieldName, 150);
        duration = intent.getIntExtra(BreathPrayConstants.durationIntentExtraFieldName,10);

        //is still in time?
        final String stringExtra = intent.getStringExtra(BreathPrayConstants.endVibrationAtIntentExtraFieldName);
        if (stringExtra == null)
            return START_STICKY;
        final DateTime endVibrationServiceAt = DateTime.parse(stringExtra, ISODateTimeFormat.dateTime());
        if(endVibrationServiceAt.isAfterNow()) {
            run();
            intent.putExtra(BreathPrayConstants.lastVibrationAtIntentExtraFieldName,DateTime.now().toString(ISODateTimeFormat.dateTime()));
            PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else
            ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getService(this,0,intent,PendingIntent.FLAG_NO_CREATE));

        return START_STICKY;
    }

    public void run() {
        Log.d(TAG,"enter - vibrationCycle");

        //+1 due to first element == 0
        //*2 because every toogle requires a inverse pattern and real pattern time
        //*100 because the duration is in 100ms steps and the vibration is counted in 1ms steps
        final int arraySize = (duration*100*2) / BreathPrayConstants.vibrationInterval + 1;
        final long[] array = new long[arraySize];
        array[0] = 0;
        long togglingIntervalToInverseinterval = pattern;
        for(int i = 1; i < arraySize; i++){
            array[i] = togglingIntervalToInverseinterval;
            togglingIntervalToInverseinterval = BreathPrayConstants.vibrationInterval - togglingIntervalToInverseinterval;
        }

        vibrator.vibrate(array,-1);


        Log.d(TAG,"exit - vibrationCycle");
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public class LocalBinder extends Binder {
        ActiveVibrationService getService(){
            return ActiveVibrationService.this;
        }
    }
}
