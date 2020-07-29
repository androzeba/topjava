package ru.javawebinar.topjava.util;

import org.springframework.format.Formatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class LocalDateFormatter implements Formatter<LocalDate> {

    @Override
    public LocalDate parse(String text, Locale locale) {
        return DateTimeUtil.parseLocalDate(text);
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return DateTimeFormatter.ISO_DATE.format(object);
    }
}
