package com.java.menu.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.java.menu.core.Feature;
import com.java.menu.core.FeatureManager;
import com.java.menu.util.Theme;

public final class ModMenuView extends LinearLayout
{
    public interface OnCloseListener
    {
        void onClose();
    }

    private final View header;
    private OnCloseListener closeListener;

    public ModMenuView(Context c, FeatureManager features)
    {
        super(c);
        setOrientation(VERTICAL);
        int pad = Theme.dp(c, 14);
        setPadding(pad, pad, pad, pad);

        GradientDrawable bg = new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Theme.BG_START, Theme.BG_END});
        bg.setCornerRadius(Theme.dp(c, 16));
        bg.setStroke(Theme.dp(c, 1), Theme.STROKE);
        setBackground(bg);

        header = buildHeader(c);

        View divider = new View(c);
        divider.setBackgroundColor(Theme.STROKE);

        LinearLayout body = new LinearLayout(c);
        body.setOrientation(VERTICAL);
        body.setPadding(0, Theme.dp(c, 8), 0, 0);
        for (Feature f : features.all())
        {
            body.addView(new FeatureRow(c, f));
        }

        ScrollView scroll = new ScrollView(c);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scroll.addView(body, new ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Theme.dp(c, 208));
        scroll.setLayoutParams(slp);

        addView(header);
        addView(divider, new LayoutParams(LayoutParams.MATCH_PARENT, Theme.dp(c, 1)));
        addView(scroll);
    }

    private View buildHeader(Context c)
    {
        LinearLayout h = new LinearLayout(c);
        h.setOrientation(HORIZONTAL);
        h.setGravity(Gravity.CENTER_VERTICAL);
        int p = Theme.dp(c, 4);
        h.setPadding(p, p, p, Theme.dp(c, 10));

        ImageView logo = new ImageView(c);
        int id = c.getResources().getIdentifier(Theme.LOGO_NAME, Theme.LOGO_TYPE, c.getPackageName());
        if (id != 0)
        {
            logo.setImageResource(id);
        }
        else
        {
            GradientDrawable lb = new GradientDrawable();
            lb.setShape(GradientDrawable.OVAL);
            lb.setColor(Theme.ACCENT);
            logo.setBackground(lb);
        }

        h.addView(logo, new LayoutParams(Theme.dp(c, 34), Theme.dp(c, 34)));

        TextView title = new TextView(c);
        title.setText("Furkan Cheat");
        title.setTextColor(Theme.TEXT);
        title.setTextSize(15);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setLetterSpacing(0.08f);
        LayoutParams tlp = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
        tlp.leftMargin = Theme.dp(c, 12);
        h.addView(title, tlp);

        final TextView close = new TextView(c);
        close.setText("X");
        close.setTextColor(Theme.MUTED);
        close.setTextSize(16);
        int cp = Theme.dp(c, 8);
        close.setPadding(cp, Theme.dp(c, 4), cp, Theme.dp(c, 4));

        close.setOnTouchListener
        (
            new OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent e)
                {
                    switch (e.getActionMasked())
                    {
                        case MotionEvent.ACTION_DOWN:
                            close.setTextColor(Theme.DANGER);
                        break;

                        case MotionEvent.ACTION_UP:
                            close.setTextColor(Theme.MUTED);
                            if (closeListener != null)
                                closeListener.onClose();
                        break;

                        case MotionEvent.ACTION_CANCEL:
                            close.setTextColor(Theme.MUTED);
                        break;
                    }
                    return true;
                }
            }
        );

        h.addView(close);
        return h;
    }

    public View getHeader()
    {
        return header;
    }

    public void setOnCloseListener(OnCloseListener l)
    {
        this.closeListener = l;
    }
}