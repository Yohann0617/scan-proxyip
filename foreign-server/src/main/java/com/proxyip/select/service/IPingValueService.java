package com.proxyip.select.service;

import com.proxyip.select.common.bean.ProxyIp;

import java.util.List;

/**
 * @author Yohann
 */
public interface IPingValueService {

    /**
     * 获取带有ping值的代理ip列表
     *
     * @return 代理ip列表
     */
    List<ProxyIp> getIpWithPingValueList();
}
