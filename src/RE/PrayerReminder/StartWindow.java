package RE.PrayerReminder;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.*;

import java.util.Date;

public class StartWindow extends Activity {

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
        if(preferences.getBoolean(getString(R.string.firstStart),true)){
            Log.d(TAG,"first startup");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(getString(R.string.firstStart), false);
            editor.putInt(getString(R.string.vibrationStrengthValue), 150);
            editor.putInt(getString(R.string.vibrationCycleTime), 10);
            editor.putInt(getString(R.string.vibrationTime), 16);
            editor.putInt(getString(R.string.VibrationEndHour), 22);
            editor.putInt(getString(R.string.VibrationEndMinute),0);
            editor.putInt(getString(R.string.VibrationStartHour),6);
            editor.putInt(getString(R.string.VibrationStartMinute),0);
            editor.putBoolean(getString(R.string.isAppActive), true);
            editor.putInt(getString(R.string.takeABreakValue), 60);
            editor.putLong(getString(R.string.lastVibrate), System.currentTimeMillis());
            editor.putLong(getString(R.string.nextVibrate), System.currentTimeMillis() + preferences.getLong(getString(R.string.vibrationCycleTime),60)*1000*60);
            editor.commit();
        }

        NumberPicker numberPicker = (NumberPicker) this.findViewById(R.id.numberPickerRepeatTime);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(99);
        numberPicker.setValue(preferences.getInt(getString(R.string.vibrationCycleTime), 10));
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            public static final String TAG = "NumberPickerRepeatTime";

            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal){
                Log.d(TAG, newVal + "");
                //write data to app storage
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.vibrationCycleTime), newVal);
                editor.commit();
                //change value it in the service too
                vibrationRepeaterService.setRepeatTime(newVal);
                //reschedule thread since it's repeat time has been changed
                if(preferences.getBoolean(getString(R.string.isAppActive),true))
                    vibrationRepeaterService.scheduleVibrationRepeater();

                drawStatus();
            }
        });

        numberPicker = (NumberPicker) this.findViewById(R.id.numberPickerVibrationTime);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(99);
        numberPicker.setValue(preferences.getInt(getString(R.string.vibrationTime), 16));
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){

            public static final String TAG = "NumberPickerVibrationTime";

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, newVal + "");
                //write data to app storage
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.vibrationTime), newVal);
                editor.commit();
                //change value in the service too
                if(preferences.getBoolean(getString(R.string.isAppActive),true))
                    vibrationRepeaterService.setVibrationTime(newVal);
            }
        });

        numberPicker = (NumberPicker) this.findViewById(R.id.numberPickerBreak);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(999);
        numberPicker.setValue(preferences.getInt(getString(R.string.takeABreakValue), 60));
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){

            public static final String TAG = "NumberPickerTakeABreak";

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d(TAG, newVal + "");
                //write data to app storage
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.takeABreakValue), newVal);
                editor.commit();
                //change value in the service too
                if(preferences.getBoolean(getString(R.string.isAppActive),true))
                    vibrationRepeaterService.setTakeABreak(newVal);
            }
        });

        TimePicker timePicker = (TimePicker) this.findViewById(R.id.timePickerStart);
        timePicker.setIs24HourView(DateFormat.is24HourFormat(this));
        timePicker.setCurrentHour(preferences.getInt(getString(R.string.VibrationStartHour),6));
        timePicker.setCurrentMinute(preferences.getInt(getString(R.string.VibrationStartMinute), 0));
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
            private static final String TAG = "TimePickerStart";


            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.d(TAG, "changed");
                //write data to app storage
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.VibrationStartHour), view.getCurrentHour());
                editor.putInt(getString(R.string.VibrationStartMinute), view.getCurrentMinute());
                editor.commit();
                //change value in the service too
                if(preferences.getBoolean(getString(R.string.isAppActive),true))
                    vibrationRepeaterService.setStartTime(view.getCurrentHour(), view.getCurrentMinute());
            }
        });

        timePicker = (TimePicker) this.findViewById(R.id.timePickerEnd);
        timePicker.setIs24HourView(DateFormat.is24HourFormat(this));
        timePicker.setCurrentHour(preferences.getInt(getString(R.string.VibrationEndHour),22));
        timePicker.setCurrentMinute(preferences.getInt(getString(R.string.VibrationEndMinute), 0));
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener(){
            private static final String TAG = "TimePickerEnd";


            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Log.d(TAG, "changed");
                //write data to app storage
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.VibrationEndHour), view.getCurrentHour());
                editor.putInt(getString(R.string.VibrationEndMinute), view.getCurrentMinute());
                editor.commit();
                //change value in the service too
                if(preferences.getBoolean(getString(R.string.isAppActive),true))
                    vibrationRepeaterService.setEndTime(view.getCurrentHour(), view.getCurrentMinute());
            }
        });

        SeekBar seekBar = (SeekBar) this.findViewById(R.id.seekBar);
        seekBar.setMax(200);
        seekBar.setProgress(preferences.getInt(getString(R.string.vibrationStrengthValue),150));
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
                editor.putInt(getString(R.string.vibrationStrengthValue), seekBar.getProgress());
                editor.commit();
                if(preferences.getBoolean(getString(R.string.isAppActive),true))
                    vibrationRepeaterService.setVibrationStrength(seekBar.getProgress());
            }
        });


        ToggleButton toggleButton = (ToggleButton) this.findViewById(R.id.toggleButton);
        toggleButton.setTextOff("the reminder is not running");
        toggleButton.setTextOn("the app keeps reminding you constantly");
        toggleButton.setChecked(preferences.getBoolean(getString(R.string.isAppActive), true));




        if(preferences.getBoolean(getString(R.string.isAppActive),true)){
            this.startService(new Intent(this, VibrationRepeaterService.class));
            bindService(new Intent(this, VibrationRepeaterService.class), mConnection, Context.BIND_NOT_FOREGROUND);
        }

        drawStatus();

    }

    public void onDestroy(){
        super.onDestroy();
    }

    public void onToggleButtonClick(View view){
        ToggleButton toggleButton = (ToggleButton) view;

        SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getString(R.string.isAppActive), toggleButton.isChecked());
        editor.commit();

        if(toggleButton.isChecked()) {
            this.startService(new Intent(this, VibrationRepeaterService.class));
            bindService(new Intent(this,VibrationRepeaterService.class), mConnection, Context.BIND_NOT_FOREGROUND);
        } else {
            Log.d(TAG, "try to stop PrayerReminder serivce");
            this.unbindService(mConnection);
            this.stopService(new Intent(this, VibrationRepeaterService.class));
        }
    }

    public void onTakeABreakClicked(View view){
        Log.d(TAG, "takeABreak was clicked");

        TextView textView = (TextView) this.findViewById(R.id.textViewNextVibrate);
        textView.setText(R.string.nextVibrate);
        textView.append(vibrationRepeaterService != null ? new Date(vibrationRepeaterService.getNextVibrate()) + "" : "");

        this.vibrationRepeaterService.scheduleVibrationRepeater(((NumberPicker) this.findViewById(R.id.numberPickerBreak)).getValue()*1000*60);
    }

    public void drawStatus(){
        Log.d(TAG, "update status: service=" + vibrationRepeaterService==null?null:"running");
        TextView textView = (TextView) this.findViewById(R.id.textViewLastVibrate);
        textView.setText(R.string.lastVibrate);
        textView.append(vibrationRepeaterService != null ? (new Date(vibrationRepeaterService.getLastVibrate())).toString() : "");

        textView = (TextView) this.findViewById(R.id.textViewNextVibrate);
        textView.setText(R.string.nextVibrate);
        textView.append(vibrationRepeaterService != null ? (new Date(vibrationRepeaterService.getNextVibrate())).toString(): "");
    }
}
