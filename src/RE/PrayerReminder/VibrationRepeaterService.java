package RE.PrayerReminder;

import java.util.Date;
import java.util.Timer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

/**
 * @author: Eisl
 * Date: 01.05.14
 * Time: 18:36
 * @version: 1.0.0
 */
public class VibrationRepeaterService extends Service{


    private static final String TAG = "VibrationRepeaterService";
    private IBinder mBinder = new LocalBinder();
    private ConfigurationManager configurationManager = new ConfigurationManager();
    private Timer timer;

    public void scheduleVibrationRepeater(){
        this.scheduleVibrationRepeater(configurationManager.getNextVibrate()-System.currentTimeMillis() >= 0 ? configurationManager.getNextVibrate()-System.currentTimeMillis() : 0);
    }

    /**
     *
     * @param time delay in milliseconds
     */
    public void scheduleVibrationRepeater(long time){
        if(timer != null){
           timer.cancel();
           timer.purge();
        }
        timer = new Timer();
        configurationManager.setNextVibrate(System.currentTimeMillis() + time);
        Log.d(TAG, "starting timer - next vibrate at: " + new Date(configurationManager.getNextVibrate()));
        VibrationRepeater vibrationRepeaterThread = new VibrationRepeater((Vibrator) getSystemService(Context.VIBRATOR_SERVICE), configurationManager);

        timer.scheduleAtFixedRate(vibrationRepeaterThread,time,(long) configurationManager.getRepeatTime()*1000*60);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "starting service...");

        final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //don't start service if cellphone has no vibrator
        if(vibrator == null || !vibrator.hasVibrator()){
            Log.d(TAG, "cellphone isn't able to vibrate");
            return;
        }

        configurationManager = new ConfigurationManager();
        mBinder = new LocalBinder();
        timer = null;

        SharedPreferences preferences = this.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
        configurationManager.setVibrationTime(preferences.getInt(getString(R.string.vibrationTime), 16));
        configurationManager.setVibrationStrength(preferences.getInt(getString(R.string.vibrationStrengthValue), 150));
        configurationManager.setRepeatTime(preferences.getInt(getString(R.string.vibrationCycleTime), 10));
        configurationManager.setEndHour(preferences.getInt(getString(R.string.VibrationEndHour), 22));
        configurationManager.setEndMinute(preferences.getInt(getString(R.string.VibrationEndMinute), 0));
        configurationManager.setStartHour(preferences.getInt(getString(R.string.VibrationStartHour), 6));
        configurationManager.setStartMinute(preferences.getInt(getString(R.string.VibrationStartMinute), 0));
        configurationManager.setTakeABreak(preferences.getInt(getString(R.string.takeABreakValue), 60));
        configurationManager.setLastVibrate(preferences.getLong(getString(R.string.lastVibrate), System.currentTimeMillis()));
        configurationManager.setNextVibrate(preferences.getLong(getString(R.string.nextVibrate),System.currentTimeMillis() + configurationManager.getRepeatTime()*1000*60));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        this.scheduleVibrationRepeater();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy(){
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    public void setRepeatTime(final int newVal) {
        Log.d(TAG,"changed repeat time: "+newVal);
        configurationManager.setRepeatTime(newVal);
        configurationManager.setNextVibrate(configurationManager.getLastVibrate() + newVal*1000*60 >= System.currentTimeMillis() ? configurationManager.getLastVibrate() + newVal*1000*60 : System.currentTimeMillis());
    }

    public void setVibrationTime(final int newVal){
        Log.d(TAG,"changed vibration time: "+newVal);
        configurationManager.setVibrationTime(newVal);
    }

    public void setStartTime(final int startHour, final int startMinute) {
        configurationManager.setStartHour(startHour);
        configurationManager.setStartMinute(startMinute);

    }

    public void setEndTime(final int endHour, final int endMinute) {
        configurationManager.setEndHour(endHour);
        configurationManager.setEndMinute(endMinute);
    }

    public void setVibrationStrength(int progress) {
        configurationManager.setVibrationStrength(progress);

    }

    public void setTakeABreak(int newVal) {
        configurationManager.setTakeABreak(newVal);
    }

    public long getNextVibrate() {
        return configurationManager.getNextVibrate();
    }

    public long getLastVibrate() {
        return configurationManager.getLastVibrate();
    }

    public class LocalBinder extends Binder{
        VibrationRepeaterService getService(){
            return VibrationRepeaterService.this;
        }
    }
}
