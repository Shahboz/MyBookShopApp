package com.example.MyBookShopApp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {

    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

    public static Date parseDate(String date) throws ParseException {
        return dateFormatter.parse(date);
    }

    public static String format(Date date) {
        return dateFormatter.format(date);
    }

}