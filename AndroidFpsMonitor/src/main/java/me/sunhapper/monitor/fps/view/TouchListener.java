package me.sunhapper.monitor.fps.view;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by sunhapper on 2019/3/19 .
 */
public class TouchListener implements View.OnTouchListener {
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    private WindowManager.LayoutParams params;
    private WindowManager windowManager;

    public TouchListener(WindowManager.LayoutParams params, WindowManager windowManager) {
        this.params = params;
        this.windowManager = windowManager;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = params.x;
                initialY = params.y;
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                params.x = initialX + (int) (event.getRawX() - initialTouchX);
                params.y = initialY + (int) (event.getRawY() - initialTouchY);
                windowManager.updateViewLayout(v, params);
                break;
        }
        return false;
    }
}
