package re.breathpray.com.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import re.breathpray.com.R;
import re.breathpray.com.ViewHelper;

/**
 * Date: 31.07.14
 */
public class FirstStartupActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.firststartup);
    }

    public void onExitClicked(View view){
        ViewHelper.pulsView(view);
        finish();
    }
}
