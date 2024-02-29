package com.proxyip.select.bean;

/**
 * <p>
 * IpWithCountryCode
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/2/29 16:28
 */
public class IpWithCountryCode {

    private String ip;
    private String country;

    public IpWithCountryCode(String ip, String country) {
        this.ip = ip;
        this.country = country;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
