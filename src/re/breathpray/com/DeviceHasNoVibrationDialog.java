package re.breathpray.com;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created with IntelliJ IDEA.
 * User: Eisl
 * Date: 31.07.14
 * Time: 13:38
 * To change this template use File | Settings | File Templates.
 */
public class DeviceHasNoVibrationDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.deviceHasNoVibrator)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO set acoustic
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO nothing?? check docu
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
