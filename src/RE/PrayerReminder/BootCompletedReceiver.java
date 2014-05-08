package RE.PrayerReminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author: Eisl
 * Date: 01.05.14
 * Time: 19:06
 * @version: 1.0.0
 */
public class BootCompletedReceiver extends BroadcastReceiver {

    final static String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.w(TAG, "starting service PrayerReminder");
        Intent intent = new Intent(context, VibrationRepeaterService.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startService(intent);
    }
}
