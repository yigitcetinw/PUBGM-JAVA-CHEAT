package com.java.menu.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

import com.java.menu.core.FeatureManager;
import com.java.menu.ui.DragController;
import com.java.menu.ui.FloatingButton;
import com.java.menu.ui.ModMenuView;
import com.java.menu.util.Theme;

public final class ModMenuService extends Service
{
    public static volatile boolean running = false;

    private static final String CHANNEL_ID = "mod_menu";
    private static final int NOTIF_ID = 42;

    private WindowManager wm;
    private int screenW;
    private int screenH;

    private FloatingButton fab;
    private WindowManager.LayoutParams fabParams;

    private ModMenuView menu;
    private WindowManager.LayoutParams menuParams;
    private boolean menuVisible = false;

    private FeatureManager features;

    @Override
    public void onCreate()
    {
        super.onCreate();
        running = true;
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        updateScreenSize();

        features = new FeatureManager();
        features.add("Line", false);
        features.add("Bone", false);
        features.add("Health", false);
        features.add("Name", false);
        features.add("Distance", false);
        features.add("Vehicle", false);
        features.add("Deadbox", false);
        features.add("Count", false);

        startForegroundNotice();
        buildFab();
        buildMenu();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        updateScreenSize();
        reclampAll();
    }

    private void updateScreenSize()
    {
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getRealMetrics(dm);
        screenW = dm.widthPixels;
        screenH = dm.heightPixels;
    }

    private void reclampAll()
    {
        if (fab != null)
        {
            fab.post
            (
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (fab == null || fabParams == null) return;

                        int fw = fab.getWidth();
                        int fh = fab.getHeight();
                        fabParams.x = clamp(fabParams.x, 0, Math.max(0, screenW - fw));
                        fabParams.y = clamp(fabParams.y, 0, Math.max(0, screenH - fh));

                        try
                        {
                            wm.updateViewLayout(fab, fabParams);
                        }
                        catch (Exception ignored)
                        {}
                    }
                }
            );
        }

        if (menu != null)
        {
            menu.post
            (
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (menu == null || menuParams == null) return;

                        int mw = menu.getWidth();
                        int mh = menu.getHeight();
                        menuParams.x = clamp(menuParams.x, 0, Math.max(0, screenW - mw));
                        menuParams.y = clamp(menuParams.y, 0, Math.max(0, screenH - mh));

                        try
                        {
                            wm.updateViewLayout(menu, menuParams);
                        } 
                        catch (Exception ignored) 
                        {}
                    }
                }
            );
        }
    }

    private void buildFab()
    {
        int size = Theme.dp(this, 54);
        fabParams = new WindowManager.LayoutParams(size, size, overlayType(), WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        fabParams.gravity = Gravity.TOP | Gravity.START;
        fabParams.x = Theme.dp(this, 16);
        fabParams.y = screenH / 3;

        fab = new FloatingButton(this);
        fab.setOnTouchListener
        (
            new DragController
            (
                wm, fab, fabParams,
                Theme.dp(this, 6), new DragController.ClickHandler()
                {
                    @Override
                    public void onClick()
                    {
                        toggleMenu();
                    }
                }
            )
        );

        wm.addView(fab, fabParams);
    }

    private void buildMenu()
    {
        menuParams = new WindowManager.LayoutParams(Theme.dp(this, 320), WindowManager.LayoutParams.WRAP_CONTENT, overlayType(), WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        menuParams.gravity = Gravity.TOP | Gravity.START;

        menu = new ModMenuView(this, features);
        menu.setOnCloseListener
        (
            new ModMenuView.OnCloseListener()
            {
                @Override
                public void onClose()
                {
                    hideMenu();
                }
            }
        );
        menu.getHeader().setOnTouchListener(new DragController(wm, menu, menuParams, Theme.dp(this, 6), null));
        menu.setVisibility(View.GONE);
        wm.addView(menu, menuParams);
    }

    private void toggleMenu()
    {
        if (menuVisible)
            hideMenu();
        else
            showMenu();
    }

    private void showMenu()
    {
        menuVisible = true;

        int menuW = Theme.dp(this, 320);
        int margin = Theme.dp(this, 8);
        menuParams.x = clamp(fabParams.x + Theme.dp(this, 66), 0, Math.max(0, screenW - menuW - margin));
        menuParams.y = clamp(fabParams.y, margin, Math.max(margin, screenH - Theme.dp(this, 420)));
        wm.updateViewLayout(menu, menuParams);

        menu.setVisibility(View.VISIBLE);
        menu.setAlpha(0f);
        menu.setScaleX(0.92f);
        menu.setScaleY(0.92f);
        menu.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(180).setInterpolator(new DecelerateInterpolator()).start();
    }

    private void hideMenu()
    {
        menuVisible = false;

        menu.animate().alpha(0f).scaleX(0.92f).scaleY(0.92f).setDuration(140).withEndAction
        (
            new Runnable()
            {
                @Override
                public void run()
                {
                    menu.setVisibility(View.GONE);
                }
            }
        ).start();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        running = false;

        try
        {
            if (fab != null)
                wm.removeViewImmediate(fab);
            if (menu != null)
                wm.removeViewImmediate(menu);
        }
        catch (Exception ignored)
        {}

        fab = null;
        menu = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent i)
    {
        return null;
    }

    private int overlayType()
    {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE;
    }

    private void startForegroundNotice()
    {
        NotificationManager nm = getSystemService(NotificationManager.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel ch = new NotificationChannel(CHANNEL_ID, "Mod Menu", NotificationManager.IMPORTANCE_MIN);
            nm.createNotificationChannel(ch);
        }

        Notification.Builder b;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            b = new Notification.Builder(this, CHANNEL_ID);
        }
        else
        {
            b = new Notification.Builder(this);
        }

        Notification n = b.setSmallIcon(android.R.drawable.ic_menu_manage).setContentTitle("Amınoğlu Cheat").setContentText("Overlay Active").setOngoing(true).setPriority(Notification.PRIORITY_MIN).build();

        startForeground(NOTIF_ID, n);
    }

    private static int clamp(int v, int min, int max)
    {
        if (v < min)
            return min;
        if (v > max)
            return max;

        return v;
    }
}
