package re.breathpray.com;


import android.os.Vibrator;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.*;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;

import java.util.HashMap;
import java.util.Map;

public class LauncherWindow extends Activity {

    private static final String TAG = "LauncherWindow";
    private static final String AD_UNIT_ID = "ca-app-pub-3956003081714684/6818330858";
    private AdView adView;
    // Time scrolled flag
    private final Context context = this;
    public final int textSizeInMM = 2;

    private Map<Integer, String> dayToInteger;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dayToInteger = new HashMap<Integer,String>(){{
            put(getString(R.string.monday).hashCode(),getString(R.string.monday));
            put(getString(R.string.tuesday).hashCode(),getString(R.string.tuesday));
            put(getString(R.string.wednesday).hashCode(),getString(R.string.wednesday));
            put(getString(R.string.thursday).hashCode(),getString(R.string.thursday));
            put(getString(R.string.friday).hashCode(),getString(R.string.friday));
            put(getString(R.string.saturday).hashCode(),getString(R.string.saturday));
            put(getString(R.string.sunday).hashCode(),getString(R.string.sunday));
        }};

        setContentView(R.layout.main);

        final int minRepeatTime = 1;
        final int minVibrationDuration = 0;
        final int minBreakTime = 1;

        SharedPreferences preferences = context.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);

        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_UNIT_ID);

        Log.d(TAG,"starting up main window of BreathPray");
        if(preferences.getBoolean(getString(R.string.keyFirstStart),false)){
            Log.d(TAG,"BreathPray - first startup");

            if(!((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).hasVibrator());
                //TODO create popup


            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(getString(R.string.keyFirstStart), true);
            editor.putInt(getString(R.string.keyVibrationPower), 150);
            editor.putInt(getString(R.string.keyVibrationRepeatTime), 10);
            editor.putInt(getString(R.string.keyVibrationDuration), 16);
            editor.putBoolean(getString(R.string.keyIsAppActive), true);
            editor.putInt(getString(R.string.keyTakeABreakValue), 60);
            /*
            for(int i = 0; i < ConfigurationManager.daysOfWeeks.length; i++){
                editor.putInt(getString(R.string.keyVibrationEndHour) + ConfigurationManager.daysOfWeeks[i], 22);
                editor.putInt(getString(R.string.keyVibrationEndMinute) + ConfigurationManager.daysOfWeeks[i],0);
                editor.putInt(getString(R.string.keyVibrationStartHour) + ConfigurationManager.daysOfWeeks[i],6);
                editor.putInt(getString(R.string.keyVibrationStartMinute) + ConfigurationManager.daysOfWeeks[i],0);
            }    */

            editor.apply();
        }

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.adView);
        linearLayout.addView(adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("9684DFFB83935CE920E945C32F975A12")
                .build();


        adView.loadAd(adRequest);


        final AbstractWheel repeatTimeWheel = (AbstractWheel) findViewById(R.id.repeatTime);
        NumericWheelAdapter repeatTimeWheelAdapter = new NumericWheelAdapter(this,minRepeatTime , 12*60, "%03d");
        repeatTimeWheelAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        repeatTimeWheelAdapter.setItemTextResource(R.id.text);
        repeatTimeWheel.setViewAdapter(repeatTimeWheelAdapter);
        repeatTimeWheel.setCurrentItem(preferences.getInt(getString(R.string.keyVibrationRepeatTime), 25) - minRepeatTime);
        repeatTimeWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                newValue += minRepeatTime;
                SharedPreferences preferences = context.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationRepeatTime), newValue);
                editor.apply();
            }
        });




        final AbstractWheel vibrationDurationWheel = (AbstractWheel) findViewById(R.id.vibrationDuration);
        ArrayWheelAdapter<String> vibrationDurationWheelAdapter = new ArrayWheelAdapter<String>(this, new String[]{
                "0,1","0,2","0,3","0,4","0,5","0,6","0,7","0,8","0,9","1,0",
                "1,1","1,2","1,3","1,4","1,5","1,6","1,7","1,8","1,9","2,0",
                "2,1","2,2","2,3","2,4","2,5","2,6","2,7","2,8","2,9","3,0",
                "3,1","3,2","3,3","3,4","3,5","3,6","3,7","3,8","3,9","4,0",
                "4,1","4,2","4,3","4,4","4,5","4,6","4,7","4,8","4,9","5,0",
                "5,1","5,2","5,3","5,4","5,5","5,6","5,7","5,8","5,9","6,0",
                "6,1","6,2","6,3","6,4","6,5","6,6","6,7","6,8","6,9","7,0",
                "7,1","7,2","7,3","7,4","7,5","7,6","7,7","7,8","7,9","8,0",
                "8,1","8,2","8,3","8,4","8,5","8,6","8,7","8,8","8,9","9,0",
                "9,1","9,2","9,3","9,4","9,5","9,6","9,7","9,8","9,9"
        });
        vibrationDurationWheelAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        vibrationDurationWheelAdapter.setItemTextResource(R.id.text);
        vibrationDurationWheel.setViewAdapter(vibrationDurationWheelAdapter);
        vibrationDurationWheel.setCurrentItem(preferences.getInt(getString(R.string.keyVibrationDuration), 25) - minVibrationDuration);
        vibrationDurationWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                newValue += minVibrationDuration;
                SharedPreferences preferences = context.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationDuration), newValue);
                editor.apply();
            }
        });

        final AbstractWheel breakTimeWheel = (AbstractWheel) findViewById(R.id.breakTimeWheel);

        NumericWheelAdapter breakTimeWheelAdapter = new NumericWheelAdapter(this,minBreakTime , 999, "%03d");
        breakTimeWheelAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        breakTimeWheelAdapter.setItemTextResource(R.id.text);
        breakTimeWheel.setViewAdapter(breakTimeWheelAdapter);
        breakTimeWheel.setCurrentItem(preferences.getInt(getString(R.string.keyTakeABreakValue), 60) - minBreakTime);
        breakTimeWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                newValue += minBreakTime;
                SharedPreferences preferences = context.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyTakeABreakValue), newValue);
                editor.apply();
            }
        });



        SeekBar seekBar = (SeekBar) this.findViewById(R.id.seekBar);
        seekBar.setMax(ConfigurationManager.vibrationCycleDuration);
        seekBar.setProgress(preferences.getInt(getString(R.string.keyVibrationPower),150));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            private boolean mBound = false;
            private ActiveVibrationService mService;

            private ServiceConnection mConnection = new ServiceConnection() {
                public void onServiceConnected(ComponentName className, IBinder service) {
                    ActiveVibrationService.LocalBinder binder = (ActiveVibrationService.LocalBinder) service;
                    mService = binder.getService();
                    mBound = true;
                    Log.d(TAG, "connection established to ActiveVibrationService");
                }

                public void onServiceDisconnected(ComponentName className) {
                    mBound = false;
                }
            };

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mBound)
                    mService.setInterval(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

                final Intent intent = new Intent(context,ActiveVibrationService.class);
                intent.setAction(ConfigurationManager.defaultCyclicVibrationServiceAction);
                intent.addCategory(ConfigurationManager.defaultCategory);
                intent.putExtra(ActiveVibrationService.intervalIntentExtraFieldName,seekBar.getProgress());
                intent.putExtra(ActiveVibrationService.durationIntentExtraFieldName, ConfigurationManager.vibrationCycleDuration);
                intent.putExtra(ActiveVibrationService.loopEndlessExecuteIntentExtraFieldName, true);

                bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mBound){
                    unbindService(mConnection);
                    mBound = false;
                }
                SharedPreferences preferences = context.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationPower), seekBar.getProgress());
                editor.apply();
            }
        });


        ToggleButton toggleButton = (ToggleButton) this.findViewById(R.id.toggleButton);

        toggleButton.setTextOff(getString(R.string.appIsNotActiveText));
        toggleButton.setTextOn(getString(R.string.appIsActiveText));
        toggleButton.setChecked(preferences.getBoolean(getString(R.string.keyIsAppActive), false));

        TextView textView = (TextView) this.findViewById(R.id.textViewRepeatTime);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeInMM);

        textView = (TextView) this.findViewById(R.id.textViewVibrateDuration);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeInMM);

        textView = (TextView) this.findViewById(R.id.textViewVibratePower);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeInMM);

        textView = (TextView) this.findViewById(R.id.textViewTakeABreak);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeInMM);

    }

    public void onDestroy(){

        final Intent intent = new Intent(this, VibrationRepeaterService.class);
        intent.putExtra(VibrationRepeaterService.startVibrationIntentExtraFieldName,true);
        this.startService(intent);

        if(adView != null)
            adView.destroy();

        super.onDestroy();
    }


    @Override
    public void onResume(){
        super.onResume();
        if(adView != null)
            adView.resume();
    }

    @Override
    public void onPause(){

        final Intent intent = new Intent(this, VibrationRepeaterService.class);
        intent.putExtra(VibrationRepeaterService.startVibrationIntentExtraFieldName,true);
        this.startService(intent);

        if(adView != null)
            adView.pause();
        super.onPause();
    }

    public void onToggleButtonClick(View view){
        ToggleButton toggleButton = (ToggleButton) view;

        SharedPreferences preferences = context.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getString(R.string.keyIsAppActive), toggleButton.isChecked());
        editor.commit();

        if(toggleButton.isChecked()) {
            final Intent intent = new Intent(this, VibrationRepeaterService.class);
            intent.putExtra(VibrationRepeaterService.startVibrationIntentExtraFieldName,true);
            this.startService(intent);
        } else {
            final Intent intent = new Intent(this, VibrationRepeaterService.class);
            intent.putExtra(VibrationRepeaterService.endVibrationIntentExtraFieldName,true);
            this.startService(intent);
        }
    }

    public void onTakeABreakClicked(View view){
        Log.d(TAG, "takeABreak was clicked");
        //TODO take a break
    }

    public void onMondayClicked(View view){
        Log.d(TAG, "monday was edited");
        createEditDayActivity(getString(R.string.monday));
    }

    public void onTuesdayClicked(View view){
        Log.d(TAG, "tuesday was edited");
        createEditDayActivity(getString(R.string.tuesday));
    }

    public void onWednesdayClicked(View view){
        Log.d(TAG, "wednesday was edited");
        createEditDayActivity(getString(R.string.wednesday));
    }

    public void onThursdayClicked(View view){
        Log.d(TAG, "thursday was edited");
        createEditDayActivity(getString(R.string.thursday));
    }

    public void onFridayClicked(View view){
        Log.d(TAG, "friday was edited");
        createEditDayActivity(getString(R.string.friday));
    }

    public void onSaturdayClicked(View view){
        Log.d(TAG, "saturday was edited");
        createEditDayActivity(getString(R.string.saturday));
    }

    public void onSundayClicked(View view){
        Log.d(TAG, "sunday was edited");
        createEditDayActivity(getString(R.string.sunday));
    }

    private void createEditDayActivity(String dayName) {
        Intent intent = new Intent(this, EditDayActivity.class);
        intent.setAction(ConfigurationManager.defaultActivityAction);
        intent.addCategory(ConfigurationManager.defaultCategory);
        //set the dayname to monday
        intent.putExtra(EditDayActivity.dayName, dayName);
        //set "monday"+"Start" to time where the app should shut down
        intent.putExtra("Start",
                context.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE).getInt(dayName+"Start",6*12));
        //set "monday"+"End" to time where the app should shut down
        intent.putExtra("End",
                context.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE).getInt(dayName+"End",22*12));
        startActivityForResult(intent, dayName.hashCode());
    }

    /**
     * Adds changing listener for spinnerwheel that updates the spinnerwheel label
     * @param wheel the spinnerwheel
     * @param label the spinnerwheel label
     */
    private void addChangingListener(final AbstractWheel wheel, final String label) {
        wheel.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                //spinnerwheel.setLabel(newValue != 1 ? label + "s" : label);
            }
        });
    }
}