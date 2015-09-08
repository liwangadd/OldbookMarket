package com.yunjian.view;

import com.yunjian.view.ScrollListener;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView
{
	private ScrollListener listener = null;
	public MyScrollView(Context context) {  
        super(context);  
        mGestureDetector = new GestureDetector(new YScrollDetector());  
        setFadingEdgeLength(0);  
    }  
  
    public MyScrollView(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        mGestureDetector = new GestureDetector(new YScrollDetector());  
        setFadingEdgeLength(0);  
    }  
  
    public MyScrollView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        mGestureDetector = new GestureDetector(new YScrollDetector());  
        setFadingEdgeLength(0);  
    }  
    public void setScrollListener(ScrollListener listener)
	{
		this.listener = listener;
	}
  
    private GestureDetector mGestureDetector;  
    View.OnTouchListener mGestureListener;  
  
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
        return super.onInterceptTouchEvent(ev)  
                && mGestureDetector.onTouchEvent(ev);  
    }  
    @Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt)
	{
		super.onScrollChanged(l, t, oldl, oldt);
		if(listener != null)
		{
			listener.ScrollChanged(getScrollX(), getScrollY());
		}
	}
  
    class YScrollDetector extends SimpleOnGestureListener {  
        @Override  
        public boolean onScroll(MotionEvent e1, MotionEvent e2,  
                float distanceX, float distanceY) {  
            if (distanceY != 0 && distanceX != 0) {  
  
            }  
            if (Math.abs(distanceY) >= Math.abs(distanceX)) {  
                return true;  
            }  
            return false;  
        }  
    }  
}
