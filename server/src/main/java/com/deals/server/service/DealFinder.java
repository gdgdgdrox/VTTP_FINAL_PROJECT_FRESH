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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.deals.server.model.Deal;
import com.deals.server.repository.RedisRepository;
import com.deals.server.repository.S3Repository;
import com.deals.server.repository.SqlRepository;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DealFinder {
    
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private S3Repository s3Repo;

    @Autowired
    private RedisRepository redisRepo;

    @Autowired
    private SqlRepository sqlRepo;

    @Autowired
    private EmailService emailService;

    @Value("${tih.api.key}")
    private String apiKey;

    private static final String API_BASE_URL = "https://api.stb.gov.sg/content/deals/v2/search";

    public List<Deal> getDealsByCategory(String category){
        //check if deals is present in Redis. if present, return
        log.info("Checking Redis for {} deals", category);
        Optional<List<Deal>> optDeals = redisRepo.getDeals(category);
        if (optDeals.isPresent()){
            log.info("Found in Redis");
            return optDeals.get();
        }
        log.info("Cannot find deals in Redis. Checking MySQL db");
        List<Deal> deals = sqlRepo.getDealsByCategory(category);
        if (!deals.isEmpty()){
            log.info("Found in MySQL");
            log.info("Saving to Redis");
            redisRepo.saveDeals(category, deals);
            return deals;
        }
        log.info("Cannot find deals in MySQL. Getting deals from API");
        List<Deal> d = getDealsFromAPI(category);
        log.info("Saving {} deals to SQL & Redis", d.size());
        sqlRepo.saveDealsAndDetails(d);        
        redisRepo.saveDeals(category, d);
        return d;
    }

    public List<Deal> getDealsByKeyword(String keyword){
        return sqlRepo.getDealsByKeyword(keyword);
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
            JsonArray images = jo.getJsonArray("images");
            String imageUUID = images.get(0).asJsonObject().getString("uuid");
            try {
                byte[] imageBinary = getImageFromAPI(imageUUID);
                String imageURL = s3Repo.storeImage(imageBinary, imageUUID);
                deal.setImageURL(imageURL);
                deals.add(deal);
            }
            catch (HttpServerErrorException httpServerErrorException){
                log.info("API threw exception for image ID {}", imageUUID);
                //check if there are other images that we can get
                if (images.size() > 1){
                    byte[] imageBinary = getImageFromAPI(images.get(1).asJsonObject().getString("uuid"));
                    //TO DO: What happen if this throws exception again?
                    String imageURL = s3Repo.storeImage(imageBinary, imageUUID);
                    deal.setImageURL(imageURL);
                    deals.add(deal);
                }
                else{
                    deal.setImageURL("C:/Users/gdfoo/Desktop/final_project/server/src/main/resources/static/no-image-available-icon-6.png");
                }

            }
        }
        return deals;
    }

    public byte[] getImageFromAPI(String imageUUID) throws HttpServerErrorException{
        HttpHeaders headers = new HttpHeaders();
        String fullUrl = UriComponentsBuilder.fromUriString("https://api.stb.gov.sg/media/download/v2/" + imageUUID)
                    // .queryParam("fileType", "Thumbnail 1080h")
                    .toUriString();
                    // .replace("%20", " ");
		headers.set("x-api-key", apiKey);
		RequestEntity<Void> reqEntity = RequestEntity.get(fullUrl)
													.headers(headers)
													.build();
        ResponseEntity<byte[]> respEntity = restTemplate.exchange(reqEntity, byte[].class);
        byte[] imageBinary = respEntity.getBody();
        return imageBinary;

    }


    // helper function to create RequestEntity
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

    //helper function to create RequestEntity with hard-coded query params for deal categories
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

    // check for new deals every 12 hours. if found new deals, save to DB and send email to subscribers
    @Scheduled(fixedRate = 12*60*60*1000)
    public void saveNewDeals(){
        List<Deal> newDeals = getNewDeals();
        if (newDeals.size() > 0){
            log.info("Saving new deals");
            sqlRepo.saveDealsAndDetails(newDeals);
            log.info("{} new deals saved", newDeals.size());
            List<String> subscriberEmails = emailService.getSubscribersEmail();
            if (subscriberEmails.size() > 0){
                emailService.sendEmail(subscriberEmails, newDeals);
            }

        }
        else{
            log.info("No new deals found.");
        }
    }

    public List<Deal> getNewDeals(){
        RequestEntity<Void> reqEntity = createRequestEntity();
        List<String> existingUUIDS = sqlRepo.getUUIDS();
        // TO DO - error handling
        log.info("Calling API to check for new deals");
        ResponseEntity<String> respEntity = restTemplate.exchange(reqEntity, String.class);
        JsonObject jsonPayload = Json.createReader(new StringReader(respEntity.getBody())).readObject();        
        List<Deal> newDeals = new LinkedList<>();

        //check whether a deal is new by comparing the deal's uuid against the existing list of uuids
        for (JsonValue jv : jsonPayload.getJsonArray("data")){
            JsonObject jo = jv.asJsonObject();
            String uuid = jo.getString("uuid");

            if (!existingUUIDS.contains(uuid)){
                log.info("New deal found. UUID: {}", uuid);
                Deal newDeal = Deal.createDeal(jo);
                String imageUUID = jo.getJsonArray("images").get(0).asJsonObject().getString("uuid");
                byte[] imageBinary = getImageFromAPI(imageUUID);
                String imageURL = s3Repo.storeImage(imageBinary, imageUUID);
                newDeal.setImageURL(imageURL);
                newDeals.add(newDeal);
            }
        }
        return newDeals;
    }



}
