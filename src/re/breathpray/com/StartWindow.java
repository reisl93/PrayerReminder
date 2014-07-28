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
import antistatic.spinnerwheel.OnWheelClickedListener;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;

public class StartWindow extends Activity {

    private static final String TAG = "StartWindow";
    private static final String AD_UNIT_ID = "ca-app-pub-3956003081714684/6818330858";
    private AdView adView;
    // Time scrolled flag
    private boolean timeScrolledStartWheels = false;
    private boolean timeScrolledEndWheels = false;
    private final Context context = this;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            //vibrationRepeaterService = ((VibrationRepeaterService.LocalBinder)service).getService();
            Log.d(TAG, "connection established");
        }

        public void onServiceDisconnected(ComponentName className) {
            //vibrationRepeaterService = null;
        }
    };

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
        final int textSizeInMM = 3;

        SharedPreferences preferences = context.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);

        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_UNIT_ID);

        Log.d(TAG,"starting up main window of BreathPray");
        if(preferences.getBoolean(getString(R.string.keyFirstStart),true)){
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

            editor.commit();
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
                editor.commit();
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
                editor.commit();
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
                editor.commit();
            }
        });


        final AbstractWheel startHourWheel = (AbstractWheel) findViewById(R.id.startHour);
        startHourWheel.setViewAdapter(new NumericWheelAdapter(this, 0, 23));
        startHourWheel.setCyclic(true);
        startHourWheel.setCurrentItem(preferences.getInt(getString(R.string.keyVibrationStartHour), 6));

        final AbstractWheel startMinuteWheel = (AbstractWheel) findViewById(R.id.startMinute);
        startMinuteWheel.setViewAdapter(new NumericWheelAdapter(this, 0, 59, "%02d"));
        startMinuteWheel.setCyclic(true);
        startMinuteWheel.setCurrentItem(preferences.getInt(getString(R.string.keyVibrationStartMinute), 0));


        // add listeners
        addChangingListener(startMinuteWheel, "minStart");
        addChangingListener(startHourWheel, "hourStart");

        OnWheelChangedListener wheelListener = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!timeScrolledStartWheels) {
                    //write data to app storage
                    SharedPreferences preferences = context.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(getString(R.string.keyVibrationStartHour), startHourWheel.getCurrentItem());
                    editor.putInt(getString(R.string.keyVibrationStartMinute), startMinuteWheel.getCurrentItem());
                    editor.commit();
                }
            }
        };
        startHourWheel.addChangingListener(wheelListener);
        startMinuteWheel.addChangingListener(wheelListener);

        OnWheelClickedListener clickStart = new OnWheelClickedListener() {
            public void onItemClicked(AbstractWheel wheel, int itemIndex) {
                wheel.setCurrentItem(itemIndex, true);
            }
        };
        startHourWheel.addClickingListener(clickStart);
        startMinuteWheel.addClickingListener(clickStart);

        OnWheelScrollListener wheelListenerStart = new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
                timeScrolledStartWheels = true;
            }
            public void onScrollingFinished(AbstractWheel wheel) {
                timeScrolledStartWheels = false;
                //write data to app storage
                SharedPreferences preferences = context.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationStartHour), startHourWheel.getCurrentItem());
                editor.putInt(getString(R.string.keyVibrationStartMinute), startMinuteWheel.getCurrentItem());
                editor.commit();
            }
        };

        startHourWheel.addScrollingListener(wheelListenerStart);
        startMinuteWheel.addScrollingListener(wheelListenerStart);



        final AbstractWheel endHourWheel = (AbstractWheel) findViewById(R.id.endHour);
        endHourWheel.setViewAdapter(new NumericWheelAdapter(this, 0, 23));
        endHourWheel.setCyclic(true);
        endHourWheel.setCurrentItem(preferences.getInt(getString(R.string.keyVibrationEndHour), 6));

        final AbstractWheel endMinuteWheel = (AbstractWheel) findViewById(R.id.endMinute);
        endMinuteWheel.setViewAdapter(new NumericWheelAdapter(this, 0, 59, "%02d"));
        endMinuteWheel.setCyclic(true);
        endMinuteWheel.setCurrentItem(preferences.getInt(getString(R.string.keyVibrationEndMinute), 0));


        // add listeners
        addChangingListener(endMinuteWheel, "minEnd");
        addChangingListener(endHourWheel, "hourEnd");

        OnWheelChangedListener wheelListenerEnd = new OnWheelChangedListener() {
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (!timeScrolledEndWheels) {
                    //write data to app storage
                    SharedPreferences preferences = context.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(getString(R.string.keyVibrationEndHour), endHourWheel.getCurrentItem());
                    editor.putInt(getString(R.string.keyVibrationEndMinute), endMinuteWheel.getCurrentItem());
                    editor.commit();
                }
            }
        };
        endHourWheel.addChangingListener(wheelListenerEnd);
        endMinuteWheel.addChangingListener(wheelListenerEnd);

        OnWheelClickedListener clickEnd = new OnWheelClickedListener() {
            public void onItemClicked(AbstractWheel wheel, int itemIndex) {
                wheel.setCurrentItem(itemIndex, true);
            }
        };
        endHourWheel.addClickingListener(clickEnd);
        endMinuteWheel.addClickingListener(clickEnd);

        OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
            public void onScrollingStarted(AbstractWheel wheel) {
                timeScrolledEndWheels = true;
            }
            public void onScrollingFinished(AbstractWheel wheel) {
                timeScrolledEndWheels = false;
                //write data to app storage
                SharedPreferences preferences = context.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationEndHour), endHourWheel.getCurrentItem());
                editor.putInt(getString(R.string.keyVibrationEndMinute), endMinuteWheel.getCurrentItem());
                editor.commit();
            }
        };

        endHourWheel.addScrollingListener(scrollListener);
        endMinuteWheel.addScrollingListener(scrollListener);


        SeekBar seekBar = (SeekBar) this.findViewById(R.id.seekBar);
        seekBar.setMax((int) ConfigurationManager.vibrationCycleDuration);
        seekBar.setProgress(preferences.getInt(getString(R.string.keyVibrationPower),150));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //activeVibrationService.setInterval(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                final Intent intent = new Intent(context,ActiveVibrationService.class);
                intent.setAction(ConfigurationManager.defaultCyclicVibrationServiceAction);
                intent.addCategory(ConfigurationManager.defaultCategory);
                intent.putExtra(ActiveVibrationService.intervalIntentExtraFieldName,(long)seekBar.getProgress());
                intent.putExtra(ActiveVibrationService.durationIntentExtraFieldName, ConfigurationManager.vibrationCycleDuration);
                intent.putExtra(ActiveVibrationService.isRunningIntentExtraFieldName, true);
                //context.startService(intent);
                //TODO

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //activeVibrationService.setRunning(false);
                SharedPreferences preferences = context.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationPower), seekBar.getProgress());
                editor.commit();
            }
        });


        ToggleButton toggleButton = (ToggleButton) this.findViewById(R.id.toggleButton);

        toggleButton.setTextOff(getString(R.string.appIsNotActiveText));
        toggleButton.setTextOn(getString(R.string.appIsActiveText));
        toggleButton.setChecked(preferences.getBoolean(getString(R.string.keyIsAppActive), false));


        TextView textView = (TextView) this.findViewById(R.id.textViewLastVibrate);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeInMM);
        textView.setText(R.string.lastVibrate);

        textView = (TextView) this.findViewById(R.id.textViewNextVibrate);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeInMM);
        textView.setText(R.string.nextVibrate);

        textView = (TextView) this.findViewById(R.id.textViewRepeatTime);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeInMM);

        textView = (TextView) this.findViewById(R.id.textViewVibrateDuration);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeInMM);

        textView = (TextView) this.findViewById(R.id.textViewEndTime);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeInMM);

        textView = (TextView) this.findViewById(R.id.textViewStartTime);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeInMM);

        textView = (TextView) this.findViewById(R.id.textViewVibratePower);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeInMM);

        textView = (TextView) this.findViewById(R.id.textViewTakeABreak);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeInMM);

        textView = (TextView) this.findViewById(R.id.textViewStatus);
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