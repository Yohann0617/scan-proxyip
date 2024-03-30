package com.proxyip.select.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * ProxyIpApiCfg
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/3/29 18:55
 */
@Configuration
@ConfigurationProperties(prefix = "api.proxyip")
public class ProxyIpApiCfg {

    private String getListApi;
    private String updatePingValueApi;

    public String getGetListApi() {
        return getListApi;
    }

    public void setGetListApi(String getListApi) {
        this.getListApi = getListApi;
    }

    public String getUpdatePingValueApi() {
        return updatePingValueApi;
    }

    public void setUpdatePingValueApi(String updatePingValueApi) {
        this.updatePingValueApi = updatePingValueApi;
    }
}
