package com.deals.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.deals.server.model.Deal;
import com.deals.server.repository.UserRepository;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;

    public int registerNewUser(JsonObject userDetails){
        return userRepo.registerNewUser(userDetails);
    }

    public boolean userExists(String email){
        return userRepo.userExist(email);
    }

    public boolean saveUserDeal(JsonObject emailAndDealIDs){
        try{
            String email = emailAndDealIDs.getString("email");
            JsonArray dealIDs = emailAndDealIDs.getJsonArray("dealIDs");
            userRepo.saveUserDeal(email, dealIDs);
            return true;
        }
        catch (DuplicateKeyException dke){
            //user is trying to save the same deal again. do nothing
            return true;
        }
        catch (DataAccessException ex){
            ex.printStackTrace();
            return false;
        }
    }

    public List<Deal> getUserDeal(String email){
        return userRepo.getUserDeal(email);
    }

    public boolean verifyUserCreds(JsonObject creds){
        boolean isVerified = userRepo.verifyUserCreds(creds) == 1 ? true : false;
        return isVerified;
    }

    public int deleteUserDeal(JsonObject emailAndDealID){
        String email = emailAndDealID.getString("email");
        String dealID = emailAndDealID.getString("dealID");
        int deleteCount = userRepo.deleteUserDeal(email, dealID);
        return deleteCount;
    }

}
