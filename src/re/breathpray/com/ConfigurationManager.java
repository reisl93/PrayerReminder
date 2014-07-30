package re.breathpray.com;

import android.content.Context;
import android.content.SharedPreferences;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

/**
 * @author: Eisl
 * Date: 01.05.14
 * Time: 18:20
 * @version: 1.0.0
 */
public class ConfigurationManager {

    private int repeatTime = 1;
    private int vibrationDuration = 16;
    private int startHour = 6;
    private int startMinute = 0;
    private int endHour = 22;
    private int endMinute = 0;

    /**
     * {@value #vibrationCycleDuration}
     * the duration of a full reminder vibration pattern
     */
    private int vibrationtimeOfACycle = 150;

    private int takeABreakTime = 60;
    private boolean appIsActive = true;
    private final Context context;
    private int currentDayOfWeek = DateTimeConstants.MONDAY;

    /**
     * the duration of a single cycle which will be repeated until the full "duration of a reminder" = {@link #vibrationCycleDuration} has been completed
     */
    public final static int vibrationCycleDuration = 200;
    public final static String defaultCategory = "re.breathpray.com.ConfigurationManager.BreathPrayDefaultCategory";
    public final static String defaultCyclicVibrationServiceAction = "re.breathpray.com.ConfigurationManager.BreathPrayVibrationAction";
    public final static String defaultVibrationRepeaterServiceAction = "re.breathpray.com.ConfigurationManager.BreathPrayVibrationRepeaterAction";
    public final static String defaultActivityAction = "re.breathpray.com.ConfigurationManager.BreathPrayActivityAction";

    //note that index = DateTimeConstant.X - 1
    public final static int[] daysOfWeeks = new int [] {
        DateTimeConstants.MONDAY,
        DateTimeConstants.TUESDAY,
        DateTimeConstants.WEDNESDAY,
        DateTimeConstants.THURSDAY,
        DateTimeConstants.FRIDAY,
        DateTimeConstants.SATURDAY,
        DateTimeConstants.SUNDAY};

    public ConfigurationManager(Context context) {
        this.context = context;
    }

    public int getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(final int repeatTime) {
        this.repeatTime = repeatTime;
    }

    public int getVibrationDuration() {
        return vibrationDuration;
    }

    public void setVibrationDuration(final int vibrationDuration) {
        this.vibrationDuration = vibrationDuration;
    }

    public int getStartHour() {
        alignToCurrentDate();
        return startHour;
    }

    public void setStartHour(final int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        alignToCurrentDate();
        return startMinute;
    }

    private void alignToCurrentDate() {
        if(DateTime.now().getDayOfWeek() != this.currentDayOfWeek){
            this.currentDayOfWeek = DateTime.now().getDayOfWeek();
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.PREFERENCEFILE), context.MODE_PRIVATE);
            startHour = sharedPreferences.getInt(context.getString(R.string.keyVibrationStartHour) + currentDayOfWeek, 6);
            startMinute = sharedPreferences.getInt(context.getString(R.string.keyVibrationStartMinute) + currentDayOfWeek, 0);
            endMinute = sharedPreferences.getInt(context.getString(R.string.keyVibrationEndMinute) + currentDayOfWeek, 22);
            endHour = sharedPreferences.getInt(context.getString(R.string.keyVibrationEndHour) + currentDayOfWeek, 0);
        }
    }

    public void setStartMinute(final int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        alignToCurrentDate();
        return endHour;
    }

    public void setEndHour(final int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        alignToCurrentDate();
        return endMinute;
    }

    public void setEndMinute(final int endMinute) {
        this.endMinute = endMinute;
    }

    public void setVibrationtimeOfACycle(final int vibrationtimeOfACycle) {
        this.vibrationtimeOfACycle = vibrationtimeOfACycle;
    }

    public int getVibrationtimeOfACycle() {
        return vibrationtimeOfACycle;
    }

    public void setTakeABreak(final int time) {
        this.takeABreakTime = time;
    }

    public int getTakeABreakTime() {
        return takeABreakTime;
    }

    public void setAppIsActive(final boolean aBoolean) {
        this.appIsActive = aBoolean;
    }

    public boolean isAppIsActive() {
        return appIsActive;
    }
}
