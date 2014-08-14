package re.breathpray.com;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

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
