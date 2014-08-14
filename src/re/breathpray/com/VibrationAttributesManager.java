package re.breathpray.com;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Date: 01.05.14
 * Time: 18:20
 */
public class VibrationAttributesManager {

    private int repeatTime = 1;
    private int duration = 16;
    private int pattern = 150;
    private int start = 6 * 12;
    private int end = 22 * 12;
    private int volume = 500;
    private boolean volumeActive = false;
    private boolean acousticNotificationActive = false;
    private String acousticNotificationUri = "";
    private boolean appIsActive = true;

    private final Context context;

    public VibrationAttributesManager(final Context context) {
        if (context == null)
            throw new IllegalArgumentException("Context must not be null");
        this.context = context;

    }

    public int getRepeatTime() {
        return repeatTime;
    }

    public int getDuration() {
        return duration;
    }

    public void reloadCurrentData() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.PREFERENCEFILE), Activity.MODE_PRIVATE);

        start = sharedPreferences.getInt(getCurrentDay() + "Start", 6 * 12);
        end = sharedPreferences.getInt(getCurrentDay() + "End", 22 * 12);

        appIsActive = sharedPreferences.getBoolean(BreathPrayConstants.keyIsAppActive, false);
        repeatTime = sharedPreferences.getInt(BreathPrayConstants.keyVibrationRepeatTime, 15);
        duration = sharedPreferences.getInt(BreathPrayConstants.keyVibrationDuration, 16);
        pattern = sharedPreferences.getInt(BreathPrayConstants.keyVibrationPattern, 15);
        volume = sharedPreferences.getInt(BreathPrayConstants.keyNotificationVolume,500);
        volumeActive = sharedPreferences.getBoolean(BreathPrayConstants.keyUniqueVolumeActive, false);
        acousticNotificationUri = sharedPreferences.getString(BreathPrayConstants.keyAcousticNotificationUri, "");
        acousticNotificationActive = sharedPreferences.getBoolean(BreathPrayConstants.keyAcousticIsActive, false);
    }

    public String getCurrentDay(){
        final Map<Integer, String> jodaTimeWeekdayIntegerToStringMapping = new HashMap<Integer, String>() {{
            put(DateTimeConstants.MONDAY, context.getString(R.string.monday));
            put(DateTimeConstants.TUESDAY, context.getString(R.string.tuesday));
            put(DateTimeConstants.WEDNESDAY, context.getString(R.string.wednesday));
            put(DateTimeConstants.THURSDAY, context.getString(R.string.thursday));
            put(DateTimeConstants.FRIDAY, context.getString(R.string.friday));
            put(DateTimeConstants.SATURDAY, context.getString(R.string.saturday));
            put(DateTimeConstants.SUNDAY, context.getString(R.string.sunday));
        }};

        return jodaTimeWeekdayIntegerToStringMapping.get(DateTime.now().getDayOfWeek());
    }

    public boolean isAppIsActive() {
        return appIsActive;
    }

    /**
     * @return the Offset from midnight to the start of the vibration of the current weekday in millis
     */
    public int getStart() {
        return start * BreathPrayConstants.gridInMinutes * 60 *1000;
    }

    /**
     * @return the Offset from midnight to the end of the vibration of the current weekday in millis
     */
    public int getEnd() {
        return end * BreathPrayConstants.gridInMinutes * 60 *1000;
    }

    public int getPattern() {
        return pattern;
    }

    public int getVolume() {
        return volume;
    }

    public boolean isVolumeActive() {
        return volumeActive;
    }

    public boolean isAcousticNotificationActive() {
        return acousticNotificationActive;
    }

    public String getAcousticNotificationUri() {
        return acousticNotificationUri;
    }
}
