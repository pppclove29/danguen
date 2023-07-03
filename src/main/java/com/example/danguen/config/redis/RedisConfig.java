package com.example.danguen.config.redis;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class RedisConfig {

//    @Bean
//    public CacheManager cacheManager(){
//        ConcurrentMapCacheManager manager = new ConcurrentMapCacheManager();
//        manager.setAllowNullValues(false);
//        manager.setCacheNames(List.of("post"));
//
//        return manager;
//    }
}
