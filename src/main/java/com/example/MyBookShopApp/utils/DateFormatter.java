package com.example.MyBookShopApp.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateFormatter {

    private static final String PATTERN = "yyyy-MM-dd'T'HH:mm";

    public static String getPattern() {
        return PATTERN;
    }

    public static Date parseDate(String date) {
        return Date.from(LocalDateTime.parse(date, DateTimeFormatter.ofPattern(PATTERN)).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String format(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(PATTERN));
    }

}