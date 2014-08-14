package re.breathpray.com;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

public class DeviceHasNoVibrationDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.deviceHasNoVibrator)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(BreathPrayConstants.PREFERENCEFILE, Context.MODE_PRIVATE).edit();
                        editor.putBoolean(BreathPrayConstants.keyAcousticIsActive,true);
                        while (!editor.commit());
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}});
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
