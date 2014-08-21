package re.breathpray.com;

import android.view.View;

public class ViewHelper {

    public static void pulsView(final View view) {
        final float stepSize = 0.2f;
        final float minAlpha = 0.2f;
        final int timeStepsInMillis = 50;

        final Runnable increaseAlpha = new Runnable() {
            @Override
            public void run() {
                view.setAlpha(view.getAlpha() + stepSize);
            }
        };
        final Runnable decreaseAlpha = new Runnable() {
            @Override
            public void run() {
                view.setAlpha(view.getAlpha() - stepSize);
            }
        };

        for(int i = 0; i < (1-minAlpha)/stepSize; i++)
            if (view != null)
                view.postDelayed(decreaseAlpha,i*timeStepsInMillis);

        for(int i = 0; i < (1-minAlpha)/stepSize; i++)
            if (view != null)
                view.postDelayed(increaseAlpha,i*timeStepsInMillis + (int) ((1 - minAlpha) / stepSize * timeStepsInMillis));


    }
}
