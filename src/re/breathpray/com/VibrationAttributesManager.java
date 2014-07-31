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
    private int vibrationDuration = 16;
    private int start = 6 * 12;
    private int end = 22 * 12;
    private int takeABreakTime = 60;
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

    public int getVibrationDuration() {
        return vibrationDuration;
    }

    public void reloadCurrentData() {
        Map<Integer, String> jodaTimeWeekdayIntegerToStringMapping = new HashMap<Integer, String>() {{
            put(DateTimeConstants.MONDAY, context.getString(R.string.monday));
            put(DateTimeConstants.TUESDAY, context.getString(R.string.tuesday));
            put(DateTimeConstants.WEDNESDAY, context.getString(R.string.wednesday));
            put(DateTimeConstants.THURSDAY, context.getString(R.string.thursday));
            put(DateTimeConstants.FRIDAY, context.getString(R.string.friday));
            put(DateTimeConstants.SATURDAY, context.getString(R.string.saturday));
            put(DateTimeConstants.SUNDAY, context.getString(R.string.sunday));
        }};

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.PREFERENCEFILE), Activity.MODE_PRIVATE);

        //current day is based on locale time and not UTC as AlarmManager
        int currentDayOfWeek = DateTime.now().getDayOfWeek();
        start = sharedPreferences.getInt(jodaTimeWeekdayIntegerToStringMapping.get(currentDayOfWeek) + "Start", 6 * 12);
        end = sharedPreferences.getInt(jodaTimeWeekdayIntegerToStringMapping.get(currentDayOfWeek) + "End", 22 * 12);

        appIsActive = sharedPreferences.getBoolean(context.getString(R.string.keyIsAppActive), false);
        repeatTime = sharedPreferences.getInt(context.getString(R.string.keyVibrationRepeatTime), 15);
        vibrationDuration = sharedPreferences.getInt(context.getString(R.string.keyVibrationDuration), 16);
        takeABreakTime = sharedPreferences.getInt(context.getString(R.string.keyTakeABreakValue), 60);

    }

    public boolean isAppIsActive() {
        return appIsActive;
    }

    public int getTakeABreakTime() {
        return takeABreakTime;
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
}
