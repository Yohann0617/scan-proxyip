package com.proxyip.select.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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

    private String cron;
    private String dnsServer;
    private String proxyDomain;
//    private String geoipAuth;
    private String outPutFile;
    private String rootDomain;
    private String hkDomainPrefix;
    private String sgDomainPrefix;
    private String krDomainPrefix;
    private String jpDomainPrefix;
    private String usDomainPrefix;
    private String ukDomainPrefix;
    private String nlDomainPrefix;
    private String deDomainPrefix;

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getRootDomain() {
        return rootDomain;
    }

    public void setRootDomain(String rootDomain) {
        this.rootDomain = rootDomain;
    }

    public String getHkDomainPrefix() {
        return hkDomainPrefix;
    }

    public void setHkDomainPrefix(String hkDomainPrefix) {
        this.hkDomainPrefix = hkDomainPrefix;
    }

    public String getSgDomainPrefix() {
        return sgDomainPrefix;
    }

    public void setSgDomainPrefix(String sgDomainPrefix) {
        this.sgDomainPrefix = sgDomainPrefix;
    }

    public String getKrDomainPrefix() {
        return krDomainPrefix;
    }

    public void setKrDomainPrefix(String krDomainPrefix) {
        this.krDomainPrefix = krDomainPrefix;
    }

    public String getJpDomainPrefix() {
        return jpDomainPrefix;
    }

    public void setJpDomainPrefix(String jpDomainPrefix) {
        this.jpDomainPrefix = jpDomainPrefix;
    }

    public String getUsDomainPrefix() {
        return usDomainPrefix;
    }

    public void setUsDomainPrefix(String usDomainPrefix) {
        this.usDomainPrefix = usDomainPrefix;
    }

    public String getUkDomainPrefix() {
        return ukDomainPrefix;
    }

    public void setUkDomainPrefix(String ukDomainPrefix) {
        this.ukDomainPrefix = ukDomainPrefix;
    }

    public String getNlDomainPrefix() {
        return nlDomainPrefix;
    }

    public void setNlDomainPrefix(String nlDomainPrefix) {
        this.nlDomainPrefix = nlDomainPrefix;
    }

    public String getDeDomainPrefix() {
        return deDomainPrefix;
    }

    public void setDeDomainPrefix(String deDomainPrefix) {
        this.deDomainPrefix = deDomainPrefix;
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

//    public void setGeoipAuth(String geoipAuth) {
//        this.geoipAuth = geoipAuth;
//    }

    public String getDnsServer() {
        return dnsServer;
    }

    public String getProxyDomain() {
        return proxyDomain;
    }

//    public String getGeoipAuth() {
//        return geoipAuth;
//    }
}
