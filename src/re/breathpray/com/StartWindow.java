package re.breathpray.com;


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

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.OnWheelClickedListener;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;


import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class StartWindow extends Activity implements Observer {

    private static final String TAG = "StartWindow";
    private static final String AD_UNIT_ID = "ca-app-pub-3956003081714684/6818330858";
    private VibrationRepeaterService vibrationRepeaterService;
    private AdView adView;
    // Time scrolled flag
    private boolean timeScrolledStartWheels = false;
    private boolean timeScrolledEndWheels = false;

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

        final int minRepeatTime = 10;
        final int minVibrationDuration = 2;
        final int minBreakTime = 1;
        final int textSizeInMM = 3;

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



        final AbstractWheel repeatTimeWheel = (AbstractWheel) findViewById(R.id.repeatTime);
        NumericWheelAdapter repeatTimeWheelAdapter = new NumericWheelAdapter(this,minRepeatTime , 12*60, "%03d");
        repeatTimeWheelAdapter.setItemResource(R.layout.wheel_text_centered);
        repeatTimeWheelAdapter.setItemTextResource(R.id.text);
        repeatTimeWheel.setViewAdapter(repeatTimeWheelAdapter);
        repeatTimeWheel.setCurrentItem(preferences.getInt(getString(R.string.keyVibrationRepeatTime), 25) - minRepeatTime);
        repeatTimeWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                newValue += minRepeatTime;
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationRepeatTime), newValue);
                editor.commit();
                //change value in the service too
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),false))
                    vibrationRepeaterService.setRepeatTime(newValue);
            }
        });




        final AbstractWheel vibrationDurationWheel = (AbstractWheel) findViewById(R.id.vibrationDuration);
        NumericWheelAdapter vibrationDurationWheelAdapter = new NumericWheelAdapter(this, minVibrationDuration, 99, "%02d");
        vibrationDurationWheelAdapter.setItemResource(R.layout.wheel_text_centered);
        vibrationDurationWheelAdapter.setItemTextResource(R.id.text);
        vibrationDurationWheel.setViewAdapter(vibrationDurationWheelAdapter);
        vibrationDurationWheel.setCurrentItem(preferences.getInt(getString(R.string.keyVibrationDuration), 25) - minVibrationDuration);
        vibrationDurationWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                newValue += minVibrationDuration;
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationDuration), newValue);
                editor.commit();
                //change value in the service too
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),false))
                    vibrationRepeaterService.setVibrationTime(newValue);
            }
        });

        final AbstractWheel breakTimeWheel = (AbstractWheel) findViewById(R.id.breakTimeWheel);

        NumericWheelAdapter breakTimeWheelAdapter = new NumericWheelAdapter(this,minBreakTime , 999, "%03d");
        breakTimeWheelAdapter.setItemResource(R.layout.wheel_text_centered);
        breakTimeWheelAdapter.setItemTextResource(R.id.text);
        breakTimeWheel.setViewAdapter(breakTimeWheelAdapter);
        breakTimeWheel.setCurrentItem(preferences.getInt(getString(R.string.keyTakeABreakValue), 60) - minBreakTime);
        breakTimeWheel.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                newValue += minBreakTime;
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyTakeABreakValue), newValue);
                editor.commit();
                //change value in the service too
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),false))
                    vibrationRepeaterService.setTakeABreak(newValue);
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
                    SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(getString(R.string.keyVibrationStartHour), startHourWheel.getCurrentItem());
                    editor.putInt(getString(R.string.keyVibrationStartMinute), startMinuteWheel.getCurrentItem());
                    editor.commit();
                    //change value in the service too
                    if(preferences.getBoolean(getString(R.string.keyIsAppActive),false))
                        vibrationRepeaterService.setStartTime(startHourWheel.getCurrentItem(), startMinuteWheel.getCurrentItem());
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
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationStartHour), startHourWheel.getCurrentItem());
                editor.putInt(getString(R.string.keyVibrationStartMinute), startMinuteWheel.getCurrentItem());
                editor.commit();
                //change value in the service too
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),false))
                    vibrationRepeaterService.setStartTime(startHourWheel.getCurrentItem(), startMinuteWheel.getCurrentItem());
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
                    SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(getString(R.string.keyVibrationEndHour), endHourWheel.getCurrentItem());
                    editor.putInt(getString(R.string.keyVibrationEndMinute), endMinuteWheel.getCurrentItem());
                    editor.commit();
                    //change value in the service too
                    if(preferences.getBoolean(getString(R.string.keyIsAppActive),false))
                        vibrationRepeaterService.setEndTime(endHourWheel.getCurrentItem(), endMinuteWheel.getCurrentItem());
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
                SharedPreferences preferences = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(getString(R.string.keyVibrationEndHour), endHourWheel.getCurrentItem());
                editor.putInt(getString(R.string.keyVibrationEndMinute), endMinuteWheel.getCurrentItem());
                editor.commit();
                //change value in the service too
                if(preferences.getBoolean(getString(R.string.keyIsAppActive),false))
                    vibrationRepeaterService.setEndTime(endHourWheel.getCurrentItem(), endMinuteWheel.getCurrentItem());
            }
        };

        endHourWheel.addScrollingListener(scrollListener);
        endMinuteWheel.addScrollingListener(scrollListener);




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
            Log.d(TAG, "try to stop breathpray service");
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" EEE HH:mm",getResources().getConfiguration().locale);
        textView.append(vibrationRepeaterService != null ? simpleDateFormat.format(new Date(vibrationRepeaterService.getLastVibrate()), new StringBuffer(),new FieldPosition(0)) : " Service is not running");

        textView = (TextView) this.findViewById(R.id.textViewNextVibrate);
        textView.setText(R.string.nextVibrate);
        textView.append(vibrationRepeaterService != null ? simpleDateFormat.format(new Date(vibrationRepeaterService.getNextVibrate()), new StringBuffer(),new FieldPosition(0)) : " Service is not running");
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