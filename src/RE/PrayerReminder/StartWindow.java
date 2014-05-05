package RE.PrayerReminder;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.util.Date;

public class StartWindow extends Activity implements Observer {

    private static final String TAG = "StartWindow";
    private VibrationRepeaterService vibrationRepeaterService;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            vibrationRepeaterService = ((VibrationRepeaterService.LocalBinder)service).getService();
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
            editor.putLong(getString(R.string.keyLastVibrate), System.currentTimeMillis());
            editor.putLong(getString(R.string.keyNextVibrate), System.currentTimeMillis() + preferences.getLong(getString(R.string.keyVibrationDuration),60)*1000*60);
            editor.commit();
        }

        NumberPicker numberPicker = (NumberPicker) this.findViewById(R.id.numberPickerRepeatTime);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(99);
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
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),true))
                    vibrationRepeaterService.setRepeatTime(newVal);
            }
        });

        numberPicker = (NumberPicker) this.findViewById(R.id.numberPickerVibrationTime);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(99);
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
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),true))
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
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),true))
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
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),true))
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
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),true))
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
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),true))
                    vibrationRepeaterService.setVibrationStrength(seekBar.getProgress());
            }
        });


        ToggleButton toggleButton = (ToggleButton) this.findViewById(R.id.toggleButton);
        toggleButton.setTextOff("the reminder is not running");
        toggleButton.setTextOn("the app keeps reminding you constantly");
        toggleButton.setChecked(preferences.getBoolean(getString(R.string.keyIsAppActive), true));




        if(preferences.getBoolean(getString(R.string.keyIsAppActive),true)){
            this.startService(new Intent(this, VibrationRepeaterService.class));
            bindService(new Intent(this, VibrationRepeaterService.class), mConnection, Context.BIND_NOT_FOREGROUND);
            this.vibrationRepeaterService.addObserver(this);
        }

        TextView textView = (TextView) this.findViewById(R.id.textViewLastVibrate);
        textView.setText(R.string.lastVibrate);
        textView.append(vibrationRepeaterService != null ? (new Date(vibrationRepeaterService.getLastVibrate())).toString() : "");

        textView = (TextView) this.findViewById(R.id.textViewNextVibrate);
        textView.setText(R.string.nextVibrate);
        textView.append(vibrationRepeaterService != null ? (new Date(vibrationRepeaterService.getNextVibrate())).toString(): "");

    }

    public void onDestroy(){
        super.onDestroy();
        if(vibrationRepeaterService != null) {
            vibrationRepeaterService.removeObserver(this);
            this.unbindService(mConnection);
        }
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
            this.vibrationRepeaterService.setAppIsActive(toggleButton.isChecked());
        } else {
            Log.d(TAG, "try to stop PrayerReminder serivce");
            this.vibrationRepeaterService.setAppIsActive(toggleButton.isChecked());
            this.unbindService(mConnection);
            this.stopService(new Intent(this, VibrationRepeaterService.class));
        }
    }

    public void onTakeABreakClicked(View view){
        Log.d(TAG, "takeABreak was clicked");
        this.vibrationRepeaterService.takeABreak();
    }

    @Override
    public void update() {
        TextView textView = (TextView) this.findViewById(R.id.textViewLastVibrate);
        textView.setText(R.string.lastVibrate);
        textView.append(vibrationRepeaterService != null ? (new Date(vibrationRepeaterService.getLastVibrate())).toString() : "");

        textView = (TextView) this.findViewById(R.id.textViewNextVibrate);
        textView.setText(R.string.nextVibrate);
        textView.append(vibrationRepeaterService != null ? (new Date(vibrationRepeaterService.getNextVibrate())).toString(): "");
    }
}
