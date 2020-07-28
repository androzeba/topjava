package ru.javawebinar.topjava.util;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Formatter;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public class MyDateTimeFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<MyDateTimeFormatter> {

    public MyDateTimeFormatAnnotationFormatterFactory() {
    }

    @Override
    public Set<Class<?>> getFieldTypes() {
        return Set.of(LocalTime.class, LocalDate.class);
    }

    @Override
    public Printer<?> getPrinter(MyDateTimeFormatter annotation, Class<?> fieldType) {
        return getDateTimeFormatter(fieldType);
    }

    @Override
    public Parser<?> getParser(MyDateTimeFormatter annotation, Class<?> fieldType) {
        return getDateTimeFormatter(fieldType);
    }

    private Formatter<?> getDateTimeFormatter(Class<?> fieldType) {
        if (fieldType == LocalDate.class) {
            return new LocalDateFormatter();
        }
        if (fieldType == LocalTime.class) {
            return new LocalTimeFormatter();
        }
        return null;
    }
}
