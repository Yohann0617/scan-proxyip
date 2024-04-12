package com.proxyip.select.business;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.proxyip.select.bean.params.*;
import com.proxyip.select.common.bean.ProxyIp;
import com.proxyip.select.common.bean.Tuple3;

import java.util.List;

/**
 * @author Yohann
 */
public interface IProxyIpBusiness {

    IPage<ProxyIp> pageList(ProxyIpPageParams params);

    void rmAllDnsRecords();

    void rmSingleIpDnsRecord(RmSingleIpDnsRecordParams params);

    void rmSingleDnsRecord(RmSingleDnsRecordParams params);

    void rmMoveFromDb(RmFromDbParams params);

    void addSingleDnsRecord(AddSingleDnsRecordParams params);

    void addDnsRecordsBatch(AddDnsRecordsBatchParams params);

    void addProxyIpToDbBatch(AddProxyIpToDbParams params);

    String getIpInfo(GetIpInfoParams ip);

    /**
     * 获取国家列表
     *
     * @return <国家,code,lowCode>
     */
    List<Tuple3<String, String, String>> getCountryList();
}
