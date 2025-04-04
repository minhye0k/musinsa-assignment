package com.musinsa.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public List<CaffeineCache> caffeineCaches() {
        return Arrays.stream(CacheType.values())
                .map(cacheType -> new CaffeineCache(
                        cacheType.getCacheName(),
                        Caffeine.newBuilder()
                                .recordStats()
                                .expireAfterWrite(cacheType.getExpireSecondAfterWrite(),
                                        TimeUnit.SECONDS)
                                .maximumSize(cacheType.getMaximumSize())
                                .build()
                )).toList();
    }

    @Bean
    public CacheManager cacheManager(List<CaffeineCache> caffeineCaches) {
//        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caffeineCaches);
        return cacheManager;
    }
}
