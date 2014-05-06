package RE.PrayerReminder;

/**
 * @author: Eisl
 * Date: 01.05.14
 * Time: 18:20
 * @version: 1.0.0
 */
public class ConfigurationManager {

    private int repeatTime = 1;
    private int vibrationTime = 16;
    private int startHour = 6;
    private int startMinute = 0;
    private int endHour = 22;
    private int endMinute = 0;
    private int vibrationStrength = 150;
    private int takeABreakTime = 60;
    private long lastVibrate = System.currentTimeMillis();
    private long nextVibrate = System.currentTimeMillis();
    private boolean appIsActive = true;

    public int getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(final int repeatTime) {
        this.repeatTime = repeatTime;
    }

    public int getVibrationTime() {
        return vibrationTime;
    }

    public void setVibrationTime(final int vibrationTime) {
        this.vibrationTime = vibrationTime;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(final int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(final int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(final int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(final int endMinute) {
        this.endMinute = endMinute;
    }

    public void setVibrationStrength(final int vibrationStrength) {
        this.vibrationStrength = vibrationStrength;
    }

    public int getVibrationStrength() {
        return vibrationStrength;
    }

    public void setTakeABreak(final int time) {
        this.takeABreakTime = time;
    }

    public int getTakeABreakTime() {
        return takeABreakTime;
    }

    public void setLastVibrate(long aLong) {
        this.lastVibrate = aLong;
    }

    public Long getLastVibrate() {
        return lastVibrate;
    }

    public Long getNextVibrate() {
        return nextVibrate;
    }

    public void setNextVibrate(final long nextVibrate) {
        this.nextVibrate = nextVibrate;
    }

    public void setAppIsActive(final boolean aBoolean) {
        this.appIsActive = aBoolean;
    }

    public boolean isAppIsActive() {
        return appIsActive;
    }
}
