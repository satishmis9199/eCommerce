package com.e_commerce.eCommerce.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Arrays;

@Configuration
@EnableCaching
public class CaffeineConfig {

    @Bean
    public CacheManager cacheManager() {

        CaffeineCache bannersCache = new CaffeineCache(
                "banners",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(1))
                        .maximumSize(1000)
                        .recordStats()
                        .build()
        );

        CaffeineCache allBannerCache = new CaffeineCache(
                "allBanner",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .maximumSize(1000)
                        .recordStats()
                        .build()
        );
        CaffeineCache jwtSecret = new CaffeineCache(
                "jwtSecret",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(20))
                        .maximumSize(1000)
                        .recordStats()
                        .build()
        );


        CaffeineCache productsCache = new CaffeineCache(
                "products",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .maximumSize(1000)
                        .recordStats()
                        .build()
        );

        CaffeineCache adminProductCache = new CaffeineCache(
                "adminProduct",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .maximumSize(1000)
                        .recordStats()
                        .build()
        );

        CaffeineCache productsByIdCache = new CaffeineCache(
                "productsById",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .maximumSize(5000)
                        .recordStats()
                        .build()
        );


        CaffeineCache productsByIdsCache = new CaffeineCache(
                "productsByIds",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(5))
                        .maximumSize(5000)
                        .recordStats()
                        .build()
        );
        CaffeineCache tenantsCache = new CaffeineCache(
                "tenants",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .maximumSize(1000)
                        .recordStats()
                        .build()
        );
        CaffeineCache allVendors = new CaffeineCache(
                "AllVendors",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .maximumSize(1000)
                        .recordStats()
                        .build()
        );
        CaffeineCache vendorDetail = new CaffeineCache(
                "vendorDetail",
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(30))
                        .maximumSize(1000)
                        .recordStats()
                        .build()
        );


        SimpleCacheManager cacheManager = new SimpleCacheManager();

        cacheManager.setCaches(Arrays.asList(
                tenantsCache,
                bannersCache,
                allBannerCache,
                productsCache,
                adminProductCache,
                productsByIdCache,
                productsByIdsCache,
                jwtSecret,allVendors
                ,vendorDetail
        ));

        return cacheManager;
    }
}