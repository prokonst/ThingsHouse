package com.prokonst.thingshouse.tools;

import android.icu.util.Calendar;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeConverter {
    private final static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @TypeConverter
    public String fromDateChange(Date dateChange) {
        if(dateChange == null)
            dateChange = new Date();

        return formatter.format(dateChange);
    }

    @TypeConverter
    public Date toDateChange(String dateStr) {
        Date parsedDate;

        try {
            parsedDate = formatter.parse(dateStr);
        } catch (Exception ex) {
            parsedDate = null;
        }

        return parsedDate;
    }
}
