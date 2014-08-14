package re.breathpray.com.activities;


import android.app.AlertDialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import antistatic.spinnerwheel.OnWheelScrollListener;
import antistatic.spinnerwheel.adapters.ArrayWheelAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.adapters.NumericWheelAdapter;
import org.joda.time.LocalTime;
import re.breathpray.com.BreathPrayConstants;
import re.breathpray.com.DeviceHasNoVibrationDialog;
import re.breathpray.com.R;
import re.breathpray.com.services.VibrationRepeaterService;

public class LauncherWindow extends Activity {

    private static final String TAG = "LauncherWindow";
    private static final String AD_UNIT_ID = "ca-app-pub-3956003081714684/6818330858";
    private AdView adView;
    // Time scrolled flag
    private final Activity activity = this;

    // that is the string I want to get from Ringtone picker
    // something like  content://media/internal/audio/media/60
    // I can also get it stored version from somewhere else (preferences and such)
    private String mRingtonePath = null;

    // that is temp path I am using, because I can't find other way to store path
    // received in setSingleChoiceItems onClickListener
    private String mRingtoneTempPath = null;

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

        SharedPreferences preferences = activity.getSharedPreferences(BreathPrayConstants.PREFERENCEFILE, MODE_PRIVATE);

        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(AD_UNIT_ID);

        Log.d(TAG, "starting up main window of BreathPray");
        if (preferences.getBoolean(BreathPrayConstants.keyFirstStart, true)) {
            Log.d(TAG, "BreathPray - first startup");

            final Intent intent = new Intent(this, FirstStartupActivity.class);
            intent.setAction(BreathPrayConstants.defaultFirstStartupActivityAction);
            intent.addCategory(BreathPrayConstants.defaultCategory);
            startActivity(intent);


            if (!((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).hasVibrator()) {
                DialogFragment newFragment = new DeviceHasNoVibrationDialog();
                newFragment.show(this.getFragmentManager(), getString(R.string.deviceHasNoVibrator));
            }

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(BreathPrayConstants.keyFirstStart, false);
            editor.putBoolean(BreathPrayConstants.keyIsAppActive, false);
            while (!editor.commit()) ;
        }

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.adView);
        linearLayout.addView(adView);

        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                //.addTestDevice("9684DFFB83935CE920E945C32F975A12")
                .build();
        adView.loadAd(adRequest);


