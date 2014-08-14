package re.breathpray.com.receivers;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import re.breathpray.com.BreathPrayConstants;

/**
 * this class creates and cancels dummy pendingIntents according to the current RingerMode.
 * If the device is on {@link AudioManager#RINGER_MODE_SILENT} the a pendingIntent with the intent action {@link BreathPrayConstants#temporaryDeactivateVibrationService} exists.
 * If the device is on {@link AudioManager#RINGER_MODE_VIBRATE} the a pendingIntent with the intent action {@link BreathPrayConstants#temporaryDeactivateAcousticNotificationService} exists.
 * If none of the above pendingIntent exists the phone is on {@link AudioManager#RINGER_MODE_NORMAL}
 */
public class RingerModeStateChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {

        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        PendingIntent pendingIntent;

        switch (am.getRingerMode()) {
            //passively deactivate phone
            case AudioManager.RINGER_MODE_SILENT:
                PendingIntent.getService(context,0,new Intent(BreathPrayConstants.temporaryDeactivateVibrationService),PendingIntent.FLAG_UPDATE_CURRENT);
                break;

            //create acoustic notification restriction
            case AudioManager.RINGER_MODE_VIBRATE:
                PendingIntent.getService(context,0,new Intent(BreathPrayConstants.temporaryDeactivateAcousticNotificationService),PendingIntent.FLAG_UPDATE_CURRENT);
                pendingIntent = PendingIntent.getService(context,0,new Intent(BreathPrayConstants.temporaryDeactivateVibrationService),PendingIntent.FLAG_NO_CREATE);
                if(pendingIntent != null)
                    pendingIntent.cancel();
                break;

            //cancel all restrictions
            case AudioManager.RINGER_MODE_NORMAL:
                pendingIntent = PendingIntent.getService(context,0,new Intent(BreathPrayConstants.temporaryDeactivateAcousticNotificationService),PendingIntent.FLAG_NO_CREATE);
                if(pendingIntent != null)
                    pendingIntent.cancel();

                pendingIntent = PendingIntent.getService(context,0,new Intent(BreathPrayConstants.temporaryDeactivateVibrationService),PendingIntent.FLAG_NO_CREATE);
                if(pendingIntent != null)
                    pendingIntent.cancel();
                break;
        }
    }
}
