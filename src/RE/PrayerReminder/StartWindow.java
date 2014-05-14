package RE.PrayerReminder;


import android.preference.PreferenceManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.*;
import java.util.Date;
import java.util.GregorianCalendar;

public class StartWindow extends Activity implements Observer {

    private static final String TAG = "StartWindow";
    private static final String AD_UNIT_ID = "ca-app-pub-3956003081714684/6818330858";
    private VibrationRepeaterService vibrationRepeaterService;
    private AdView adView;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            vibrationRepeaterService = ((VibrationRepeaterService.LocalBinder)service).getService();
            addObserverToService();
            Log.d(TAG, "connection established");
        }

        public void onServiceDisconnected(ComponentName className) {
            vibrationRepeaterService = null;
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        SharedPreferences preferences = this.getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);

        if(preferences.getBoolean(getString(R.string.keyIsAppActive),true)){
            Log.d(TAG, "starting service");
            this.startService(new Intent(this, VibrationRepeaterService.class));
            bindService(new Intent(this, VibrationRepeaterService.class), mConnection, Context.BIND_NOT_FOREGROUND);
        }
        
        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_UNIT_ID);

        if(preferences.getBoolean(getString(R.string.keyFirstStart),true)){
            Log.d(TAG,"first startup");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(getString(R.string.keyFirstStart), false);
            editor.putInt(getString(R.string.keyVibrationPower), 150);
            editor.putInt(getString(R.string.keyVibrationRepeatTime), 10);
            editor.putInt(getString(R.string.keyVibrationDuration), 16);
            editor.putInt(getString(R.string.keyVibrationEndHour), 22);
            editor.putInt(getString(R.string.keyVibrationEndMinute),0);
            editor.putInt(getString(R.string.keyVibrationStartHour),6);
            editor.putInt(getString(R.string.keyVibrationStartMinute),0);
            editor.putBoolean(getString(R.string.keyIsAppActive), true);
            editor.putInt(getString(R.string.keyTakeABreakValue), 60);
            editor.putLong(getString(R.string.keyLastVibrate), System.currentTimeMillis() - GregorianCalendar.getInstance().getTimeZone().getRawOffset() - 1000*60);
            editor.putLong(getString(R.string.keyNextVibrate), System.currentTimeMillis() - GregorianCalendar.getInstance().getTimeZone().getRawOffset() - 1000*60 + preferences.getLong(getString(R.string.keyVibrationDuration),60)*1000*60);
            editor.commit();
        }

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.adView);
        linearLayout.addView(adView);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("9684DFFB83935CE920E945C32F975A12")
                .build();


        adView.loadAd(adRequest);

        NumberPicker numberPicker = (NumberPicker) this.findViewById(R.id.numberPickerRepeatTime);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(24*60);
        numberPicker.setValue(preferences.getInt(getString(R.string.keyVibrationRepeatTime), 10));
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            public static final String TAG = "NumberPickerRepeatTime";

            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal){
                Log.d(TAG, newVal + "");
                //write data to app storage
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationRepeatTime), newVal);
                editor.commit();
                //change value it in the service too
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),false))
                    vibrationRepeaterService.setRepeatTime(newVal);
            }
        });

        numberPicker = (NumberPicker) this.findViewById(R.id.numberPickerVibrationTime);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(140);
        numberPicker.setValue(preferences.getInt(getString(R.string.keyVibrationDuration), 16));
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){

            public static final String TAG = "NumberPickerVibrationTime";

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, newVal + "");
                //write data to app storage
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationDuration), newVal);
                editor.commit();
                //change value in the service too
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),false))
                    vibrationRepeaterService.setVibrationTime(newVal);
            }
        });

        numberPicker = (NumberPicker) this.findViewById(R.id.numberPickerBreak);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(999);
        numberPicker.setValue(preferences.getInt(getString(R.string.keyTakeABreakValue), 60));
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){

            public static final String TAG = "NumberPickerTakeABreak";

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, newVal + "");
                //write data to app storage
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyTakeABreakValue), newVal);
                editor.commit();
                //change value in the service too
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),false))
                    vibrationRepeaterService.setTakeABreak(newVal);
            }
        });

        TimePicker timePicker = (TimePicker) this.findViewById(R.id.timePickerStart);
        timePicker.setIs24HourView(DateFormat.is24HourFormat(this));
        timePicker.setCurrentHour(preferences.getInt(getString(R.string.keyVibrationStartHour),6));
        timePicker.setCurrentMinute(preferences.getInt(getString(R.string.keyVibrationStartMinute), 0));
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
            private static final String TAG = "TimePickerStart";


            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.d(TAG, "changed");
                //write data to app storage
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationStartHour), view.getCurrentHour());
                editor.putInt(getString(R.string.keyVibrationStartMinute), view.getCurrentMinute());
                editor.commit();
                //change value in the service too
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),false))
                    vibrationRepeaterService.setStartTime(view.getCurrentHour(), view.getCurrentMinute());
            }
        });

        timePicker = (TimePicker) this.findViewById(R.id.timePickerEnd);
        timePicker.setIs24HourView(DateFormat.is24HourFormat(this));
        timePicker.setCurrentHour(preferences.getInt(getString(R.string.keyVibrationEndHour),22));
        timePicker.setCurrentMinute(preferences.getInt(getString(R.string.keyVibrationEndMinute), 0));
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
            private static final String TAG = "TimePickerEnd";


            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.d(TAG, "changed");
                //write data to app storage
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationEndHour), view.getCurrentHour());
                editor.putInt(getString(R.string.keyVibrationEndMinute), view.getCurrentMinute());
                editor.commit();
                //change value in the service too
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),false))
                    vibrationRepeaterService.setEndTime(view.getCurrentHour(), view.getCurrentMinute());
            }
        });

        SeekBar seekBar = (SeekBar) this.findViewById(R.id.seekBar);
        seekBar.setMax(200);
        seekBar.setProgress(preferences.getInt(getString(R.string.keyVibrationPower),150));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationPower), seekBar.getProgress());
                editor.commit();
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),false))
                    vibrationRepeaterService.setVibrationStrength(seekBar.getProgress());
            }
        });


        ToggleButton toggleButton = (ToggleButton) this.findViewById(R.id.toggleButton);
        toggleButton.setTextOff(getString(R.string.appIsNotActiveText));
        toggleButton.setTextOn(getString(R.string.appIsActiveText));
        toggleButton.setChecked(preferences.getBoolean(getString(R.string.keyIsAppActive), false));


        final int textSizeInMM = 3;
        TextView textView = (TextView) this.findViewById(R.id.textViewLastVibrate);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeInMM);
        textView.setText(R.string.lastVibrate);
        textView.append(vibrationRepeaterService != null ? (new Date(vibrationRepeaterService.getLastVibrate())).toString() : "");

        textView = (TextView) this.findViewById(R.id.textViewNextVibrate);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_MM, textSizeInMM);
        textView.setText(R.string.nextVibrate);
        textView.append(vibrationRepeaterService != null ? (new Date(vibrationRepeaterService.getNextVibrate())).toString(): "");

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
        if(vibrationRepeaterService != null) {
            vibrationRepeaterService.removeObserver(this);
            this.unbindService(mConnection);
        }
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
        if(adView != null)
            adView.pause();
        super.onPause();
    }

    public void onToggleButtonClick(View view){
        ToggleButton toggleButton = (ToggleButton) view;

        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getString(R.string.keyIsAppActive), toggleButton.isChecked());
        editor.commit();

        if(toggleButton.isChecked()) {
            this.startService(new Intent(this, VibrationRepeaterService.class));
            bindService(new Intent(this,VibrationRepeaterService.class), mConnection, Context.BIND_NOT_FOREGROUND);
        } else {
            Log.d(TAG, "try to stop PrayerReminder service");
            this.vibrationRepeaterService.setAppIsActive(toggleButton.isChecked());
            this.unbindService(mConnection);
            this.stopService(new Intent(this, VibrationRepeaterService.class));
        }
    }

    public void onTakeABreakClicked(View view){
        Log.d(TAG, "takeABreak was clicked");
        if(getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE).getBoolean(getString(R.string.keyIsAppActive),false))
            this.vibrationRepeaterService.takeABreak();
    }

    public void addObserverToService(){
        this.vibrationRepeaterService.addObserver(this);
    }


    @Override
    public void update() {
        TextView textView = (TextView) this.findViewById(R.id.textViewLastVibrate);
        textView.setText(R.string.lastVibrate);
        textView.append(vibrationRepeaterService != null ? DateFormat.format(" EEE HH:mm:ss", new Date(vibrationRepeaterService.getLastVibrate())) : " Service is not running");

        textView = (TextView) this.findViewById(R.id.textViewNextVibrate);
        textView.setText(R.string.nextVibrate);
        textView.append(vibrationRepeaterService != null ? DateFormat.format(" EEE HH:mm:ss", new Date(vibrationRepeaterService.getNextVibrate())) : " Service is not running");
    }
}
