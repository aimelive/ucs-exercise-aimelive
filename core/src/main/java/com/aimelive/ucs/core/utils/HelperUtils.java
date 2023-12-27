package com.aimelive.ucs.core.utils;

public class HelperUtils {
    public static String extractStringTag(String tag) {
        if (tag.contains("/")) {
            return tag.substring(tag.lastIndexOf("/") + 1);
        }
        return tag.substring(tag.lastIndexOf(":") + 1);
    }
}