        final AbstractWheel repeatTimeWheel = (AbstractWheel) findViewById(R.id.repeatTime);
        NumericWheelAdapter repeatTimeWheelAdapter = new NumericWheelAdapter(this, minRepeatTime, 12 * 60, "%03d");
        repeatTimeWheelAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        repeatTimeWheelAdapter.setItemTextResource(R.id.text);
        repeatTimeWheel.setViewAdapter(repeatTimeWheelAdapter);
        repeatTimeWheel.setCurrentItem(preferences.getInt(BreathPrayConstants.keyVibrationRepeatTime, 15) - minRepeatTime);
        repeatTimeWheel.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(AbstractWheel wheel) {}

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
                int value = minRepeatTime + wheel.getCurrentItem();
                SharedPreferences preferences = activity.getSharedPreferences(BreathPrayConstants.PREFERENCEFILE, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(BreathPrayConstants.keyVibrationRepeatTime, value);
                editor.putBoolean(BreathPrayConstants.keyIsAppActive, true);
                //Data has to be commited!
                while (!editor.commit()) ;
                ((ToggleButton) activity.findViewById(R.id.toggleButtonAppIsActive)).setChecked(true);
                startVibrationService(true, 0);
            }
        });


        final AbstractWheel vibrationDurationWheel = (AbstractWheel) findViewById(R.id.vibrationDuration);
        ArrayWheelAdapter<String> vibrationDurationWheelAdapter = new ArrayWheelAdapter<String>(this, new String[]{
                "0,1", "0,2", "0,3", "0,4", "0,5", "0,6", "0,7", "0,8", "0,9", "1,0",
                "1,1", "1,2", "1,3", "1,4", "1,5", "1,6", "1,7", "1,8", "1,9", "2,0",
                "2,1", "2,2", "2,3", "2,4", "2,5", "2,6", "2,7", "2,8", "2,9", "3,0",
                "3,1", "3,2", "3,3", "3,4", "3,5", "3,6", "3,7", "3,8", "3,9", "4,0",
                "4,1", "4,2", "4,3", "4,4", "4,5", "4,6", "4,7", "4,8", "4,9", "5,0",
                "5,1", "5,2", "5,3", "5,4", "5,5", "5,6", "5,7", "5,8", "5,9", "6,0",
                "6,1", "6,2", "6,3", "6,4", "6,5", "6,6", "6,7", "6,8", "6,9", "7,0",
                "7,1", "7,2", "7,3", "7,4", "7,5", "7,6", "7,7", "7,8", "7,9", "8,0",
                "8,1", "8,2", "8,3", "8,4", "8,5", "8,6", "8,7", "8,8", "8,9", "9,0",
                "9,1", "9,2", "9,3", "9,4", "9,5", "9,6", "9,7", "9,8", "9,9"
        });
        vibrationDurationWheelAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        vibrationDurationWheelAdapter.setItemTextResource(R.id.text);
        vibrationDurationWheel.setViewAdapter(vibrationDurationWheelAdapter);
        vibrationDurationWheel.setCurrentItem(preferences.getInt(BreathPrayConstants.keyVibrationDuration, 15) - minVibrationDuration);
        vibrationDurationWheel.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(AbstractWheel wheel) {
            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
                int value = minVibrationDuration + wheel.getCurrentItem();
                SharedPreferences preferences = activity.getSharedPreferences(BreathPrayConstants.PREFERENCEFILE, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(BreathPrayConstants.keyVibrationDuration, value);
                editor.putBoolean(BreathPrayConstants.keyIsAppActive, true);
                //Data has to be commited!
                while (!editor.commit()) ;
                ((ToggleButton) activity.findViewById(R.id.toggleButtonAppIsActive)).setChecked(true);
                startVibrationService(true, 0);
            }
        });

        final AbstractWheel breakTimeWheel = (AbstractWheel) findViewById(R.id.breakTimeWheel);

        NumericWheelAdapter breakTimeWheelAdapter = new NumericWheelAdapter(this, minBreakTime, 999, "%03d");
        breakTimeWheelAdapter.setItemResource(R.layout.wheel_text_centered_dark_back);
        breakTimeWheelAdapter.setItemTextResource(R.id.text);
        breakTimeWheel.setViewAdapter(breakTimeWheelAdapter);
        breakTimeWheel.setCurrentItem(preferences.getInt(BreathPrayConstants.keyTakeABreakValue, 60) - minBreakTime);
        breakTimeWheel.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(AbstractWheel wheel) {
            }

            @Override
            public void onScrollingFinished(AbstractWheel wheel) {
                int value = minBreakTime + wheel.getCurrentItem();
                SharedPreferences preferences = activity.getSharedPreferences(BreathPrayConstants.PREFERENCEFILE, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(BreathPrayConstants.keyTakeABreakValue, value);
                //Data has to be commited!
                while (!editor.commit()) ;
            }
        });


        SeekBar seekBar = (SeekBar) this.findViewById(R.id.seekBarPattern);
        seekBar.setMax(BreathPrayConstants.vibrationInterval);
        seekBar.setProgress(preferences.getInt(BreathPrayConstants.keyVibrationPattern, 150));
        final SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                final SharedPreferences preferences = activity.getSharedPreferences(BreathPrayConstants.PREFERENCEFILE, MODE_PRIVATE);
                final SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(BreathPrayConstants.keyVibrationPattern, ((SeekBar) activity.findViewById(R.id.seekBarPattern)).getProgress());
                final float value = ((SeekBar) activity.findViewById(R.id.seekBarVolume)).getProgress() / BreathPrayConstants.volumeMax;
                editor.putFloat(BreathPrayConstants.keyNotificationVolume, value*value); //value * value to generate a non linear seekbar/volume dependency - note that value <= 1
                while (!editor.commit()) ;
                startVibrationService(true, 0);
            }
        };
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        seekBar = (SeekBar) this.findViewById(R.id.seekBarVolume);
        seekBar.setMax((int)BreathPrayConstants.volumeMax);
        seekBar.setProgress((int) (preferences.getFloat(BreathPrayConstants.keyNotificationVolume, 0.5f) * BreathPrayConstants.volumeMax));
        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);


        //app is active/inactive button
        ToggleButton toggleButton = (ToggleButton) this.findViewById(R.id.toggleButtonAppIsActive);
        toggleButton.setTextOff(getString(R.string.appIsNotActiveText));
        toggleButton.setTextOn(getString(R.string.appIsActiveText));
        toggleButton.setChecked(preferences.getBoolean(BreathPrayConstants.keyIsAppActive, false));

        //acoustic activate button
        toggleButton = (ToggleButton) this.findViewById(R.id.toggleButtonRingtone);
        toggleButton.setChecked(preferences.getBoolean(BreathPrayConstants.keyAcousticIsActive, false));

        //unique volume button
        toggleButton = (ToggleButton) this.findViewById(R.id.toggleButtonVolume);
        toggleButton.setChecked(preferences.getBoolean(BreathPrayConstants.keyUniqueVolumeActive, false));

        updateDateTimes();

    }

    public void onDestroy() {

        if (adView != null)
            adView.destroy();

        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (adView != null)
            adView.resume();

        updateDateTimes();
    }

    private void updateDateTimes() {

        SharedPreferences sharedPreferences = getSharedPreferences(BreathPrayConstants.PREFERENCEFILE, MODE_PRIVATE);

        updateSingleDay(sharedPreferences, getString(R.string.monday), R.id.mondayTime);
        updateSingleDay(sharedPreferences, getString(R.string.tuesday), R.id.tuesdayTime);
        updateSingleDay(sharedPreferences, getString(R.string.wednesday), R.id.wednesdayTime);
        updateSingleDay(sharedPreferences, getString(R.string.thursday), R.id.thursdayTime);
        updateSingleDay(sharedPreferences, getString(R.string.friday), R.id.fridayTime);
        updateSingleDay(sharedPreferences, getString(R.string.saturday), R.id.saturdayTime);
        updateSingleDay(sharedPreferences, getString(R.string.sunday), R.id.sundayTime);

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
    public void onPause() {
        if (adView != null)
            adView.pause();
        super.onPause();
    }

    public void onToggleButtonActivateAppClick(final View view) {
        pulsView(view);

        final ToggleButton toggleButton = (ToggleButton) view;

        SharedPreferences preferences = activity.getSharedPreferences(BreathPrayConstants.PREFERENCEFILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(BreathPrayConstants.keyIsAppActive, toggleButton.isChecked());
        while (!editor.commit()) ;

        startVibrationService(toggleButton.isChecked(), 0);
    }

    public void onToggleButtonActivateRingtoneClick(final View view) {
        pulsView(view);

        final ToggleButton toggleButton = (ToggleButton) view;

        SharedPreferences preferences = activity.getSharedPreferences(BreathPrayConstants.PREFERENCEFILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(BreathPrayConstants.keyAcousticIsActive, toggleButton.isChecked());
        while (!editor.commit()) ;

        //reactivate app or don't do anything if app is active/inactive
        startVibrationService(((ToggleButton) activity.findViewById(R.id.toggleButtonAppIsActive)).isChecked(), 0);
    }

    public void onToggleButtonActivateVolumeClick(final View view) {
        pulsView(view);

        final ToggleButton toggleButton = (ToggleButton) view;

        SharedPreferences preferences = activity.getSharedPreferences(BreathPrayConstants.PREFERENCEFILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(BreathPrayConstants.keyUniqueVolumeActive, toggleButton.isChecked());
        while (!editor.commit()) ;

        startVibrationService(((ToggleButton) activity.findViewById(R.id.toggleButtonAppIsActive)).isChecked(), 0);
    }

    public void onToggleButtonSetRingtoneClick(final View view) {
        pulsView(view);

        final RingtoneManager rm = new RingtoneManager(activity);
        final Cursor ringtones = rm.getCursor();
        final MediaPlayer mp = new MediaPlayer();

        int selected = -1;

        // moving to proper ringtone in case some path was already supplied
        if (mRingtonePath != null)
            for (ringtones.moveToFirst(); !ringtones.isAfterLast(); ringtones
                    .moveToNext()) {
                selected++;
                String path = ringtones
                        .getString(RingtoneManager.URI_COLUMN_INDEX)
                        + "/"
                        + ringtones.getInt(RingtoneManager.ID_COLUMN_INDEX);
                if (path.equals(mRingtonePath)) {
                    break;
                }
            }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.ringtoneChooser));
        builder.setSingleChoiceItems(ringtones, selected,
                ringtones.getColumnName(RingtoneManager.TITLE_COLUMN_INDEX),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ringtones.moveToPosition(which);
                        String path = ringtones
                                .getString(RingtoneManager.URI_COLUMN_INDEX)
                                + "/"
                                + ringtones
                                .getInt(RingtoneManager.ID_COLUMN_INDEX);
                        // ugly solution to store temp path
                        setTempPathTo(path);
                        mp.reset();
                        try {
                            Uri uri = Uri.parse(path);
                            mp.setDataSource(activity, uri);
                            mp.setLooping(false);
                            mp.prepare();
                            mp.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mp.reset();
                mp.release();

                // I could read path nicely from here instead of using temp path,
                // but ringtones Cursor somehow moves couple positions forward
                // since last call to onClick in setSingleChoiceItems and
                // String s = ringtones
                // .getString(RingtoneManager.URI_COLUMN_INDEX)
                // + "/"
                // + ringtones.getInt(RingtoneManager.ID_COLUMN_INDEX);
                mRingtonePath = mRingtoneTempPath;
                SharedPreferences.Editor editor = activity.getSharedPreferences(BreathPrayConstants.PREFERENCEFILE, MODE_PRIVATE).edit();
                editor.putString(BreathPrayConstants.keyAcousticNotificationUri, mRingtonePath);
                while (!editor.commit()) ;
            }
        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mp.reset();
                        mp.release();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    void setTempPathTo(final String path) {
        mRingtoneTempPath = path;
    }

    private synchronized void startVibrationService(final boolean startOrStop, final int breakTime) {
        final ToggleButton toggleButton = (ToggleButton) this.findViewById(R.id.toggleButtonAppIsActive);
        final Intent intent = new Intent(this, VibrationRepeaterService.class);
        intent.putExtra(BreathPrayConstants.breakTimeIntentExtraFieldName, breakTime);

        if (startOrStop) {
            //set extra to activating service
            intent.putExtra(BreathPrayConstants.startVibrationIntentExtraFieldName, true);
            //set button text to activating
            toggleButton.setText(getString(R.string.activatingApp));
        } else {
            //set extra to deactivating service
            intent.putExtra(BreathPrayConstants.endVibrationIntentExtraFieldName, true);
            //set button text to deactivating
            toggleButton.setText(getString(R.string.deactivatingApp));
        }

        //start service
        while (!new Handler().post(new Runnable() {
            @Override
            public void run() {
                activity.startService(intent);
            }
        })) ;

        //delayed update of the displaying togglebutton - to visualize if the app is activating or deactivating
        while (!new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toggleButton.setChecked(startOrStop);
            }
        }, 800)) ;
    }

    public void onTakeABreakClicked(final View view) {
        pulsView(view);
        Log.d(TAG, "takeABreak was clicked");
        startVibrationService(true, activity.getSharedPreferences(BreathPrayConstants.PREFERENCEFILE, MODE_PRIVATE).getInt(BreathPrayConstants.keyTakeABreakValue, 60));
    }

    public void onMondayClicked(final View view) {
        pulsView(view);
        Log.d(TAG, "monday was edited");
        createEditDayActivity(getString(R.string.monday));
    }

    public void onTuesdayClicked(final View view) {
        pulsView(view);
        Log.d(TAG, "tuesday was edited");
        createEditDayActivity(getString(R.string.tuesday));
    }

    public void onWednesdayClicked(final View view) {
        pulsView(view);
        Log.d(TAG, "wednesday was edited");
        createEditDayActivity(getString(R.string.wednesday));
    }

    public void onThursdayClicked(final View view) {
        pulsView(view);
        Log.d(TAG, "thursday was edited");
        createEditDayActivity(getString(R.string.thursday));
    }

    public void onFridayClicked(final View view) {
        pulsView(view);
        Log.d(TAG, "friday was edited");
        createEditDayActivity(getString(R.string.friday));
    }

    public void onSaturdayClicked(final View view) {
        pulsView(view);
        Log.d(TAG, "saturday was edited");
        createEditDayActivity(getString(R.string.saturday));
    }

    public void onSundayClicked(final View view) {
        pulsView(view);
        Log.d(TAG, "sunday was edited");
        createEditDayActivity(getString(R.string.sunday));
    }

    private void createEditDayActivity(final String dayName) {
        Intent intent = new Intent(this, EditDayActivity.class);
        intent.setAction(BreathPrayConstants.defaultEditDayAction);
        intent.addCategory(BreathPrayConstants.defaultCategory);
        //set the dayname to monday
        intent.putExtra(BreathPrayConstants.dayNameIntentExtraFieldName, dayName);
        //set "monday"+"Start" to time where the app should shut down
        final SharedPreferences sharedPreferences = activity.getSharedPreferences(BreathPrayConstants.PREFERENCEFILE, MODE_PRIVATE);
        intent.putExtra("Start",
                sharedPreferences.getInt(dayName + "Start", 8 * BreathPrayConstants.numberOfGridPerHour));
        //set "monday"+"End" to time where the app should shut down
        intent.putExtra("End",
                sharedPreferences.getInt(dayName + "End", 22 * BreathPrayConstants.numberOfGridPerHour));
        startActivity(intent);
    }

    private void pulsView(final View view) {
        final float stepSize = 0.2f;
        final float minAlpha = 0.2f;
        final int timeStepsInMillis = 50;

        final Runnable increaseAlpha = new Runnable() {
            @Override
            public void run() {
                view.setAlpha(view.getAlpha() + stepSize);
            }
        };
        final Runnable decreaseAlpha = new Runnable() {
            @Override
            public void run() {
                view.setAlpha(view.getAlpha() - stepSize);
            }
        };

        for(int i = 0; i < (1-minAlpha)/stepSize; i++)
            view.postDelayed(decreaseAlpha,i*timeStepsInMillis);

        for(int i = 0; i < (1-minAlpha)/stepSize; i++)
            view.postDelayed(increaseAlpha,i*timeStepsInMillis + (int) ((1 - minAlpha) / stepSize * timeStepsInMillis));


    }
}