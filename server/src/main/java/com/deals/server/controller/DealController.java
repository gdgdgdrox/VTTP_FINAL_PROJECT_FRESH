package com.deals.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deals.server.model.Deal;
import com.deals.server.service.DealFinder;

@RestController
@RequestMapping(path="api")
@CrossOrigin(origins = "*")
public class DealController {
    
    @Autowired
    private DealFinder dealFinder;

    @GetMapping(path="/deals/{category}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Deal>> getDealsByCategory(@PathVariable String category){
        System.out.println("IN GET DEAL BY CATEGORY CONTROLLER");
        List<Deal> deals = dealFinder.getDealsByCategory(category);
        return ResponseEntity.ok(deals);
    }

    @GetMapping(path="/deals", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Deal>> getDealsByKeyword(@RequestParam String keyword){
        System.out.println("IN GET DEAL BY KEYWORD CONTROLLER");
        System.out.println("KEYWORD : %s".formatted(keyword));
        List<Deal> deals = dealFinder.getDealsByKeyword(keyword);
        return ResponseEntity.ok(deals);
    }
}
