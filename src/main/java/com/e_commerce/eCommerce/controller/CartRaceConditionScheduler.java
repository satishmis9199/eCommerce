//package com.e_commerce.eCommerce.controller;
//
//import com.e_commerce.eCommerce.service.CartsService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class CartRaceConditionScheduler {
//
//    private final CartsService testService;
//
//    @Scheduled(cron = "*/10 * * * * *", zone = "Asia/Kolkata")
//    public void runRaceConditionTest() throws InterruptedException {
//        log.error("inside scheduler");
//
//        System.out.println("======================================");
//        System.out.println("CART RACE CONDITION TEST STARTED");
//        System.out.println("======================================");
//
//        testService.raceTest(
//                6L,
//                100
//        );
//
//        System.out.println("======================================");
//        System.out.println("CART RACE CONDITION TEST FINISHED");
//        System.out.println("======================================");
//    }
//}