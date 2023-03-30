package com.deals.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.deals.server.service.DealFinder;

@SpringBootApplication
@EnableScheduling
public class ServerApplication implements CommandLineRunner{
	@Autowired
	private DealFinder dealFinder;


	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		// dealFinder.getNewDeals();
		// dealFinder.saveNewDeals(null);
	}

}
