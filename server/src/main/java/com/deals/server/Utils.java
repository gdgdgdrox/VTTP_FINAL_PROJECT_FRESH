package com.deals.server;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.json.JsonArray;

public class Utils {
    public static Timestamp createTimestamp(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        OffsetDateTime dateTime = OffsetDateTime.parse(date, formatter);
        Timestamp timestamp = Timestamp.from(dateTime.toInstant());
        return timestamp;
    }

    public static ZonedDateTime parseToZDT(String date){
        return ZonedDateTime.parse(date);
    }

    public static String formattedTimeStamp(ZonedDateTime zdt){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return zdt.format(dtf);
    }

    public static String getCurrentDateTime(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        return formattedDateTime;
    }


    
}
