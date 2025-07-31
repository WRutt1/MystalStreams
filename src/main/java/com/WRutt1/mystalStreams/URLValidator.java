package com.WRutt1.mystalStreams;

public class URLValidator {
    public static boolean isValidStreamUrl(String url) {
        return url.matches("^(https?://)?(www\\.)?(youtube\\.com|youtu\\.be|twitch\\.tv)/.+$");
    }
}