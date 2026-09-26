package com.java.menu.ui;

import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public final class DragController implements View.OnTouchListener
{
    public interface ClickHandler
    {
        void onClick();
    }

    private final WindowManager wm;
    private final View target;
    private final WindowManager.LayoutParams params;
    private final float threshold;
    private final ClickHandler click;

    private float touchX;
    private float touchY;
    private int startX;
    private int startY;
    private boolean dragged;

    public DragController(WindowManager wm, View target, WindowManager.LayoutParams params,
    float thresholdPx, ClickHandler click)
    {
        this.wm = wm;
        this.target = target;
        this.params = params;
        this.threshold = thresholdPx;
        this.click = click;
    }

    @Override
    public boolean onTouch(View v, MotionEvent e)
    {
        switch (e.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
            touchX = e.getRawX();
            touchY = e.getRawY();
            startX = params.x;
            startY = params.y;
            dragged = false;
            return true;

            case MotionEvent.ACTION_MOVE:
            float dx = e.getRawX() - touchX;
            float dy = e.getRawY() - touchY;
            if (!dragged && Math.hypot(dx, dy) > threshold) dragged = true;
            if (dragged)
            {
                DisplayMetrics dm = new DisplayMetrics();
                wm.getDefaultDisplay().getRealMetrics(dm);
                int maxX = dm.widthPixels - target.getWidth();
                int maxY = dm.heightPixels - target.getHeight();
                if (maxX < 0) maxX = 0;
                if (maxY < 0) maxY = 0;
                params.x = clamp(startX + (int) dx, 0, maxX);
                params.y = clamp(startY + (int) dy, 0, maxY);
                wm.updateViewLayout(target, params);
            }
            return true;

            case MotionEvent.ACTION_UP:
            if (!dragged && click != null) click.onClick();
                return true;
        }
        return false;
    }

    private static int clamp(int v, int min, int max)
    {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }
}