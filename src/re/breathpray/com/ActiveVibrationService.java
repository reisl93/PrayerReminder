package re.breathpray.com;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;

/**
 * Date: 16.05.14
 */
public class ActiveVibrationService extends Service {

    private static final String TAG = "ActiveVibrationService";
    private int interval;
    private int duration;
    private Vibrator vibrator;
    private IBinder mBinder;

    public void setInterval(int interval) {
        this.interval = interval;
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
        this.interval = BreathPrayConstants.vibrationCycleDuration;
        this.duration = 10;
        this.mBinder = new LocalBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);

        interval = intent.getIntExtra(BreathPrayConstants.intervalIntentExtraFieldName, BreathPrayConstants.vibrationCycleDuration);
        duration = intent.getIntExtra(BreathPrayConstants.durationIntentExtraFieldName,10);

        run();

        return START_STICKY;
    }

    public void run() {
        Log.d(TAG,"enter - vibrationCycle");

        //+1 due to first element == 0
        //*2 because every toogle requires a inverse interval and real interval time
        //*100 because the duration is in 100ms steps and the vibration is counted in 1ms steps
        final int arraySize = (duration*100*2) / BreathPrayConstants.vibrationCycleDuration + 1;
        final long[] array = new long[arraySize];
        array[0] = 0;
        long togglingIntervalToInverseinterval = interval;
        for(int i = 1; i < arraySize; i++){
            array[i] = togglingIntervalToInverseinterval;
            togglingIntervalToInverseinterval = BreathPrayConstants.vibrationCycleDuration - togglingIntervalToInverseinterval;
        }

        vibrator.vibrate(array,-1);

        Log.d(TAG,"exit - vibrationCycle");
    }

    public class LocalBinder extends Binder {
        ActiveVibrationService getService(){
            return ActiveVibrationService.this;
        }
    }
}
