package re.breathpray.com.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import re.breathpray.com.BreathPrayConstants;
import re.breathpray.com.services.VibrationRepeaterService;

public class BootCompletedReceiver extends BroadcastReceiver {

    final static String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(Context context, Intent arg1) {
        Log.w(TAG, "starting service breathpray");
        Intent intent = new Intent(context, VibrationRepeaterService.class);
        intent.setAction(BreathPrayConstants.defaultVibrationRepeaterServiceAction);
        intent.addCategory(BreathPrayConstants.defaultCategory);
        intent.putExtra(BreathPrayConstants.startVibrationIntentExtraFieldName, true);
        context.startService(intent);
    }
}
