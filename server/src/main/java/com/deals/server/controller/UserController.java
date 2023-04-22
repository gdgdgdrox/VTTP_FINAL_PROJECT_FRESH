package com.deals.server.controller;

import java.io.StringReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deals.server.Utils;
import com.deals.server.model.Deal;
import com.deals.server.service.UserService;

import jakarta.json.Json;
import jakarta.json.JsonObject;

@RestController
@RequestMapping(path="api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;
    
    @PostMapping(path="/register")
    public ResponseEntity<String> registerNewUser(@RequestBody String payload){
        System.out.println("IN REGISTER NEW USER CONTROLLER. PAYLOAD > %s".formatted(payload));
        JsonObject userDetails = Utils.payloadToJson(payload);
        String email = userDetails.getString("email");
        String response = "";
        if (userService.userExists(email)){
            response = Utils.createResponse("message", "Email already exists.");
            return ResponseEntity.status(409).body(response);
        }
        int added = userService.registerNewUser(userDetails);
        if (added == 1){
            response = Utils.createResponse("email", email);
            return ResponseEntity.status(201).body(response);
        }
        else {
            response = Utils.createResponse("message", "Unknown error. Please try again later.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping(path="/login")
    public ResponseEntity<String> checkLoginCredentials(@RequestBody String payload){
        System.out.println("IN CHECK LOGIN CONTROLLER. CREDS > %s".formatted(payload));
        JsonObject creds = Utils.payloadToJson(payload);
        boolean isVerified = userService.verifyUserCreds(creds);
        String response = "";
        if (isVerified){
            String email = creds.getString("email");
            response = Utils.createResponse("email", email);
            return ResponseEntity.ok(response);
        }
        else{
            response = Utils.createResponse("message", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }
    }

    
    @PostMapping(path="/deal/save")
    public ResponseEntity<String> saveUserDeal(@RequestBody String payload){
        JsonObject emailAndDealIDs = Utils.payloadToJson(payload);
        boolean saveSuccess = userService.saveUserDeal(emailAndDealIDs);
        String response = "";
        if (saveSuccess){
            response = Utils.createResponse("message", "deals were successfully saved");
            return ResponseEntity.ok().body(response);
        }
        else{
            response = Utils.createResponse("message", "error saving deals");
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping(path="/deal/get")
    public ResponseEntity<List<Deal>> getUserDeals(@RequestBody String payload){
        System.out.println("IN GET USER DEAL CONTROLLER");
        System.out.println("USER > " + payload);
        String email = Utils.payloadToJson(payload).getString("email");
        List<Deal> userDeals = userService.getUserDeal(email);
        return ResponseEntity.ok(userDeals);
    }

    @DeleteMapping(path="/deal/delete")
    public ResponseEntity<String> deleteUserDeal(@RequestBody String payload){
        System.out.println("IN DELETE UESR DEAL CONTROLLER");
        System.out.println("USER > " + payload);
        JsonObject emailAndDealID = Utils.payloadToJson(payload);
        int deleteCount = userService.deleteUserDeal(emailAndDealID);
        String response = "";
        if (deleteCount == 1){
            response = Utils.createResponse("message", "successfully deleted");
            return ResponseEntity.ok(response);
        }
        else{
            response = Utils.createResponse("message", "Unknown error. Please try again later.");
            return ResponseEntity.status(500).body(response);
        }
    }


}
