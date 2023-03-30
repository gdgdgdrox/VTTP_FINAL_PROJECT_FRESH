package com.deals.server.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.jdbc.support.rowset.SqlRowSet;

import jakarta.json.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class Deal implements Serializable{
    private String uuid;
    private String name;
    private String description;

    //create index for category?
    private String category;
    private String imageURL;
    private String venue;
    private Double latitude;
    private Double longitude;
    private LocalDateTime validStartDate;
    private LocalDateTime validEndDate;
    private boolean isValid;
    private String tnc;
    private String websiteURL;
    private String tags;

    public static Deal createDeal(JsonObject jo){
        Deal deal = new Deal();
        deal.setUuid(jo.getString("uuid"));
        deal.setName(jo.getString("name"));
        deal.setDescription(jo.getString("body"));
        deal.setCategory(jo.getString("category"));
        deal.setValidStartDate(LocalDateTime.parse(jo.getString("validStartDate").split("\\+")[0]));
        deal.setValidEndDate(LocalDateTime.parse(jo.getString("validEndDate").split("\\+")[0]));
        deal.setValid(jo.getString("dealStatus").equals("VALID") ? true : false);
        deal.setTnc(jo.getString("termsConditions"));
        deal.setWebsiteURL(jo.getString("officialWebsite"));
        String tags = jo.getJsonArray("tags").toString().replaceAll("\\[|\\]", "");
        deal.setTags(tags);
        deal.setVenue(jo.getJsonObject("poiInfo").getString("name"));
        deal.setLatitude(jo.getJsonObject("poiInfo").getJsonObject("location").getJsonNumber("latitude").doubleValue());
        deal.setLongitude(jo.getJsonObject("poiInfo").getJsonObject("location").getJsonNumber("longitude").doubleValue());
        return deal;
    }

    public static Deal createDeal(SqlRowSet result){
        Deal deal = new Deal();
        deal.setUuid(result.getString("uuid"));
        deal.setName(result.getString("name"));
        deal.setDescription(result.getString("description"));
        deal.setImageURL(result.getString("image_url"));
        deal.setWebsiteURL(result.getString("website_url"));
        deal.setTnc(result.getString("terms_conditions"));
        deal.setTags(result.getString("tags"));
        deal.setValidStartDate(LocalDateTime.parse(result.getString("valid_start_date")));
        deal.setValidEndDate(LocalDateTime.parse(result.getString("valid_end_date")));
        deal.setVenue(result.getString("venue"));
        deal.setLatitude(result.getDouble("latitude"));
        deal.setLongitude(result.getDouble("longitude"));
        return deal;
    }
}