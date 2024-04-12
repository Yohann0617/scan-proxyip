package com.proxyip.select.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * <p>
 * DnsConfig
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/2/22 18:03
 */
@Configuration
@ConfigurationProperties(prefix = "dns-cfg")
@Data
public class DnsCfg {

    private Boolean powerOnExec;
    private String cron;
    private String dnsServer;
    private List<String> proxyDomain;
    private String geoipAuth;
    private String outPutFile;
    private String uploadApi;
    private List<String> releaseIps;
    private String zipUrl;
    private List<Integer> zipPorts;
}
