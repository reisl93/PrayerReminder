package RE.PrayerReminder;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.util.Log;

/**
 * @author: Eisl
 * Date: 01.05.14
 * Time: 18:36
 * @version: 1.0.0
 */
public class VibrationRepeaterService extends Service implements Runnable, Observeable{


    private static final String TAG = "VibrationRepeaterService";
    private IBinder mBinder = new LocalBinder();
    private ConfigurationManager configurationManager = new ConfigurationManager();
    private Handler handler;
    private Vibrator vibrator;
    private List<Observer> observers;

    public void scheduleNextVibration(){
        if(!configurationManager.isAppIsActive())
            return;

        Log.d(TAG, "starting timer - next vibrate at: " + new Date(configurationManager.getNextVibrate()));
        notifyAllObservers();
        this.handler.removeCallbacks(this);
        this.handler.postAtTime(this, configurationManager.getNextVibrate() - (System.currentTimeMillis() - SystemClock.uptimeMillis()));
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG, "starting service...");

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        configurationManager = new ConfigurationManager();
        mBinder = new LocalBinder();
        handler = new Handler();
        observers = new LinkedList<Observer>();

        SharedPreferences preferences = this.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
        configurationManager.setAppIsActive(preferences.getBoolean(getString(R.string.keyIsAppActive),true));
        configurationManager.setVibrationTime(preferences.getInt(getString(R.string.keyVibrationDuration), 16));
        configurationManager.setVibrationStrength(preferences.getInt(getString(R.string.keyVibrationPower), 150));
        configurationManager.setRepeatTime(preferences.getInt(getString(R.string.keyVibrationRepeatTime), 10));
        configurationManager.setEndHour(preferences.getInt(getString(R.string.keyVibrationEndHour), 22));
        configurationManager.setEndMinute(preferences.getInt(getString(R.string.keyVibrationEndMinute), 0));
        configurationManager.setStartHour(preferences.getInt(getString(R.string.keyVibrationStartHour), 6));
        configurationManager.setStartMinute(preferences.getInt(getString(R.string.keyVibrationStartMinute), 0));
        configurationManager.setTakeABreak(preferences.getInt(getString(R.string.keyTakeABreakValue), 60));
        configurationManager.setLastVibrate(preferences.getLong(getString(R.string.keyLastVibrate), System.currentTimeMillis()));
        configurationManager.setNextVibrate(preferences.getLong(getString(R.string.keyNextVibrate),System.currentTimeMillis() + configurationManager.getRepeatTime()*1000*60));

        //don't start service if cellphone has no vibrator
        if(vibrator == null || !vibrator.hasVibrator()){
            Log.d(TAG, "cellphone isn't able to vibrate");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        this.scheduleNextVibration();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy(){
    }

    public void setRepeatTime(final int newVal) {
        Log.d(TAG,"changed repeat time: "+newVal);
        configurationManager.setRepeatTime(newVal);
        configurationManager.setNextVibrate(configurationManager.getLastVibrate() + newVal*1000*60 >= System.currentTimeMillis() ? configurationManager.getLastVibrate() + newVal*1000*60 : System.currentTimeMillis() + newVal*1000*60);
        scheduleNextVibration();
        notifyAllObservers();
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

    public void takeABreak() {
        this.configurationManager.setNextVibrate(configurationManager.getNextVibrate()+configurationManager.getTakeABreakTime()*1000*60);
        scheduleNextVibration();
        notifyAllObservers();

    }

    @Override
    public void run() {
        if(System.currentTimeMillis() >= configurationManager.getNextVibrate()){
            configurationManager.setLastVibrate(System.currentTimeMillis());

            if(configurationManager.getVibrationStrength() <= 190) {
                vibrator.vibrate(new long[] {0, configurationManager.getVibrationStrength(), 200-configurationManager.getVibrationStrength()}, 0);

                try {
                    Thread.sleep(configurationManager.getVibrationTime()*50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                vibrator.cancel();
            } else {
                vibrator.vibrate(configurationManager.getVibrationTime()*50);
            }
            //set next vibration
            configurationManager.setNextVibrate(configurationManager.getLastVibrate()+configurationManager.getRepeatTime()*1000*60);
            //if it isn't in the future --> set it to the future
            if(System.currentTimeMillis() >= configurationManager.getNextVibrate())
                configurationManager.setNextVibrate(System.currentTimeMillis() + configurationManager.getRepeatTime()*1000*60);
            //if it is out of the boundaries --> set it to tomorrow
            if(configurationManager.getNextVibrate() >= configurationManager.getNextVibrate() - configurationManager.getNextVibrate() % (1000 * 3600 * 24) +
                                                        configurationManager.getEndHour()*1000*3600 + configurationManager.getEndMinute()*1000){
                configurationManager.setNextVibrate(
                                configurationManager.getNextVibrate() - configurationManager.getNextVibrate() % (1000 * 3600 * 24) +    //set to 00:00 AM of current day
                                configurationManager.getStartHour()*1000*3600 + configurationManager.getStartMinute()*1000 +            //set next vibration to start time
                                1000*3600*24);                                                                                          //add one day so vibration is tomorrow
            }
        }
        //set next Vibration
        this.scheduleNextVibration();
    }


    @Override
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    public void notifyAllObservers(){
        for(final Observer o : observers){
            o.update();
        }
    }

    public void setAppIsActive(boolean checked) {
        this.configurationManager.setAppIsActive(checked);
    }

    public class LocalBinder extends Binder{
        VibrationRepeaterService getService(){
            return VibrationRepeaterService.this;
        }
    }
}
