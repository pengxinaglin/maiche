package com.haoche51.sales.custom.calendar;

import android.support.annotation.NonNull;

/**
 * Supply labels for a given day. Default implementation is to format using a {@linkplain java.text.SimpleDateFormat}
 */
public interface DayFormatter {

    /**
     * Format a given day into a string
     *
     * @param day the day
     * @return a label for the day
     */
    @NonNull String format(@NonNull CalendarDay day);

    /**
     * Default implementation used by {@linkplain com.prolificinteractive.materialcalendarview.MaterialCalendarView}
     */
    DayFormatter DEFAULT = new DateFormatDayFormatter();
}
