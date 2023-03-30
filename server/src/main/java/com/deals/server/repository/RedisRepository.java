package com.deals.server.repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.deals.server.model.Deal;

@Repository
public class RedisRepository {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void saveDeals(String category, List<Deal> deals){
        //maybe TTL of 24h? since deal expire 24h
        redisTemplate.opsForValue().set(category, deals, Duration.ofMinutes(30));
    }

    public Optional<List<Deal>> getDeals(String category){
        System.out.println("redis key: %s".formatted(category));
        List<Deal> deals = (List<Deal>)redisTemplate.opsForValue().get(category);
        return Optional.ofNullable(deals);
    }
}
