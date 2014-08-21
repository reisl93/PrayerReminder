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
import re.breathpray.com.SettingsManager;

/**
 * Date: 01.05.14
 * Time: 18:36
 */
public class VibrationRepeaterService extends Service{


    private static final String TAG = "VibrationRepeaterService";

    private SettingsManager settingsManager;
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
        settingsManager = new SettingsManager(this);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        settingsManager.reloadCurrentData();

        if(intent != null && intent.getBooleanExtra(BreathPrayConstants.startVibrationIntentExtraFieldName,false) && settingsManager.isAppIsActive()){
            this.stopCurrentlyPendingVibrations();
            this.startOffCyclicReminders(intent.getIntExtra(BreathPrayConstants.breakTimeIntentExtraFieldName, 0));
            kickOffVibrationRepeaterService();
        } else if (intent != null && intent.getBooleanExtra(BreathPrayConstants.endVibrationIntentExtraFieldName,false) || !settingsManager.isAppIsActive()){
            stopCurrentlyPendingVibrations();
            stopVibrationRepeaterService();
        }
        stopSelf();
        return START_NOT_STICKY;
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
        final DateTime currentWeekdayVibrationStart = midnight.plus(settingsManager.getStart());
        final DateTime currentWeekdayVibrationEnd = midnight.plus(settingsManager.getEnd());

        final int repeatTime = this.settingsManager.getRepeatTime();
        final int duration = settingsManager.getDuration();
        final int pattern = settingsManager.getPattern();
        final boolean volumeActive = settingsManager.isVolumeActive();
        final float volume = settingsManager.getVolume();
        final boolean acousticNotificationActive =
                settingsManager.isAcousticNotificationActive() &&
                settingsManager.getPhoneRingerMode() == AudioManager.RINGER_MODE_NORMAL;
        final String acousticNotificationUri = settingsManager.getAcousticNotificationUri();


        //current time is before start?
        if (scheduleVibrationAt.isBefore(currentWeekdayVibrationStart))
            scheduleVibrationAt = currentWeekdayVibrationStart;

        scheduleVibrationAt = scheduleVibrationAt.plusMillis(500).plusMinutes(withDelay);

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                scheduleVibrationAt.getMillis(),
                repeatTime*1000*60,   //1000*60 == convert to minutes
                createCyclingServicePendingIntent(duration, pattern, currentWeekdayVibrationEnd,PendingIntent.FLAG_UPDATE_CURRENT, acousticNotificationActive, volumeActive, volume, acousticNotificationUri, repeatTime));

        Intent i = new Intent(BreathPrayConstants.updateNextVibrationTimeAction);
        i.putExtra(BreathPrayConstants.nextVibrationAtIntentExtraFieldName, ISODateTimeFormat.dateTime().print(scheduleVibrationAt));
        sendBroadcast(i);
    }

    /**
     * stops all currently scheduled vibrations
     */
    private void stopCurrentlyPendingVibrations(){
        alarmManager.cancel(createCyclingServicePendingIntent(0, 0, null, PendingIntent.FLAG_CANCEL_CURRENT, false, false, 0, null,0));
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

    private PendingIntent createCyclingServicePendingIntent(int duration, int pattern, DateTime endTime, int flags, boolean notificationActive, boolean notificationVolumeActive, float notificationVolume, String notificationUri, int repeatTime){
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
        intent.putExtra(BreathPrayConstants.repeatTimeIntentExtraFieldName, repeatTime);

        return PendingIntent.getService(this, 0, intent, flags);
    }
}
