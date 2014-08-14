package re.breathpray.com.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.*;
import android.util.Log;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import re.breathpray.com.BreathPrayConstants;
import re.breathpray.com.VibrationAttributesManager;
import re.breathpray.com.receivers.RingerModeStateChangeReceiver;

/**
 * Date: 01.05.14
 * Time: 18:36
 */
public class VibrationRepeaterService extends Service{


    private static final String TAG = "VibrationRepeaterService";

    private VibrationAttributesManager vibrationAttributesManager;
    private AlarmManager alarmManager;


    /**
     * The intentExtra {@link re.breathpray.com.BreathPrayConstants#startVibrationIntentExtraFieldName startVibrationIntentExtraFieldName} must be set <code>true</code> to start the service.
     * The intentExtra {@link re.breathpray.com.BreathPrayConstants#endVibrationIntentExtraFieldName} must be set <code>true</code> to start the service.
     */
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "creating service...");

        //Load basic informations
        vibrationAttributesManager = new VibrationAttributesManager(this);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        vibrationAttributesManager.reloadCurrentData();

        if(intent.getBooleanExtra(BreathPrayConstants.startVibrationIntentExtraFieldName,false) && vibrationAttributesManager.isAppIsActive()){
            this.stopCurrentlyPendingVibrations();
            this.startOffCyclicReminders(intent.getIntExtra(BreathPrayConstants.breakTimeIntentExtraFieldName, 0));
            kickOffVibrationRepeaterService();
            final Context context = this;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    new RingerModeStateChangeReceiver().onReceive(context, null);
                }
            });
        } else if (intent.getBooleanExtra(BreathPrayConstants.endVibrationIntentExtraFieldName,false) || !vibrationAttributesManager.isAppIsActive()){
            stopCurrentlyPendingVibrations();
            stopVibrationRepeaterService();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Fills the day with vibration {@link AlarmManager} alarms with type: {@link AlarmManager#RTC_WAKEUP}.
     * First vibration is scheduled at start-time of current weekday or, if current daytime is more advanced, now.
     * Stops at end-time of current weekday
     * @param withDelay delays the start in minutes
     */
    private void startOffCyclicReminders(final int withDelay){

        //AlarmManager works with UTC-times! all calculations are done in UTC!
        DateTime scheduleVibrationAt = DateTime.now();
        //midnight of the locale day
        final DateTime midnight = scheduleVibrationAt.withTimeAtStartOfDay();
        final DateTime currentWeekdayVibrationStart = midnight.plus(vibrationAttributesManager.getStart());
        final DateTime currentWeekdayVibrationEnd = midnight.plus(vibrationAttributesManager.getEnd());

        final int repeatTime = this.vibrationAttributesManager.getRepeatTime()*1000*60; //1000*60 == convert to minutes
        final int duration = vibrationAttributesManager.getDuration();
        final int pattern = vibrationAttributesManager.getPattern();
        final boolean volumeActive = vibrationAttributesManager.isVolumeActive();
        final float volume = vibrationAttributesManager.getVolume();
        final boolean acousticNotificationActive =
                vibrationAttributesManager.isAcousticNotificationActive() &&
                vibrationAttributesManager.getPhoneRingerMode() == AudioManager.RINGER_MODE_NORMAL;
        final String acousticNotificationUri = vibrationAttributesManager.getAcousticNotificationUri();


        //current time is before start?
        if (scheduleVibrationAt.isBefore(currentWeekdayVibrationStart))
            scheduleVibrationAt = currentWeekdayVibrationStart;

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                scheduleVibrationAt.plusMillis(500).plusMinutes(withDelay).getMillis(),
                repeatTime,
                createCyclingServicePendingIntent(duration, pattern, currentWeekdayVibrationEnd,PendingIntent.FLAG_UPDATE_CURRENT, acousticNotificationActive, volumeActive, volume, acousticNotificationUri));
    }

    /**
     * stops all currently scheduled vibrations
     */
    private void stopCurrentlyPendingVibrations(){
        alarmManager.cancel(createCyclingServicePendingIntent(0, 0, null, PendingIntent.FLAG_CANCEL_CURRENT, false, false, 0, null));
    }

    /**
     * @return a standardised {@link PendingIntent} to schedule or stop this
     */
    private PendingIntent createVibrationRepeaterServicePendingIntent(){

        final Intent intent = new Intent(this,VibrationRepeaterService.class);
        intent.setAction(BreathPrayConstants.defaultVibrationRepeaterServiceAction);
        intent.addCategory(BreathPrayConstants.defaultCategory);

        intent.putExtra(BreathPrayConstants.startVibrationIntentExtraFieldName,true);

        return PendingIntent.getService(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void stopVibrationRepeaterService(){
        alarmManager.cancel(createVibrationRepeaterServicePendingIntent());
    }

    private void kickOffVibrationRepeaterService() {

        DateTime.now().toString();

        alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                DateTime.now()
                        .withTimeAtStartOfDay()
                        .plusDays(1)
                        .plusMinutes(1)
                        .getMillis(),
                createVibrationRepeaterServicePendingIntent());
    }

    private PendingIntent createCyclingServicePendingIntent(int duration, int pattern, DateTime endTime, int flags, boolean notificationActive, boolean notificationVolumeActive, float notificationVolume, String notificationUri){
        final Intent intent = new Intent(this,ActiveVibrationService.class);
        intent.setAction(BreathPrayConstants.defaultCyclicVibrationServiceAction);
        intent.addCategory(BreathPrayConstants.defaultCategory);
        //config vibration
        intent.putExtra(BreathPrayConstants.patternIntentExtraFieldName, pattern);
        intent.putExtra(BreathPrayConstants.durationIntentExtraFieldName, duration);
        intent.putExtra(BreathPrayConstants.endVibrationAtIntentExtraFieldName, ISODateTimeFormat.dateTime().print(endTime));
        intent.putExtra(BreathPrayConstants.acousticNotificationActiveIntentExtraFieldName, notificationActive);
        intent.putExtra(BreathPrayConstants.acousticVolumeIntentExtraFieldName, notificationVolume);
        intent.putExtra(BreathPrayConstants.acousticUniqueVolumeActiveIntentExtraFieldName, notificationVolumeActive);
        intent.putExtra(BreathPrayConstants.acousticUriIntentExtraFieldName, notificationUri);

        return PendingIntent.getService(this, 0, intent, flags);
    }
}
