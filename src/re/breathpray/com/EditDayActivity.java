package re.breathpray.com;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.triggertrap.seekarc.SeekArc;
import org.joda.time.LocalTime;

import java.util.LinkedList;
import java.util.Locale;

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

    public final static String dayName = "EditDayActivity.dayName";
    public final static String dayDescription = "EditDayActivity.dayDescription";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.editday);

        Intent intent = getIntent();

        seekarcsListWithFirstElementsOnForeground = new LinkedList<SeekArc>();
        ((TextView) this.findViewById(R.id.nameOfEditedDay)).setText(intent.getStringExtra(dayName));
        ((TextView) this.findViewById(R.id.description)).setText(intent.getStringExtra(dayDescription));

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
        seekArcEnd.setProgress(seekArkStart.getSweepAngle()-endDayAt);

        seekArcEnd.invalidate();
        seekArcEnd.setVisibility(View.INVISIBLE);
        seekArcEnd.setThumbInvisible();
        seekArkStart.invalidate();
        seekArkStart.setVisibility(View.VISIBLE);
        seekArkStart.setThumbVisible();

        onSeekarcFocusChangeButtonClicked(null);
    }

    private void initSeekArkStart() {
        seekArkStart.setProgressWidth(11);
        seekArkStart.setTouchInSide(false);
        seekArkStart.setSweepAngle(MAXSEEKARCRANGE / 2);
        seekArkStart.setClockwise(false);
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
        seekArcEnd.setThumbVisible();
        seekArcEnd.setVisibility(View.INVISIBLE);
        seekArkStart.setMax(MAXSEEKARCRANGE / 2);

        seekArcEnd.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                endDayAt = progress + seekArkStart.getSweepAngle() - (int) seekArkStart.getProgressAngle();
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

        final Intent data = new Intent();
        data.putExtra("Start",startDayAt);
        data.putExtra("End",endDayAt);

        if(getParent() == null){
            setResult(RESULT_OK,data);
        } else {
            getParent().setResult(RESULT_OK,data);
        }
/*
        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.PREFERENCEFILE),MODE_PRIVATE).edit();
        editor.putInt(name+"Start",data.getIntExtra("Start",6*12));
        editor.putInt(name + "End", data.getIntExtra("End", 22 * 12));
        editor.apply();*/

        finish();
    }

}
