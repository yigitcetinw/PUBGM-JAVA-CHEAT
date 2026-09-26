package com.java.menu.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.java.menu.util.Theme;

public final class ToggleSwitch extends View
{

    public interface OnCheckedChangeListener
    {
        void onCheckedChanged(ToggleSwitch v, boolean checked);
    }

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final float density;
    private final float trackW;
    private final float trackH;
    private final float thumbR;
    private final float pad;
    private final RectF rect = new RectF();

    private float progress = 0f;
    private boolean checked = false;
    private ValueAnimator animator;
    private OnCheckedChangeListener listener;

    public ToggleSwitch(Context c)
    {
        super(c);
        density = c.getResources().getDisplayMetrics().density;
        trackW = 44 * density;
        trackH = 24 * density;
        thumbR = 9 * density;
        pad = 3 * density;

        trackPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setColor(Color.WHITE);
        thumbPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(0x40000000);
        shadowPaint.setStyle(Paint.Style.FILL);

        setClickable(true);
        setFocusable(true);
    }

    @Override
    protected void onMeasure(int wSpec, int hSpec)
    {
        setMeasuredDimension(
        resolveSize((int) trackW, wSpec),
        resolveSize((int) trackH, hSpec));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        float w = getWidth();
        float h = getHeight();
        float r = h / 2f;

        trackPaint.setColor(blend(Theme.TRACK_OFF, Theme.RED, progress));
        rect.set(0, 0, w, h);
        canvas.drawRoundRect(rect, r, r, trackPaint);

        float cx = pad + thumbR + (w - 2 * (pad + thumbR)) * progress;
        float cy = h / 2f;
        canvas.drawCircle(cx, cy + density, thumbR, shadowPaint);
        canvas.drawCircle(cx, cy, thumbR, thumbPaint);
    }

    @Override
    public boolean performClick()
    {
        toggle();
        return super.performClick();
    }

    public void toggle()
    {
        setChecked(!checked, true);
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean value, boolean animate)
    {
        if (this.checked == value && progress == (value ? 1f : 0f)) return;
        this.checked = value;

        if (animator != null)
        {
            animator.cancel();
            animator = null;
        }

        if (animate)
        {
            animator = ValueAnimator.ofFloat(progress, value ? 1f : 0f);
            animator.setDuration(180);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener
            (
                new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator a)
                    {
                        progress = (float) a.getAnimatedValue();
                        invalidate();
                    }
                }
            );
            animator.start();
        }
        else
        {
            progress = value ? 1f : 0f;
            invalidate();
        }

        if (listener != null)
            listener.onCheckedChanged(this, checked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l)
    {
        this.listener = l;
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if (animator != null)
        {
            animator.cancel();
            animator = null;
        }
        listener = null;
    }

    private static int blend(int a, int b, float t)
    {
        int ar = (a >> 16) & 0xFF;
        int ag = (a >> 8) & 0xFF;
        int ab = a & 0xFF;
        int br = (b >> 16) & 0xFF;
        int bg = (b >> 8) & 0xFF;
        int bb = b & 0xFF;
        int r = (int) (ar + (br - ar) * t);
        int g = (int) (ag + (bg - ag) * t);
        int bl = (int) (ab + (bb - ab) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | bl;
    }
}
