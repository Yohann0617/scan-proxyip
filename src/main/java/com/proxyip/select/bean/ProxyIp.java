package com.proxyip.select.bean;

import java.io.Serializable;

/**
 * 代理ip表
 *
 * @author Yohann
 */
public class ProxyIp implements Serializable {
    private String id;

    private String country;

    private String ip;

    private Integer pingValue;

    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "ProxyIp{" +
                "id='" + id + '\'' +
                ", country='" + country + '\'' +
                ", ip='" + ip + '\'' +
                ", pingValue=" + pingValue +
                '}';
    }

    public ProxyIp() {
    }

    public ProxyIp(String id, String country, String ip, Integer pingValue) {
        this.id = id;
        this.country = country;
        this.ip = ip;
        this.pingValue = pingValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPingValue() {
        return pingValue;
    }

    public void setPingValue(Integer pingValue) {
        this.pingValue = pingValue;
    }
}