package com.tnong.boot.framework.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 配置 Caffeine 缓存管理器
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        // 默认缓存配置：最大1000个条目，写入后2小时过期
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(2, TimeUnit.HOURS)
                .recordStats());

        return cacheManager;
    }

    /**
     * 企业微信 access_token 专用缓存
     * 有效期：7000秒（略小于企业微信的7200秒，留出200秒缓冲）
     */
    @Bean
    public Caffeine<Object, Object> wecomTokenCaffeine() {
        return Caffeine.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(7000, TimeUnit.SECONDS)
                .recordStats();
    }
}
