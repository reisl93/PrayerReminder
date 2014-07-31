package re.breathpray.com;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
        finish();
    }
}
