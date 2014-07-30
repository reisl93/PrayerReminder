package re.breathpray.com;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created with IntelliJ IDEA.
 * User: Eisl
 * Date: 30.07.14
 * Time: 02:16
 * To change this template use File | Settings | File Templates.
 */
public class RelativeLayoutForCircle extends RelativeLayout {
    public RelativeLayoutForCircle(Context context) {
        super(context);
    }

    public RelativeLayoutForCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeLayoutForCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;
        setMeasuredDimension(size, size);
    }
}
