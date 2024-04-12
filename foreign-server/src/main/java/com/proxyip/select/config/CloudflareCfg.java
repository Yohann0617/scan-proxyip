package com.proxyip.select.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * CloudflareCfg
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/2/22 18:14
 */
@Configuration
@ConfigurationProperties(prefix = "cloudflare-cfg")
@Data
public class CloudflareCfg {

    private String zoneId;
    private String apiToken;
    private String rootDomain;
    private String proxyDomainPrefix;
}
