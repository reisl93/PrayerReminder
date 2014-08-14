package re.breathpray.com;

/**
 * User: Eisl
 * Date: 31.07.14
 */
public class BreathPrayConstants {
    /**
     * the duration of a single cycle which will be repeated until the full "duration of a reminder" = {@link #vibrationInterval} has been completed
     */
    public final static int vibrationInterval = 200;
    public final static String defaultCategory = "re.breathpray.com.VibrationAttributesManager.BreathPrayDefaultCategory";
    public final static String defaultCyclicVibrationServiceAction = "re.breathpray.com.VibrationAttributesManager.BreathPrayVibrationAction";
    public final static String defaultVibrationRepeaterServiceAction = "re.breathpray.com.VibrationAttributesManager.BreathPrayVibrationRepeaterAction";
    public final static String defaultActivityAction = "re.breathpray.com.VibrationAttributesManager.BreathPrayActivityAction";
    public final static String defaultFirstStartupActivityAction = "re.breathpray.com.VibrationAttributesManager.BreathPrayFirstStartupActivityAction";
    public final static String defaultEditDayAction = "re.breathpray.com.VibrationAttributesManager.BreathPrayEditDayActivityAction";
    public final static String patternIntentExtraFieldName = "re.breathpray.com.interval";
    public final static String loopEndlessExecuteIntentExtraFieldName = "re.breathpray.com.loopEndlessExecute";
    public final static String lastVibrationAtIntentExtraFieldName = "re.breathpray.com.lastVibrationAt";
    public final static String endVibrationAtIntentExtraFieldName = "re.breathpray.com.endVibrationAt";
    public final static String durationIntentExtraFieldName = "re.breathpray.com.duration";
    public final static String breakTimeIntentExtraFieldName = "re.breathpray.com.breakTime";
    public final static String acousticActiveIntentExtraFieldName = "re.breathpray.com.acousticActive";
    public final static String acousticVolumeIntentExtraFieldName = "re.breathpray.com.acousticVolume";
    public final static String acousticUriIntentExtraFieldName = "re.breathpray.com.acousticUri";
    public final static String acousticUniqueVolumeIntentExtraFieldName = "re.breathpray.com.acousticUniqueVolume";


    public final static String keyTakeABreakValue = "keyTakeABreakValue";
    public final static String keyIsAppActive = "keyIsAppActive";
    public final static String keyFirstStart = "keyFirstStartV3";
    public final static String keyVibrationDuration = "keyVibrationDuration";
    public final static String keyVibrationRepeatTime = "keyVibrationRepeatTime";
    public final static String keyVibrationPattern = "keyVibrationPattern";
    public final static String keyAcousticIsActive = "keyAcousticIsActive";
    public final static String keyNotificationVolume = "keyNotificationVolume";
    public final static String keyUniqueVolumeActive = "keyUniqueVolumeActive";
    public final static String keyAcousticNotificationUri = "keyAcousticNotificationUri";
    

    public final static int MAXSEEKARCRANGE = 288; // Hence in 5 minute steps
    public final static int numberOfGridPerHour = MAXSEEKARCRANGE / 24;
    public final static int gridInMinutes = 24 * 60 / MAXSEEKARCRANGE;
    public final static String dayName = "re.breathpray.com.EditDayActivity.dayName";

    public final static String startVibrationIntentExtraFieldName = "re.breathpray.com.startVibration";

    public final static String endVibrationIntentExtraFieldName = "re.breathpray.com.endVibration";
}
