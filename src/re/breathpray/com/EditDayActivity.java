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
 * Created with IntelliJ IDEA.
 * User: Eisl
 * Date: 30.07.14
 * Time: 01:31
 * To change this template use File | Settings | File Templates.
 */
public class EditDayActivity extends Activity {

    private final Activity activity = this;

    private LinkedList<SeekArc> seekarcsListWithFirstElementsOnForeground;

    public final int MAXSEEKARCRANGE = 288; // Hence in 5 minute steps
    private final int gridInMinutes = 24 * 60 / MAXSEEKARCRANGE;
    private final int numberOfGridPerHour = MAXSEEKARCRANGE / 24;

    private int startDayAt = numberOfGridPerHour * 6; //start default at 6 o'clock
    private int endDayAt = numberOfGridPerHour * 22; //end default at 22 o'clock

    private SeekArc seekArcEnd;
    private SeekArc seekArkStart;
    private String currentDayName;

    public final static String dayName = "EditDayActivity.dayName";
    private final static String TAG = "EditDayActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.editday);

        Intent intent = getIntent();

        seekarcsListWithFirstElementsOnForeground = new LinkedList<SeekArc>();
        currentDayName = intent.getStringExtra(dayName);
        ((TextView) this.findViewById(R.id.nameOfEditedDay)).setText(currentDayName);

        startDayAt = intent.getIntExtra("Start",6*numberOfGridPerHour);
        endDayAt = intent.getIntExtra("End",22*numberOfGridPerHour);

        seekArcEnd = (SeekArc) this.findViewById(R.id.seekArcStart);
        seekArkStart = (SeekArc) this.findViewById(R.id.seekArcEnd);

        seekarcsListWithFirstElementsOnForeground.add(seekArcEnd);
        seekarcsListWithFirstElementsOnForeground.addFirst(seekArkStart);

        //requires some toggles unit it is levelled off
        initSeekArcEnd();
        initSeekArkStart();

        seekArkStart.setArcRotation(MAXSEEKARCRANGE/2 - endDayAt);
        seekArkStart.setSweepAngle(MAXSEEKARCRANGE / 2 - seekArkStart.getArcRotation());
        seekArkStart.setMax(seekArkStart.getSweepAngle());
        seekArkStart.setProgress(seekArkStart.getSweepAngle()-startDayAt);

        seekArcEnd.setArcRotation(MAXSEEKARCRANGE/2-startDayAt);
        seekArcEnd.setSweepAngle(MAXSEEKARCRANGE/2-seekArcEnd.getArcRotation());
        seekArcEnd.setMax(seekArcEnd.getSweepAngle());
        seekArcEnd.setProgress(endDayAt - startDayAt);

        seekArcEnd.invalidate();
        seekArcEnd.setVisibility(View.INVISIBLE);
        seekArcEnd.setThumbInvisible();
        seekArkStart.invalidate();
        seekArkStart.setVisibility(View.VISIBLE);
        seekArkStart.setThumbVisible();

    }

    private void initSeekArkStart() {
        seekArkStart.setProgressWidth(11);
        seekArkStart.setTouchInSide(false);
        seekArkStart.setSweepAngle(MAXSEEKARCRANGE / 2);
        seekArkStart.setClockwise(false);
        seekArkStart.setArcRotation(0);
        seekArkStart.setMax(MAXSEEKARCRANGE / 2);

        seekArkStart.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                startDayAt = seekArkStart.getSweepAngle() - progress;
                ((TextView) activity.findViewById(R.id.startTimeTextInButton)).setText(
                        new LocalTime()
                                .hourOfDay().setCopy(startDayAt / numberOfGridPerHour)
                                .minuteOfHour().setCopy((startDayAt % numberOfGridPerHour) * gridInMinutes)
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
        seekArcEnd.setSweepAngle(MAXSEEKARCRANGE / 2);
        seekArcEnd.setClockwise(true);
        seekArcEnd.setArcRotation(0);
        seekArcEnd.setMax(MAXSEEKARCRANGE / 2);

        seekArcEnd.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                endDayAt = progress + seekArkStart.getSweepAngle() - (int) seekArkStart.getProgressAngle();
                //24:00 not possible -> capture
                if (endDayAt >= MAXSEEKARCRANGE) {
                    endDayAt -= 1;
                }
                ((TextView) activity.findViewById(R.id.endTimeTextInButton)).setText(
                        new LocalTime()
                                .hourOfDay().setCopy(endDayAt / numberOfGridPerHour)
                                .minuteOfHour().setCopy((endDayAt % numberOfGridPerHour) * gridInMinutes)
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

            int oldStartAngle = newFirst.getArcRotation();
            newFirst.setArcRotation(-((int) first.getProgressAngle() + first.getArcRotation()));
            newFirst.setSweepAngle(MAXSEEKARCRANGE / 2 - newFirst.getArcRotation());
            newFirst.setMax(newFirst.getSweepAngle());
            newFirst.setProgress((int) newFirst.getProgressAngle() + (oldStartAngle - newFirst.getArcRotation()));
        }
    }

    public void onExitDayActivityClicked(View view) {

        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.PREFERENCEFILE),MODE_PRIVATE).edit();
        editor.putInt(currentDayName+"Start",startDayAt);
        editor.putInt(currentDayName+"End", endDayAt);
        while (!editor.commit())
            SystemClock.sleep(10);


        finish();
    }

}
