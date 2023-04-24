package com.deals.server.repository;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.deals.server.model.Deal;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;

@Repository
public class UserRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean userExist(String email){
        int count = jdbcTemplate.queryForObject(SqlQueries.USER_EXIST, Integer.class, email);
        boolean exist = count > 0 ? true : false;
        return exist;
    }

    public int registerNewUser(JsonObject userDetails){
        int added = jdbcTemplate.update(SqlQueries.CREATE_USER, userDetails.getString("email"), 
                                        userDetails.getString("password"), 
                                        userDetails.getString("firstName"), 
                                        userDetails.getString("lastName"), 
                                        LocalDate.parse(userDetails.getString("dob")),
                                        userDetails.getBoolean("receiveUpdate"));
        return added;
    }

    public void saveUserDeal(String email, JsonArray dealIDs) throws DataAccessException{
        List<Object[]> data = dealIDs.stream().map(dealID -> new Object[]{email, ((JsonString)(dealID)).getString()}).toList();
        jdbcTemplate.batchUpdate(SqlQueries.SAVE_USER_DEAL, data);
    }

    public List<Deal> getUserDeal(String email){
        List<Deal> deals = new LinkedList<>();
        SqlRowSet result = jdbcTemplate.queryForRowSet(SqlQueries.GET_USER_DEAL, email);
        while (result.next()){
            Deal deal = Deal.createDeal(result);
            deals.add(deal);
        }
        return deals;
    }

    public int verifyUserCreds(JsonObject creds){
        return jdbcTemplate.queryForObject(SqlQueries.VERIFY_USER_CREDS, Integer.class, creds.getString("email"), creds.getString("password"));
    }

    public int deleteUserDeal(String email, String dealID){
        int deleteCount = jdbcTemplate.update(SqlQueries.DELETE_USER_DEAL, email, dealID);
        return deleteCount;
    }

    public List<String> getSubscribersEmail(){
        return jdbcTemplate.queryForList(SqlQueries.GET_SUBSCRIBERS_EMAIL, String.class);
    }








}
