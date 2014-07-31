package re.breathpray.com;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.triggertrap.seekarc.SeekArc;
import org.joda.time.LocalTime;

import java.util.LinkedList;

/**
 * Date: 30.07.14
 */
public class EditDayActivity extends Activity {

    private final Activity activity = this;

    private LinkedList<SeekArc> seekarcsListWithFirstElementsOnForeground;

    private int startDayAt = BreathPrayConstants.numberOfGridPerHour * 6; //start default at 6 o'clock
    private int endDayAt = BreathPrayConstants.numberOfGridPerHour * 22; //end default at 22 o'clock

    private SeekArc seekArcEnd;
    private SeekArc seekArkStart;
    private String currentDayName;

    private final static String TAG = "EditDayActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.editday);

        Intent intent = getIntent();

        seekarcsListWithFirstElementsOnForeground = new LinkedList<SeekArc>();
        currentDayName = intent.getStringExtra(BreathPrayConstants.dayName);
        final TextView textView = (TextView) this.findViewById(R.id.nameOfEditedDay);
        textView.setText(currentDayName);


        startDayAt = intent.getIntExtra("Start", 6 * BreathPrayConstants.numberOfGridPerHour);
        endDayAt = intent.getIntExtra("End", 22 * BreathPrayConstants.numberOfGridPerHour);

        seekArcEnd = (SeekArc) this.findViewById(R.id.seekArcStart);
        seekArkStart = (SeekArc) this.findViewById(R.id.seekArcEnd);

        seekarcsListWithFirstElementsOnForeground.add(seekArkStart);
        seekarcsListWithFirstElementsOnForeground.addFirst(seekArcEnd);

        //requires some toggles unit it is levelled off
        initSeekArcEnd();
        initSeekArkStart();

        seekArkStart.setArcRotation(BreathPrayConstants.MAXSEEKARCRANGE / 2 - endDayAt);
        seekArkStart.setSweepAngle(BreathPrayConstants.MAXSEEKARCRANGE / 2 - seekArkStart.getArcRotation());
        seekArkStart.setMax(seekArkStart.getSweepAngle());
        seekArkStart.setProgress(seekArkStart.getSweepAngle() - startDayAt);

        seekArcEnd.setArcRotation(startDayAt - BreathPrayConstants.MAXSEEKARCRANGE / 2);
        seekArcEnd.setSweepAngle(BreathPrayConstants.MAXSEEKARCRANGE / 2 - seekArcEnd.getArcRotation());
        seekArcEnd.setMax(seekArcEnd.getSweepAngle());
        seekArcEnd.setProgress(endDayAt - startDayAt);

        seekArkStart.invalidate();
        seekArkStart.setVisibility(View.INVISIBLE);
        seekArkStart.setThumbInvisible();
        seekArcEnd.invalidate();
        seekArcEnd.setVisibility(View.VISIBLE);
        seekArcEnd.setThumbVisible();

    }

    private void initSeekArkStart() {
        seekArkStart.setProgressWidth(11);
        seekArkStart.setTouchInSide(false);
        seekArkStart.setSweepAngle(BreathPrayConstants.MAXSEEKARCRANGE / 2);
        seekArkStart.setClockwise(false);
        seekArkStart.setArcRotation(0);
        seekArkStart.setMax(BreathPrayConstants.MAXSEEKARCRANGE / 2);

        seekArkStart.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                startDayAt = seekArkStart.getSweepAngle() - progress;
                ((TextView) activity.findViewById(R.id.startTimeTextInButton)).setText(
                        new LocalTime()
                                .hourOfDay().setCopy(startDayAt / BreathPrayConstants.numberOfGridPerHour)
                                .minuteOfHour().setCopy((startDayAt % BreathPrayConstants.numberOfGridPerHour) * BreathPrayConstants.gridInMinutes)
                                .toString(getString(R.string.timePattern)));
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            }
        });
    }

    private void initSeekArcEnd() {
        seekArcEnd.setProgressWidth(11);
        seekArcEnd.setTouchInSide(false);
        seekArcEnd.setSweepAngle(BreathPrayConstants.MAXSEEKARCRANGE / 2);
        seekArcEnd.setClockwise(true);
        seekArcEnd.setArcRotation(0);
        seekArcEnd.setMax(BreathPrayConstants.MAXSEEKARCRANGE / 2);

        seekArcEnd.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                endDayAt = progress + startDayAt;
                //24:00 not possible -> capture
                if (endDayAt >= BreathPrayConstants.MAXSEEKARCRANGE) {
                    endDayAt -= 1;
                }
                ((TextView) activity.findViewById(R.id.endTimeTextInButton)).setText(
                        new LocalTime()
                                .hourOfDay().setCopy(endDayAt / BreathPrayConstants.numberOfGridPerHour)
                                .minuteOfHour().setCopy((endDayAt % BreathPrayConstants.numberOfGridPerHour) * BreathPrayConstants.gridInMinutes)
                                .toString(getString(R.string.timePattern))
                );
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            }
        });
    }

    public void onSeekarcFocusChangeButtonClicked(View view) {
        final SeekArc first = seekarcsListWithFirstElementsOnForeground.getFirst();
        final ViewGroup parent = (ViewGroup) first.getParent();
        if (null != parent) {
            parent.removeView(first);
            parent.addView(first, 0);
            first.setThumbInvisible();
            first.setVisibility(View.INVISIBLE);
            seekarcsListWithFirstElementsOnForeground.addLast(seekarcsListWithFirstElementsOnForeground.removeFirst());
            final SeekArc newFirst = seekarcsListWithFirstElementsOnForeground.getFirst();
            newFirst.setThumbVisible();
            newFirst.setVisibility(View.VISIBLE);

            int oldRotationAngle = newFirst.getArcRotation();
            newFirst.setArcRotation(-((int) first.getProgressAngle() + first.getArcRotation()));
            newFirst.setSweepAngle(BreathPrayConstants.MAXSEEKARCRANGE / 2 - newFirst.getArcRotation());
            newFirst.setMax(newFirst.getSweepAngle());
            newFirst.setProgress((int) newFirst.getProgressAngle() + (oldRotationAngle - newFirst.getArcRotation()));
        }
    }

    public void onExitDayActivityClicked(View view) {

        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.PREFERENCEFILE), MODE_PRIVATE).edit();
        editor.putInt(currentDayName + "Start", startDayAt);
        editor.putInt(currentDayName + "End", endDayAt);
        while (!editor.commit())
            SystemClock.sleep(10);


        finish();
    }

}
