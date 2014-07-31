package re.breathpray.com;


import android.os.Vibrator;
import antistatic.spinnerwheel.OnWheelScrollListener;
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
import org.joda.time.LocalTime;

public class LauncherWindow extends Activity {

    private static final String TAG = "LauncherWindow";
    private static final String AD_UNIT_ID = "ca-app-pub-3956003081714684/6818330858";
    private AdView adView;
    // Time scrolled flag
    private final Activity activity = this;
    public final int textSizeInMM = 2;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        final int minRepeatTime = 1;
        final int minVibrationDuration = 0;
        final int minBreakTime = 1;

        SharedPreferences preferences = activity.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);

        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_UNIT_ID);

        Log.d(TAG,"starting up main window of BreathPray");
        if(preferences.getBoolean(getString(R.string.keyFirstStart),false)){
            Log.d(TAG,"BreathPray - first startup");


            final Intent intent = new Intent(this, FirstStartupActivity.class);
            intent.setAction(BreathPrayConstants.defaultFirstStartupActivityAction);
            intent.addCategory(BreathPrayConstants.defaultCategory);
            startActivity(intent);

            if(!((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).hasVibrator());
                //TODO create popup

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(getString(R.string.keyFirstStart),true);
            editor.putBoolean(getString(R.string.keyIsAppActive),false);
            while(!editor.commit());
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
        repeatTimeWheel.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(AbstractWheel wheel) {

            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
                int value = minRepeatTime + wheel.getCurrentItem();
                SharedPreferences preferences = activity.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationRepeatTime), value);
                editor.putBoolean(getString(R.string.keyIsAppActive), true);
                //Data has to be commited!
                while (!editor.commit()) ;
                ((ToggleButton) activity.findViewById(R.id.toggleButton)).setChecked(true);
                startVibrationService(true);
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
        vibrationDurationWheel.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(AbstractWheel wheel) {
            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
                int value = minVibrationDuration + wheel.getCurrentItem();
                SharedPreferences preferences = activity.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationDuration), value);
                editor.putBoolean(getString(R.string.keyIsAppActive), true);
                //Data has to be commited!
                while (!editor.commit()) ;
                ((ToggleButton) activity.findViewById(R.id.toggleButton)).setChecked(true);
                startVibrationService(true);
            }
        });

        final AbstractWheel breakTimeWheel = (AbstractWheel) findViewById(R.id.breakTimeWheel);

        NumericWheelAdapter breakTimeWheelAdapter = new NumericWheelAdapter(this,minBreakTime , 999, "%03d");
        breakTimeWheelAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        breakTimeWheelAdapter.setItemTextResource(R.id.text);
        breakTimeWheel.setViewAdapter(breakTimeWheelAdapter);
        breakTimeWheel.setCurrentItem(preferences.getInt(getString(R.string.keyTakeABreakValue), 60) - minBreakTime);
        breakTimeWheel.addScrollingListener(new OnWheelScrollListener() {


            @Override
            public void onScrollingStarted(AbstractWheel wheel) {
            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
                int value = minBreakTime + wheel.getCurrentItem();
                SharedPreferences preferences = activity.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyTakeABreakValue), value);
                //Data has to be commited!
                while (!editor.commit()) ;
            }
        });



        SeekBar seekBar = (SeekBar) this.findViewById(R.id.seekBar);
        seekBar.setMax(BreathPrayConstants.vibrationCycleDuration);
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

                final Intent intent = new Intent(activity,ActiveVibrationService.class);
                intent.setAction(BreathPrayConstants.defaultCyclicVibrationServiceAction);
                intent.addCategory(BreathPrayConstants.defaultCategory);
                intent.putExtra(BreathPrayConstants.intervalIntentExtraFieldName,seekBar.getProgress());
                intent.putExtra(BreathPrayConstants.durationIntentExtraFieldName, BreathPrayConstants.vibrationCycleDuration);
                intent.putExtra(BreathPrayConstants.loopEndlessExecuteIntentExtraFieldName, true);

                bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mBound){
                    unbindService(mConnection);
                    mBound = false;
                }
                SharedPreferences preferences = activity.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
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

        updateDateTimes();

    }

    public void onDestroy(){

        if(adView != null)
            adView.destroy();

        super.onDestroy();
    }


    @Override
    public void onResume(){
        super.onResume();
        if(adView != null)
            adView.resume();

        updateDateTimes();
    }

    private void updateDateTimes() {

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);

        updateSingleDay(sharedPreferences, getString(R.string.monday),R.id.mondayTime);
        updateSingleDay(sharedPreferences, getString(R.string.tuesday),R.id.tuesdayTime);
        updateSingleDay(sharedPreferences, getString(R.string.wednesday),R.id.wednesdayTime);
        updateSingleDay(sharedPreferences, getString(R.string.thursday),R.id.thursdayTime);
        updateSingleDay(sharedPreferences, getString(R.string.friday),R.id.fridayTime);
        updateSingleDay(sharedPreferences, getString(R.string.saturday),R.id.saturdayTime);
        updateSingleDay(sharedPreferences, getString(R.string.sunday),R.id.sundayTime);

    }

    private void updateSingleDay(SharedPreferences sharedPreferences, String day, int textviewID) {
        int startTime;
        int endTime;
        startTime = sharedPreferences.getInt(day + "Start", 6 * BreathPrayConstants.numberOfGridPerHour);
        endTime = sharedPreferences.getInt(day + "End", 22 * BreathPrayConstants.numberOfGridPerHour);
        ((TextView) this.findViewById(textviewID)).setText(
                new LocalTime()
                        .hourOfDay().setCopy(startTime / BreathPrayConstants.numberOfGridPerHour)
                        .minuteOfHour().setCopy((startTime % BreathPrayConstants.numberOfGridPerHour) * BreathPrayConstants.gridInMinutes)
                        .toString(getString(R.string.timePattern))
                + " - " +
                new LocalTime()
                        .hourOfDay().setCopy(endTime / BreathPrayConstants.numberOfGridPerHour)
                        .minuteOfHour().setCopy((endTime % BreathPrayConstants.numberOfGridPerHour) * BreathPrayConstants.gridInMinutes)
                        .toString(getString(R.string.timePattern))

        );
    }

    @Override
    public void onPause(){
        if(adView != null)
            adView.pause();
        super.onPause();
    }

    public void onToggleButtonClick(View view){
        ToggleButton toggleButton = (ToggleButton) view;

        SharedPreferences preferences = activity.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getString(R.string.keyIsAppActive), toggleButton.isChecked());
        while (!editor.commit());

        startVibrationService(toggleButton.isChecked());
    }

    private void startVibrationService(final boolean startOrStop) {
        if(startOrStop) {
            final Intent intent = new Intent(this, VibrationRepeaterService.class);
            intent.putExtra(BreathPrayConstants.startVibrationIntentExtraFieldName,true);
            this.startService(intent);
        } else {
            final Intent intent = new Intent(this, VibrationRepeaterService.class);
            intent.putExtra(BreathPrayConstants.endVibrationIntentExtraFieldName,true);
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
        intent.setAction(BreathPrayConstants.defaultEditDayAction);
        intent.addCategory(BreathPrayConstants.defaultCategory);
        //set the dayname to monday
        intent.putExtra(BreathPrayConstants.dayName, dayName);
        //set "monday"+"Start" to time where the app should shut down
        intent.putExtra("Start",
                activity.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE).getInt(dayName + "Start", 6 * 12));
        //set "monday"+"End" to time where the app should shut down
        intent.putExtra("End",
                activity.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE).getInt(dayName+"End",22*12));
        startActivity(intent);
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