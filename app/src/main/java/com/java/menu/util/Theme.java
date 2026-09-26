package com.java.menu.util;

import android.content.Context;

public final class Theme
{
    public static final int BG_START = 0x22222222;
    public static final int BG_END = 0x22222222;
    public static final int STROKE = 0x14FFFFFF;
    public static final int ROW_IDLE = 0x0AFFFFFF;
    public static final int ROW_PRESSED = 0x1FFFFFFF;
    public static final int TEXT = 0xFFF0F0F5;
    public static final int MUTED = 0xFF8A8A9A;
    public static final int ACCENT = 0xFFFF0000;
    public static final int RED = 0xFFFF0000;
    public static final int TRACK_OFF = 0x44444444;
    public static final String LOGO_NAME = "icon";
    public static final String LOGO_TYPE = "mipmap";

    private Theme()
    {}

    public static int dp(Context c, float v)
    {
        return (int) (v * c.getResources().getDisplayMetrics().density + 0.5f);
    }
}
