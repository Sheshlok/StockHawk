package com.sam_chordas.android.stockhawk.data.networkingAPI.jsonpGsonCustomConverter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sheshloksamal on 13/07/16.
 * A class to convert JSONP response to JSON using regex
 */
public class JsonpParser {

    private static final Pattern JSONP = Pattern.compile("(?s)\\w+\\((.*)\\).*");

    public static String jsonpToJson(String jsonStr) {
        Matcher matcher = JSONP.matcher(jsonStr);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new IllegalArgumentException("Unknown jsonp format");
        }
    }
}
