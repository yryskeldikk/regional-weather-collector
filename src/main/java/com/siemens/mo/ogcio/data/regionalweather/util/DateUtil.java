package com.siemens.mo.ogcio.data.regionalweather.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtil {
    public static final String DATE_TIME_FORMAT = "yyyyMMddHHmm";
    public static final String HONG_KONG_TIMEZONE = "Asia/Hong_Kong";

    public static ZoneId getHongKongTimeZone(){
        return ZoneId.of( HONG_KONG_TIMEZONE );
    }
    public static ZoneId getUTCTimeZone(){
        return ZoneId.of( "UTC" );
    }

    public static ZonedDateTime toHongKongZonedDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime localDateTime = LocalDateTime.parse(date, formatter);
        return ZonedDateTime.of(localDateTime, getHongKongTimeZone());
    }

    public static Date toUTC(String date) {
        ZonedDateTime dateHongKong = toHongKongZonedDateTime(date);
        ZonedDateTime utcDate = dateHongKong.withZoneSameInstant(getUTCTimeZone());
        return Date.from(utcDate.toInstant());
    }
}
