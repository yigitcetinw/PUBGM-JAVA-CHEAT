package com.java.menu.core;

public final class Feature
{
    public interface Listener
    {
        void onChanged(Feature f, boolean checked);
    }

    private final String name;
    private boolean checked;
    private Listener listener;

    public Feature(String name, boolean checked)
    {
        this.name = name;
        this.checked = checked;
    }

    public String name()
    {
        return name;
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setListener(Listener l)
    {
        this.listener = l;
    }

    public void setChecked(boolean value)
    {
        if (this.checked == value)
            return;

        this.checked = value;

        if (listener != null)
            listener.onChanged(this, value);
    }
}