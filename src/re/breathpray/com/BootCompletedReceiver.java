package re.breathpray.com;

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
        Log.w(TAG, "starting service breathpray");
        Intent intent = new Intent(context, VibrationRepeaterService.class);
        intent.setAction(BreathPrayConstants.defaultVibrationRepeaterServiceAction);
        intent.addCategory(BreathPrayConstants.defaultCategory);
        context.startService(intent);
    }
}
