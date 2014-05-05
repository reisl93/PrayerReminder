package RE.PrayerReminder;

import android.os.Vibrator;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimerTask;

/**
 * @author: Eisl
 * Date: 01.05.14
 * Time: 19:25
 * @version: 1.0.0
 */
public class VibrationRepeater extends TimerTask {

    private static final String TAG = "VibrationRepeater";
    private Vibrator vibrator;
    private ConfigurationManager configurationManager;

    public VibrationRepeater(Vibrator vibrator, ConfigurationManager configurationManager) {
        this.vibrator = vibrator;
        this.configurationManager = configurationManager;
    }

    @Override
    public void run() {

        Calendar now = GregorianCalendar.getInstance();
        Calendar start = GregorianCalendar.getInstance();
        Calendar end = GregorianCalendar.getInstance();
        start.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), configurationManager.getStartHour(), configurationManager.getStartMinute(), 0);
        end.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH), configurationManager.getEndHour(), configurationManager.getEndMinute(), 0);

        if(start.before(now) && end.after(now)){
            Log.d(TAG, "vibrated - strength: "+configurationManager.getVibrationStrength() + " - 1/20 seconds * " + configurationManager.getVibrationTime());
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

        }
    }
}
