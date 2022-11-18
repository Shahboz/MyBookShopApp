package com.example.MyBookShopApp.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;


class DateFormatterTest {

    private Date date;
    private String formatDate;
    private String anotherFormatDate;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();
        dateFormat = new SimpleDateFormat(DateFormatter.getPattern());
        formatDate = dateFormat.format(date);
        anotherFormatDate = date.toInstant()
                .atZone(ZoneOffset.systemDefault())
                .toLocalDateTime()
                .format(DateTimeFormatter.ISO_DATE_TIME);
    }

    @AfterEach
    void tearDown() {
        date = null;
        dateFormat = null;
        formatDate = null;
    }

    @Test
    void parseDateSuccessTest() {
        assertEquals(date, DateFormatter.parseDate(formatDate));
    }

    @Test
    void parseDateFailTest() {
        assertThrows(DateTimeParseException.class, () -> DateFormatter.parseDate(formatDate.replace("T", "")));
    }

    @Test
    void formatDateSuccessTest() {
        assertEquals(formatDate, DateFormatter.format(date));
    }

    @Test
    void formatDateFailTest() {
        assertNotEquals(anotherFormatDate, DateFormatter.format(date));
    }

}