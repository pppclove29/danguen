package com.example.danguen.config.redis;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@EnableCaching
@Configuration
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(){
        ConcurrentMapCacheManager manager = new ConcurrentMapCacheManager();
        manager.setAllowNullValues(false);
        manager.setCacheNames(List.of("post"));

        return manager;
    }
}
