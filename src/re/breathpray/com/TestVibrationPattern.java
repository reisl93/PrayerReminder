package re.breathpray.com;

import android.os.Vibrator;
import android.util.Log;

/**
 * @version 1.0
 * @author: Eisl
 * Date: 16.05.14
 * Time: 18:12
 */
public class TestVibrationPattern implements Runnable {

    private static final String TAG = "TestVibrationPattern";
    private long interval;
    final private Vibrator vibrator;
    private boolean isRunning;

    public TestVibrationPattern(long interval,final Vibrator vibrator) {
        this.interval = interval;
        this.vibrator = vibrator;
        isRunning = true;

    }

    @Override
    public void run() {
        while(isRunning){
            if(interval <= 190)
                vibrator.vibrate(new long[]{0,interval, 200 - interval}, -1);
            else
                vibrator.vibrate(new long[] {0, 200},-1);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void setRunning(boolean running){
        this.isRunning = running;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }
}
