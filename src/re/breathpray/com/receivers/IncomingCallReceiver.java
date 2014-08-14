package re.breathpray.com.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import re.breathpray.com.BreathPrayConstants;


public class IncomingCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context mContext, Intent intent)
    {
        try
        {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);


            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                //only reactivate if phone isn't on silent mode
                if(((AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE)).getRingerMode() != AudioManager.RINGER_MODE_SILENT){
                    final PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, new Intent(BreathPrayConstants.temporaryDeactivateVibrationService), PendingIntent.FLAG_NO_CREATE);
                    if(pendingIntent != null)
                        pendingIntent.cancel();
                }

            } else if(state.equals(TelephonyManager.EXTRA_STATE_RINGING ) || state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                PendingIntent.getService(mContext, 0, new Intent(BreathPrayConstants.temporaryDeactivateVibrationService), PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }
}
