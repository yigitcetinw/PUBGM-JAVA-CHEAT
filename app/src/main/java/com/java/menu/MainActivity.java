package com.java.menu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.java.menu.service.ModMenuService;
import com.java.menu.util.Theme;

public final class MainActivity extends Activity
{
    private static final int REQ_OVERLAY = 1001;
    private Button start;

    @Override
    protected void onCreate(Bundle b)
    {
        super.onCreate(b);
        setContentView(buildUi());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateButton();
    }

    private void updateButton()
    {
        if (start == null) return;
        start.setText(ModMenuService.running ? "Stop" : "Start");
    }

    private View buildUi()
    {
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(LinearLayout.VERTICAL);
        col.setGravity(Gravity.CENTER);
        col.setBackgroundColor(0xFF0F0F18);

        TextView title = new TextView(this);
        title.setText("Furkan Cheat");
        title.setTextColor(Theme.TEXT);
        title.setTextSize(22);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setLetterSpacing(0.1f);
        title.setGravity(Gravity.CENTER);
        title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        start = new Button(this);
        start.setText("Start");
        start.setTextColor(Color.WHITE);
        start.setAllCaps(false);

        GradientDrawable g = new GradientDrawable();
        g.setColor(Theme.ACCENT);
        g.setCornerRadius(Theme.dp(this, 12));

        start.setBackground(g);

        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        blp.topMargin = Theme.dp(this, 24);

        start.setLayoutParams(blp);

        start.setOnClickListener
        (
            new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    onStartStopClicked();
                }
            }
        );

        col.addView(title);
        col.addView(start);
        return col;
    }

    private void onStartStopClicked()
    {
        if (ModMenuService.running)
        {
            stopService(new Intent(this, ModMenuService.class));
            start.postDelayed
            (
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        updateButton();
                    }
                }, 150
            );
        }
        else
        {
            ensureOverlayAndStart();
        }
    }

    private void ensureOverlayAndStart()
    {
        if (Settings.canDrawOverlays(this))
        {
            startMenu();
        }
        else
        {
            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:" + getPackageName()));
            startActivityForResult(i, REQ_OVERLAY);
        }
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data)
    {
        super.onActivityResult(req, res, data);
        if (req == REQ_OVERLAY && Settings.canDrawOverlays(this))
            startMenu();
    }

    private void startMenu()
    {
        Intent i = new Intent(this, ModMenuService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            startForegroundService(i);
        }
        else
        {
            startService(i);
        }
        start.postDelayed
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    updateButton();
                }
            }, 250
        );
    }
}
