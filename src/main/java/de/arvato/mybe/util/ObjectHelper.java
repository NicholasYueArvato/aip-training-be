package de.arvato.mybe.util;

import java.util.ArrayList;
import java.util.List;

public class ObjectHelper
{
    public static List<Long> findDifferent(List<Long> source, List<Long> target)
    {
        ArrayList<Long> sources = new ArrayList<>(source);
        sources.removeAll(target);
        return sources;
    }

    public static List<String> getDifferent(List<String> source, List<String> target)
    {
        ArrayList<String> sources = new ArrayList<>(source);
        sources.removeAll(target);
        return sources;
    }
}
