package re.breathpray.com;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.TimeZone;

/**
 * Date: 01.05.14
 * Time: 18:36
 */
public class VibrationRepeaterService extends Service{


    private static final String TAG = "VibrationRepeaterService";

    private VibrationAttributesManager vibrationAttributesManager;
    private AlarmManager alarmManager;


    /**
     * The intentExtra {@link BreathPrayConstants#startVibrationIntentExtraFieldName startVibrationIntentExtraFieldName} must be set <code>true</code> to start the service.
     * The intentExtra {@link BreathPrayConstants#endVibrationIntentExtraFieldName} must be set <code>true</code> to start the service.
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
            this.fillDayWithPendingVibrations();
            kickOffVibrationRepeaterService();
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
     */
    private void fillDayWithPendingVibrations(){

        //AlarmManager works with UTC-times! all calculations are done in UTC!
        DateTime scheduleVibrationAt =  DateTime.now(DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));
        //midnight of the locale day
        final DateTime midnight = scheduleVibrationAt.withTimeAtStartOfDay();//.toDateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));
        final DateTime currentWeekdayVibrationStart = midnight.plus(vibrationAttributesManager.getStart());
        final DateTime currentWeekdayVibrationEnd = midnight.plus(vibrationAttributesManager.getEnd());

        final int repeatTime = this.vibrationAttributesManager.getRepeatTime();
        final int duration = vibrationAttributesManager.getVibrationDuration();

        //current time is before start?
        if(scheduleVibrationAt.isBefore(currentWeekdayVibrationStart))
            scheduleVibrationAt = currentWeekdayVibrationStart.toDateTime(DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")));

        //requestCode for the PendingIntent
        int requestCode = 0;

        //while the Day is not filled!
        while (scheduleVibrationAt.isBefore(currentWeekdayVibrationEnd)){

            alarmManager.set(AlarmManager.RTC_WAKEUP,scheduleVibrationAt.getMillis(), createCyclingServicePendingIntent(requestCode, duration));

            //increase scheduletime and requestCode
            scheduleVibrationAt = scheduleVibrationAt.plusMinutes(repeatTime);
            requestCode++;
        }

    }

    /**
     * stops all currently scheduled vibrations
     */
    private void stopCurrentlyPendingVibrations(){

        //calculate number of vibration that are scheduled at current day
        final int numberOfVibrationsPerDay = (vibrationAttributesManager.getStart() - vibrationAttributesManager.getEnd())
                / BreathPrayConstants.numberOfGridPerHour/ BreathPrayConstants.gridInMinutes/1000;
        final int dummyDuration = 0;

        for(int requestCode = 0; requestCode < numberOfVibrationsPerDay; requestCode++)
            alarmManager.cancel(createCyclingServicePendingIntent(requestCode,dummyDuration));
    }

    /**
     * @return a standardised {@link PendingIntent} to schedule or stop this
     */
    private PendingIntent createVibrationRepeaterServicePendingIntent(){

        final Intent intent = new Intent(this,VibrationRepeaterService.class);
        intent.setAction(BreathPrayConstants.defaultVibrationRepeaterServiceAction);
        intent.addCategory(BreathPrayConstants.defaultCategory);

        intent.putExtra(BreathPrayConstants.startVibrationIntentExtraFieldName,true);

        return PendingIntent.getService(this,0,intent,0);
    }

    private void stopVibrationRepeaterService(){
        alarmManager.cancel(createVibrationRepeaterServicePendingIntent());
    }

    private void kickOffVibrationRepeaterService(){
        alarmManager.set(AlarmManager.RTC_WAKEUP, DateTime.now().withTimeAtStartOfDay().plusDays(1).getMillis(), createVibrationRepeaterServicePendingIntent());
    }

    private PendingIntent createCyclingServicePendingIntent(int number, int duration){
        final Intent intent = new Intent(this,ActiveVibrationService.class);
        intent.setAction(BreathPrayConstants.defaultCyclicVibrationServiceAction);
        intent.addCategory(BreathPrayConstants.defaultCategory);
        //config vibration
        intent.putExtra(BreathPrayConstants.intervalIntentExtraFieldName, BreathPrayConstants.vibrationCycleDuration);
        intent.putExtra(BreathPrayConstants.durationIntentExtraFieldName,duration);

        return PendingIntent.getService(this,number, intent,0);
    }
}
