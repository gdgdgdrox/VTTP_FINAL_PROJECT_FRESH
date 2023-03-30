package com.deals.server.service;

import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.deals.server.Utils;
import com.deals.server.model.Deal;
import com.deals.server.repository.RedisRepository;
import com.deals.server.repository.S3Repository;
import com.deals.server.repository.SqlRepository;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

@Service
public class DealFinder {
    
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private S3Repository s3Repo;

    @Autowired
    private RedisRepository redisRepo;

    @Autowired
    private SqlRepository sqlRepo;

    @Value("${tih.api.key}")
    private String apiKey;

    private static final String API_BASE_URL = "https://api.stb.gov.sg/content/deals/v2/search";

    public List<Deal> getDealsByCategory(String category){
        //check if deals is present in Redis
        System.out.println("Checking Redis for %s deals".formatted(category));
        Optional<List<Deal>> optDeals = redisRepo.getDeals(category);
        if (optDeals.isPresent()){
            System.out.println("found in Redis");
            return optDeals.get();
        }
        System.out.println("Cannot find deal in Redis");
        System.out.println("Checking MySQL for %s deals".formatted(category));
        List<Deal> deals = sqlRepo.getDealsByCategory(category);
        if (!deals.isEmpty()){
            System.out.println("found in MySQL");
            System.out.println("saving to Redis");
            redisRepo.saveDeals(category, deals);
            return deals;
        }
        System.out.println("Cannot find deal in sql");

        System.out.println("Getting %s deals from API".formatted(category));
        List<Deal> d = getDealsFromAPI(category);
        System.out.println("Got back %d deals".formatted(d.size()));
        System.out.println("Saving to SQL & Redis");
        sqlRepo.saveDealsAndDetails(d);        
        redisRepo.saveDeals(category, d);

        return d;
    }

    public List<Deal> getDealsByKeyword(String keyword){
        //1. search mysql db
        return sqlRepo.getDealsByKeyword(keyword);
        //2. if not in mysql db, search API
    }
    
    public List<Deal> getDealsFromAPI(String category){
        RequestEntity<Void> reqEntity = createRequestEntity(category);
        // TO DO - error handling
        ResponseEntity<String> respEntity = restTemplate.exchange(reqEntity, String.class);
        List<Deal> deals = createDeals(respEntity.getBody());
        return deals;
    }

    public List<Deal> createDeals (String payload){
        List<Deal> deals = new LinkedList<>();
        JsonObject jsonPayload = Json.createReader(new StringReader(payload)).readObject();
        for (JsonValue jv : jsonPayload.getJsonArray("data")){
            JsonObject jo = jv.asJsonObject();
            Deal deal = Deal.createDeal(jo);
            // String imageUUID = jo.getJsonObject("poiInfo").getJsonArray("images").get(1).asJsonObject().getString("uuid");
            String imageUUID = jo.getJsonArray("images").get(0).asJsonObject().getString("uuid");
            byte[] imageBinary = getImageFromAPI(imageUUID);
            String imageURL = s3Repo.storeImage(imageBinary, imageUUID);
            deal.setImageURL(imageURL);
            deals.add(deal);
        }
        return deals;
    }

    public byte[] getImageFromAPI(String imageUUID){
        HttpHeaders headers = new HttpHeaders();
        String fullUrl = UriComponentsBuilder.fromUriString("https://api.stb.gov.sg/media/download/v2/" + imageUUID)
                    // .queryParam("fileType", "Thumbnail 1080h")
                    .toUriString();
                    // .replace("%20", " ");
		headers.set("x-api-key", "apiKey");
		RequestEntity<Void> reqEntity = RequestEntity.get(fullUrl)
													.headers(headers)
													.build();
        ResponseEntity<byte[]> respEntity = restTemplate.exchange(reqEntity, byte[].class);

        byte[] imageBinary = respEntity.getBody();

        return imageBinary;
    }





// helper functions to create RequestEntity
    public RequestEntity<Void> createRequestEntity(String category){
        String fullUrl = UriComponentsBuilder.fromUriString(API_BASE_URL)
        .queryParam("searchType", "keyword")
        .queryParam("category", category)
        .toUriString();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accept", "application/json");
        httpHeaders.add("x-api-key", apiKey);

        RequestEntity<Void> reqEntity = RequestEntity.get(fullUrl).headers(httpHeaders).build();
        return reqEntity;
    }

    //hard-coded query params
    public RequestEntity<Void> createRequestEntity(){
        String fullUrl = UriComponentsBuilder
                            .fromUriString(API_BASE_URL)
                            .queryParam("searchType", "keyword")
                            .queryParam("category", "attractions,bars_clubs,food_beverages,tours")
                            .toUriString();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("accept", "application/json");
        httpHeaders.add("x-api-key", apiKey);
        RequestEntity<Void> reqEntity = RequestEntity.get(fullUrl).headers(httpHeaders).build();
        return reqEntity;
    }

    // check for new deals every 12 hours and save to DB
    @Scheduled(fixedRate = 12*60*60*1000)
    public void saveNewDeals(){
        List<Deal> newDeals = getNewDeals();
        if (newDeals.size() > 0){
            System.out.println(Utils.getCurrentDateTime() + ": Saving new deals");
            sqlRepo.saveDealsAndDetails(newDeals);
        }
        else{
            System.out.println("No new deals found.");
        }
    }

    public List<Deal> getNewDeals(){
        RequestEntity<Void> reqEntity = createRequestEntity();
        List<String> existingUUIDS = sqlRepo.getUUIDS();
        // TO DO - error handling
        System.out.println(Utils.getCurrentDateTime() + ": Calling API to check for new deals");
        ResponseEntity<String> respEntity = restTemplate.exchange(reqEntity, String.class);
        JsonObject jsonPayload = Json.createReader(new StringReader(respEntity.getBody())).readObject();        
        List<Deal> newDeals = new LinkedList<>();

        //check whether a deal is new by comparing the deal's uuid against the existing list of uuids
        for (JsonValue jv : jsonPayload.getJsonArray("data")){
            JsonObject jo = jv.asJsonObject();
            String uuid = jo.getString("uuid");
            if (!existingUUIDS.contains(uuid)){
                System.out.println("New deal found: %s".formatted(uuid));
                Deal newDeal = Deal.createDeal(jo);
                String imageUUID = jo.getJsonArray("images").get(0).asJsonObject().getString("uuid");
                byte[] imageBinary = getImageFromAPI(imageUUID);
                String imageURL = s3Repo.storeImage(imageBinary, imageUUID);
                newDeal.setImageURL(imageURL);
                newDeals.add(newDeal);
                System.out.println(newDeal);
            }
        }
        return newDeals;
    }



}
