package re.breathpray.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.triggertrap.seekarc.SeekArc;

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
    private final int MAXSEEKARCRANGE = 288; // Hence in 5 minute steps
    private final int gridInMinutes = 24 * 60 / MAXSEEKARCRANGE;
    private final int numberOfGridPerHour = MAXSEEKARCRANGE/24;
    private int startDayAt = gridInMinutes * numberOfGridPerHour * 6; //start default at 6 o'clock
    private int endDayAt = gridInMinutes * numberOfGridPerHour * 22; //end default at 22 o'clock
    private int duration = endDayAt-startDayAt;
    private SeekArc seekArcEnd;
    private SeekArc seekArkStart;

    public final static String dayName= "re.breathpray.com.EditDayActivity.dayName";
    public final static String dayDescription= "re.breathpray.com.EditDayActivity.dayDescription";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.editday);

        Intent intent = getIntent();


        seekarcsListWithFirstElementsOnForeground = new LinkedList<SeekArc>();
        ((TextView) this.findViewById(R.id.nameOfEditedDay)).setText(intent.getStringExtra(dayName));
        ((TextView) this.findViewById(R.id.description)).setText(intent.getStringExtra(dayDescription));

        //LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.circleLayout);
        //linearLayout.addView(this.findViewById(R.id.progressBar));
        seekArcEnd = (SeekArc) this.findViewById(R.id.seekArcStart);
        seekArkStart = (SeekArc) this.findViewById(R.id.seekArcEnd);

        seekArcEnd.setProgressWidth(11);
        seekArcEnd.setTouchInSide(false);
        seekArcEnd.setStartAngle(0);
        seekArcEnd.setSweepAngle(MAXSEEKARCRANGE / 2);
        seekArcEnd.setClockwise(true);
        seekArcEnd.setThumbInvisible();
        seekArcEnd.setVisibility(View.INVISIBLE);


        seekArcEnd.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                endDayAt = progress + seekArkStart.getSweepAngle();
                ((Button) activity.findViewById(R.id.toggleFocusSeekarcButton)).setText("click me!\n" +
                        startDayAt / numberOfGridPerHour + ":" + (startDayAt % numberOfGridPerHour) * gridInMinutes +
                        " - " + endDayAt/numberOfGridPerHour + ":" + (endDayAt%numberOfGridPerHour)* gridInMinutes);
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            }
        });

        seekArkStart.setProgressWidth(11);
        seekArkStart.setTouchInSide(false);
        seekArkStart.setStartAngle(0);
        seekArkStart.setSweepAngle(MAXSEEKARCRANGE / 2);
        seekArkStart.setClockwise(false);

        seekArkStart.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                startDayAt = seekArkStart.getSweepAngle() - progress;
                ((Button) activity.findViewById(R.id.toggleFocusSeekarcButton)).setText("click me!\n"
                        + startDayAt / numberOfGridPerHour + ":" + (startDayAt % numberOfGridPerHour) * gridInMinutes +
                        " - " + endDayAt/numberOfGridPerHour + ":" + (endDayAt%numberOfGridPerHour)* gridInMinutes);
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
            }
        });


        seekarcsListWithFirstElementsOnForeground.add(seekArcEnd);
        seekarcsListWithFirstElementsOnForeground.addFirst(seekArkStart);
    }

    public void onSeekarcFocusChangeButtonClicked(View view){
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

            newFirst.setStartAngle(-((int)first.getProgress() + first.getStartAngle()));
            newFirst.setSweepAngle( MAXSEEKARCRANGE/2 - newFirst.getStartAngle());
            newFirst.setProgress((int)newFirst.getProgress()-newFirst.getStartAngle());
        }
    }

    public void onExitDayActivityClicked(View view){
        finish();
    }

}
