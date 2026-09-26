package com.java.menu.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FeatureManager
{
    private final List<Feature> features = new ArrayList<>();

    public Feature add(String name, boolean def)
    {
        Feature f = new Feature(name, def);
        features.add(f);
        return f;
    }

    public List<Feature> all()
    {
        return Collections.unmodifiableList(features);
    }

    public Feature byName(String name)
    {
        for (Feature f : features)
        {
            if (f.name().equals(name))
                return f;
        }
        return null;
    }
}