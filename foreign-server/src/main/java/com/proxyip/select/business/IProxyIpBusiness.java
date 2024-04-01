package com.proxyip.select.business;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.proxyip.select.bean.params.*;
import com.proxyip.select.common.bean.ProxyIp;

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
}
