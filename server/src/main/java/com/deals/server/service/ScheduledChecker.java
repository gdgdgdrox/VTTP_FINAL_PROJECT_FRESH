package com.deals.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.deals.server.model.Deal;
import com.deals.server.repository.SqlRepository;

@Service
public class ScheduledChecker {

    @Autowired
    private SqlRepository sqlRepo;

    @Autowired
    private DealFinder dealFinder;
     
 
}
