package eci.officeshopper.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import eci.officeshopper.R;

public class ListenerFrameLayout extends FrameLayout {
    private boolean mConsumeEvents = false;
    public ListenerFrameLayout(Context context){
        super(context);
    }

    public ListenerFrameLayout(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public ListenerFrameLayout(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (mConsumeEvents) {
            this.callOnClick();
            return !mConsumeEvents;
        }
        return super.dispatchTouchEvent(e);
    }

    public void consumeEvents (boolean consume) {
        this.mConsumeEvents = consume;
    }
}
