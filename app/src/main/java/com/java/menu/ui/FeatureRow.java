package com.java.menu.ui;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.java.menu.core.Feature;
import com.java.menu.util.Theme;

public final class FeatureRow extends LinearLayout
{
    private final Feature feature;
    private final ToggleSwitch toggle;
    private final GradientDrawable bg;

    public FeatureRow(Context c, Feature feature)
    {
        super(c);
        this.feature = feature;

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int p = Theme.dp(c, 12);
        setPadding(p, 0, p, 0);

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Theme.dp(c, 46));
        lp.bottomMargin = Theme.dp(c, 4);
        setLayoutParams(lp);

        bg = new GradientDrawable();
        bg.setColor(Theme.ROW_IDLE);
        bg.setCornerRadius(Theme.dp(c, 10));
        setBackground(bg);

        TextView label = new TextView(c);
        label.setText(feature.name());
        label.setTextColor(Theme.TEXT);
        label.setTextSize(13);
        LayoutParams llp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
        label.setLayoutParams(llp);

        toggle = new ToggleSwitch(c);
        toggle.setChecked(feature.isChecked(), false);
        toggle.setOnCheckedChangeListener
        (
            new ToggleSwitch.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(ToggleSwitch v, boolean checked)
                {
                    feature.setChecked(checked);
                }
            }
        );

        setClickable(true);
        setOnClickListener
        (
            new OnClickListener()
            {
                @Override
                public void onClick(android.view.View v)
                {
                    toggle.toggle();
                }
            }
        );

        setOnTouchListener
        (
            new OnTouchListener()
            {
                @Override
                public boolean onTouch(android.view.View v, MotionEvent e)
                {
                    switch (e.getActionMasked())
                    {
                        case MotionEvent.ACTION_DOWN:
                        bg.setColor(Theme.ROW_PRESSED);
                        break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                        bg.setColor(Theme.ROW_IDLE);
                        break;
                    }
                    return false;
                }
            }
        );

        addView(label);
        addView(toggle);
    }

    public Feature feature()
    {
        return feature;
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        setOnClickListener(null);
        setOnTouchListener(null);
    }
}