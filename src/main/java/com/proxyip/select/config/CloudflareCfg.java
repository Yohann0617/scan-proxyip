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
    private String rootDomain;
    private String proxyDomainPrefix;

    public String getRootDomain() {
        return rootDomain;
    }

    public void setRootDomain(String rootDomain) {
        this.rootDomain = rootDomain;
    }

    public String getProxyDomainPrefix() {
        return proxyDomainPrefix;
    }

    public void setProxyDomainPrefix(String proxyDomainPrefix) {
        this.proxyDomainPrefix = proxyDomainPrefix;
    }

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

    @Override
    public String toString() {
        return "CloudflareCfg{" +
                "zoneId='" + zoneId + '\'' +
                ", apiToken='" + apiToken + '\'' +
                ", rootDomain='" + rootDomain + '\'' +
                ", proxyDomainPrefix='" + proxyDomainPrefix + '\'' +
                '}';
    }
}
