package com.feihu1024.mapserver.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

    @Configuration
    @EnableCaching
    public class CaffeineCacheConfig {

        // =============== 静态瓦片缓存（30天） ===============
        // @Bean("staticTileCacheManager")
        // public CacheManager staticTileCacheManager() {
        //     CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        //     cacheManager.setCaffeine(Caffeine.newBuilder().maximumSize(20_000).expireAfterAccess(Duration.ofDays(30)));
        //     return cacheManager;
        // }

        // =============== 实时瓦片缓存（5分钟） ===============
        @Bean("tileCacheManager")
        public CacheManager realtimeTileCacheManager() {
            CaffeineCacheManager cacheManager = new CaffeineCacheManager();
            cacheManager.setCaffeine(Caffeine.newBuilder().maximumSize(10_000).expireAfterAccess(Duration.ofSeconds(60)));
            return cacheManager;
        }
    }
