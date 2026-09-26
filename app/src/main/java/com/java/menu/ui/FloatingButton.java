package com.java.menu.ui;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.java.menu.util.Theme;

public final class FloatingButton extends FrameLayout
{
    public FloatingButton(Context c)
    {
        super(c);

        ImageView icon = new ImageView(c);
        int id = c.getResources().getIdentifier(Theme.LOGO_NAME, Theme.LOGO_TYPE, c.getPackageName());

        if (id != 0)
            icon.setImageResource(id);

        addView(icon, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}