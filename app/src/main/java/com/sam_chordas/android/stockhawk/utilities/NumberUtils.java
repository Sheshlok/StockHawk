package com.sam_chordas.android.stockhawk.utilities;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by sheshloksamal on 23/07/16.
 */
public class NumberUtils {
    /** Format the Volume & Average Daily Volume numbers
     *  http://stackoverflow.com/questions/4753251/how-to-go-about-formatting-1200-to-1-2k-in-java*/

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");

    }

    public static String format(long value) {
        // Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here

        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value);

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value/(divideBy/10); // the number part of the output times 10
        boolean hasDecimal = truncated < 1000 && (truncated / 100d) != (truncated /100);
        return hasDecimal ? (truncated/100d) + suffix : (truncated/100) + suffix;
    }
}
