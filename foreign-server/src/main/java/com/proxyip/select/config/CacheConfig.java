package com.proxyip.select.config;

import com.proxyip.select.utils.ExpiringCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.config
 * @className: CacheConfig
 * @author: Yohann
 * @date: 2024/8/1 21:56
 */
@Configuration
public class CacheConfig {

    @Bean
    public ExpiringCache<String, String> expiringCache() {
        return new ExpiringCache<>(2); // 线程池大小为2
    }
}