package de.arvato.mybe.base.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig
{
    @Value("${bep.central.employee.cache-in-seconds:300}")
    int cacheEmployeeInSeconds;

    @Value("${bep.central.businessUnit.cache-in-minutes:2}")
    int cacheBusinessUnitInMinutes;
//
//    @Bean
//    public CacheManager getCacheManager()
//    {
//        SimpleCacheManager cacheManager = new SimpleCacheManager();
//
//        List list = new ArrayList<CaffeineCache>();
//
//        list.add(new CaffeineCache("employees", Caffeine.newBuilder().maximumSize(2000).expireAfterAccess(cacheEmployeeInSeconds, TimeUnit.SECONDS).build()));
//        cacheManager.setCaches(list);
//
//        return cacheManager;
//    }

    @Bean
    public CaffeineCache cacheEmployee() {
        return new CaffeineCache("employees",
                Caffeine.newBuilder()
                        .maximumSize(2000)
                        .expireAfterAccess(cacheEmployeeInSeconds, TimeUnit.SECONDS)
                        .build());
    }

    @Bean
    public CaffeineCache cacheBusinessUnit() {
        return new CaffeineCache("businessUnit",
                Caffeine.newBuilder()
                        .maximumSize(2000)
                        .expireAfterAccess(cacheBusinessUnitInMinutes, TimeUnit.MINUTES)
                        .build());
    }
}
