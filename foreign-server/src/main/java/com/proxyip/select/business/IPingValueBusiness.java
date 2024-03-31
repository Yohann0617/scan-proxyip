package com.proxyip.select.business;

import com.proxyip.select.common.bean.ProxyIp;

import java.util.List;

/**
 * @author Yohann
 */
public interface IPingValueBusiness {

    /**
     * 获取带有ping值的代理ip列表
     *
     * @return 代理ip列表
     */
    List<ProxyIp> getIpWithPingValueList();
}
