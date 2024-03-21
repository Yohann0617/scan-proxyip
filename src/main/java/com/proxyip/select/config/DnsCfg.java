package com.proxyip.select.config;

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
public class DnsCfg {

    private Boolean powerOnExec;
    private String cron;
    private String dnsServer;
    private String proxyDomain;
    private String geoipAuth;
    private String outPutFile;
    private String uploadApi;
    private List<String> releaseIps;

    public List<String> getReleaseIps() {
        return releaseIps;
    }

    public void setReleaseIps(List<String> releaseIps) {
        this.releaseIps = releaseIps;
    }

    public Boolean getPowerOnExec() {
        return powerOnExec;
    }

    public void setPowerOnExec(Boolean powerOnExec) {
        this.powerOnExec = powerOnExec;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getOutPutFile() {
        return outPutFile;
    }

    public void setOutPutFile(String outPutFile) {
        this.outPutFile = outPutFile;
    }

    public void setDnsServer(String dnsServer) {
        this.dnsServer = dnsServer;
    }

    public void setProxyDomain(String proxyDomain) {
        this.proxyDomain = proxyDomain;
    }

    public void setGeoipAuth(String geoipAuth) {
        this.geoipAuth = geoipAuth;
    }

    public String getDnsServer() {
        return dnsServer;
    }

    public String getProxyDomain() {
        return proxyDomain;
    }

    public String getGeoipAuth() {
        return geoipAuth;
    }

    public String getUploadApi() {
        return uploadApi;
    }

    public void setUploadApi(String uploadApi) {
        this.uploadApi = uploadApi;
    }
}
