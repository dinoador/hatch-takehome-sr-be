package com.example.takehome.util;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {

    public static String sanitizeList(final List<String> list) {
        return
            list.stream()
                .map(ele -> "\"" + ele + "\"")
                .collect(Collectors.toList())
                .toString();
    }

    public static String sanitizeString(final String str) {
        return "\"" + str + "\"";
    }

}
