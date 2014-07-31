package re.breathpray.com;

/**
 * User: Eisl
 * Date: 31.07.14
 */
public class BreathPrayConstants {
    /**
     * the duration of a single cycle which will be repeated until the full "duration of a reminder" = {@link #vibrationCycleDuration} has been completed
     */
    public final static int vibrationCycleDuration = 200;
    public final static String defaultCategory = "re.breathpray.com.VibrationAttributesManager.BreathPrayDefaultCategory";
    public final static String defaultCyclicVibrationServiceAction = "re.breathpray.com.VibrationAttributesManager.BreathPrayVibrationAction";
    public final static String defaultVibrationRepeaterServiceAction = "re.breathpray.com.VibrationAttributesManager.BreathPrayVibrationRepeaterAction";
    public final static String defaultActivityAction = "re.breathpray.com.VibrationAttributesManager.BreathPrayActivityAction";
    public final static String intervalIntentExtraFieldName = "interval";
    public final static String loopEndlessExecuteIntentExtraFieldName = "loopEndlessExecute";
    public final static String durationIntentExtraFieldName = "duration";
    public final static int MAXSEEKARCRANGE = 288; // Hence in 5 minute steps
    public final static int numberOfGridPerHour = MAXSEEKARCRANGE / 24;
    public final static int gridInMinutes = 24 * 60 / MAXSEEKARCRANGE;
    public final static String dayName = "EditDayActivity.dayName";

    public final static String startVibrationIntentExtraFieldName = "startVibration";

    public final static String endVibrationIntentExtraFieldName = "endVibration";
}
