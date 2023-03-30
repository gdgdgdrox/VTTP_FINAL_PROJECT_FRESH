package com.deals.server.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.deals.server.Utils;
import com.deals.server.model.Deal;

@Repository
public class SqlRepository{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveDealsAndDetails(List<Deal> deals){
        saveDeals(deals);
        saveDealDetails(deals);
    }

    public void saveDeals(List<Deal> deals){
        List<Object[]> dealsForInsertion = deals.stream().map(deal -> new Object[]{
            deal.getUuid(), deal.isValid(), deal.getCategory()})
            .toList();
        jdbcTemplate.batchUpdate(SqlQueries.SAVE_DEALS, dealsForInsertion);
    }

    public void saveDealDetails(List<Deal> deals){
        List<Object[]> dealDetailsForInsertion = deals.stream()
                                                        .map(deal -> new Object[]{
                                                            deal.getUuid(), deal.getName(), deal.getDescription(),
                                                            deal.getImageURL(), deal.getWebsiteURL(), deal.getTnc(),
                                                            deal.getTags(), Timestamp.valueOf(deal.getValidStartDate()), 
                                                            Timestamp.valueOf(deal.getValidEndDate()),
                                                            deal.getVenue(), deal.getLatitude(), deal.getLongitude()
                                                        })
                                                        .toList();
        jdbcTemplate.batchUpdate(SqlQueries.SAVE_DEAL_DETAILS, dealDetailsForInsertion);
    }

    public List<String> getUUIDS(){
        List<String> uuids = new LinkedList<>();
        SqlRowSet result = jdbcTemplate.queryForRowSet(SqlQueries.GET_UUIDS);
        while (result.next()){
            uuids.add(result.getString("uuid"));
        }
        return uuids;
    }

    public List<Deal> getDealsByCategory(String category){
        List<Deal> deals = new LinkedList<>();
        SqlRowSet result = jdbcTemplate.queryForRowSet(SqlQueries.GET_DEALS_BY_CATEGORY, category);
        while (result.next()){
            Deal deal = Deal.createDeal(result);
            deals.add(deal);
        }
        return deals;
    
    }

    public List<Deal> getDealsByKeyword(String keyword){
        List<Deal> deals = new LinkedList<>();
        SqlRowSet result = jdbcTemplate.queryForRowSet(SqlQueries.GET_DEALS_BY_KEYWORD, keyword, keyword, keyword);
        while (result.next()){
            Deal deal = Deal.createDeal(result);
            deals.add(deal);
        }
        return deals;
    
    }

    //update the valid status of deals that are expiring to false to prevent expiring deals from being displayed
    @Scheduled(fixedRate = 4*60*60*1000)
    public void updateValidStatus(){
        System.out.println(Utils.getCurrentDateTime() + ": Checking and updating validity status..");
        int numRowsUpdated = jdbcTemplate.update(SqlQueries.UPDATE_VALID_STATUS);
        System.out.println();

    }




}