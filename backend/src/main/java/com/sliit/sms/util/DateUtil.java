package com.sliit.sms.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class DateUtil {

    private DateUtil() {
    }

    /** Counts working days (Mon-Fri) between two dates, inclusive. */
    public static long countWorkingDays(LocalDate start, LocalDate end) {
        long days = 0;
        LocalDate d = start;
        while (!d.isAfter(end)) {
            if (d.getDayOfWeek() != DayOfWeek.SATURDAY && d.getDayOfWeek() != DayOfWeek.SUNDAY) {
                days++;
            }
            d = d.plusDays(1);
        }
        return days;
    }

    public static long inclusiveDaysBetween(LocalDate start, LocalDate end) {
        return ChronoUnit.DAYS.between(start, end) + 1;
    }
}
