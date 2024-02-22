package com.proxyip.select.config;

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
public class CloudflareCfg {

    private String zoneId;
    private String apiToken;

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}
