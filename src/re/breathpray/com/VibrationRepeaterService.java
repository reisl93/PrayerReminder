package re.breathpray.com;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.util.Log;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;

/**
 * @author: Eisl
 * Date: 01.05.14
 * Time: 18:36
 * @version: 1.0.0
 */
public class VibrationRepeaterService extends Service{


    private static final String TAG = "VibrationRepeaterService";

    private ConfigurationManager configurationManager = new ConfigurationManager(this);
    private AlarmManager alarmManager;

    /**
     * {@value #startVibrationIntentExtraFieldName}
     */
    public final static String startVibrationIntentExtraFieldName = "startVibration";
    /**
     * {@value #endVibrationIntentExtraFieldName}
     */
    public final static String endVibrationIntentExtraFieldName = "endVibration";


    /**
     * The intentExtra {@link #startVibrationIntentExtraFieldName} must be set <code>true</code> to start the service.
     * The intentExtra {@link #endVibrationIntentExtraFieldName} must be set <code>true</code> to start the service.
     */
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "creating service...");

        //Load basic informations
        configurationManager = new ConfigurationManager(this);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        SharedPreferences preferences = this.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
        configurationManager.setAppIsActive(preferences.getBoolean(getString(R.string.keyIsAppActive), false));
        configurationManager.setVibrationDuration(preferences.getInt(getString(R.string.keyVibrationDuration), 16));
        configurationManager.setVibrationtimeOfACycle(preferences.getInt(getString(R.string.keyVibrationPower), 150));
        configurationManager.setRepeatTime(preferences.getInt(getString(R.string.keyVibrationRepeatTime), 10));
        configurationManager.setEndHour(preferences.getInt(getString(R.string.keyVibrationEndHour) + DateTime.now().getDayOfWeek(), 22));
        configurationManager.setEndMinute(preferences.getInt(getString(R.string.keyVibrationEndMinute) + DateTime.now().getDayOfWeek(), 0));
        configurationManager.setStartHour(preferences.getInt(getString(R.string.keyVibrationStartHour) + DateTime.now().getDayOfWeek(), 6));
        configurationManager.setStartMinute(preferences.getInt(getString(R.string.keyVibrationStartMinute) + DateTime.now().getDayOfWeek(), 0));
        configurationManager.setTakeABreak(preferences.getInt(getString(R.string.keyTakeABreakValue), 60));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        if(intent.getBooleanExtra(startVibrationIntentExtraFieldName,false) && configurationManager.isAppIsActive()){
            this.stopCurrentlyPendingVibrations();
            this.fillDayWithPendingVibrations();
            kickOffVibrationRepeaterService();
        } else if (intent.getBooleanExtra(endVibrationIntentExtraFieldName,false) || !configurationManager.isAppIsActive()){
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void fillDayWithPendingVibrations(){

        final long startOfVibrationsAtThatDay = configurationManager.getStartHour()*60 - configurationManager.getStartMinute() * 1000;
        final long numberOfVibrationsPerDay =
                (configurationManager.getEndHour()*60+configurationManager.getEndMinute() - startOfVibrationsAtThatDay)
                /configurationManager.getRepeatTime();
        //final long pendingIntentIDOffsetOfCurrentTime = numberOfVibrationsPerDay - DateTime.now().getMillis()
        for(int i = 0; i < numberOfVibrationsPerDay; i++)
            alarmManager.set(AlarmManager.RTC_WAKEUP,i*configurationManager.getRepeatTime()*60*1000 + startOfVibrationsAtThatDay+DateMidnight.now().getMillis(), createCyclingServicePendingIntent(i));

    }
    private void stopCurrentlyPendingVibrations(){

        final long startOfVibrationsAtThatDay = configurationManager.getStartHour()*60 - configurationManager.getStartMinute();
        final long numberOfVibrationsPerDay =
                (configurationManager.getEndHour()*60+configurationManager.getEndMinute() - startOfVibrationsAtThatDay)
                        /configurationManager.getRepeatTime();
        for(int i = 0; i < numberOfVibrationsPerDay; i++)
            alarmManager.cancel(createCyclingServicePendingIntent(i));
    }

    private PendingIntent createVibrationRepeaterServicePendingIntent(){
        final Intent intent = new Intent(this,VibrationRepeaterService.class);
        intent.setAction(ConfigurationManager.defaultVibrationRepeaterServiceAction);
        intent.addCategory(ConfigurationManager.defaultCategory);
        intent.putExtra(startVibrationIntentExtraFieldName,true);

        return PendingIntent.getService(this,0,intent,0);
    }

    private void stopVibrationRepeaterService(){
        alarmManager.cancel(createVibrationRepeaterServicePendingIntent());
    }

    private void kickOffVibrationRepeaterService(){
        alarmManager.set(AlarmManager.RTC_WAKEUP, DateMidnight.now().plusDays(1).getMillis(), createVibrationRepeaterServicePendingIntent());
    }

    private PendingIntent createCyclingServicePendingIntent(int number){
        final Intent intent = new Intent(this,ActiveVibrationService.class);
        intent.setAction(ConfigurationManager.defaultCyclicVibrationServiceAction);
        intent.addCategory(ConfigurationManager.defaultCategory);
        //config vibration
        intent.putExtra(ActiveVibrationService.intervalIntentExtraFieldName,configurationManager.getVibrationtimeOfACycle());
        intent.putExtra(ActiveVibrationService.durationIntentExtraFieldName,configurationManager.getVibrationDuration());
        intent.putExtra(ActiveVibrationService.isRunningIntentExtraFieldName,false);

        return PendingIntent.getService(this,number, intent,0);
    }
}
