package com.deals.server;

import java.io.StringReader;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.json.Json;
import jakarta.json.JsonObject;

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

    public static String createResponse(String key, String value){
        return Json.createObjectBuilder().add(key, value).build().toString();
    }
    public static JsonObject payloadToJson(String payload){
        return Json.createReader(new StringReader(payload)).readObject();
    }


    
}
