package com.proxyip.select.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.proxyip.select.bean.params.*;
import com.proxyip.select.common.bean.ResponseData;
import com.proxyip.select.common.bean.ProxyIp;
import com.proxyip.select.business.IProxyIpBusiness;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.controller
 * @className: ProxyIpController
 * @author: Yohann
 * @date: 2024/3/30 14:33
 */
@RestController
@RequestMapping(path = "/api/proxyIp")
public class ProxyIpController {

    @Resource
    private IProxyIpBusiness proxyIpBusiness;

    @PostMapping(path = "/page")
    public ResponseData<IPage<ProxyIp>> page(@RequestBody ProxyIpPageParams params) {
        return ResponseData.successData(proxyIpBusiness.pageList(params), "获取分页成功");
    }

    @PostMapping(path = "/rmMoveFromDb")
    public ResponseData<Void> rmMoveFromDb(@RequestBody RmFromDbParams params) {
        proxyIpBusiness.rmMoveFromDb(params);
        return ResponseData.successData("删除数据库中数据成功");
    }

    @PostMapping(path = "/rmAllDnsRecords")
    public ResponseData<Void> rmAllDnsRecords() {
        proxyIpBusiness.rmAllDnsRecords();
        return ResponseData.successData("删除所有dns记录成功");
    }

    @PostMapping(path = "/rmSingleIpDnsRecord")
    public ResponseData<Void> rmSingleIpDnsRecord(@RequestBody RmSingleIpDnsRecordParams params) {
        proxyIpBusiness.rmSingleIpDnsRecord(params);
        return ResponseData.successData("删除该代理域名的某个ip地址的dns记录成功");
    }

    @PostMapping(path = "/rmSingleDnsRecord")
    public ResponseData<Void> rmSingleDnsRecord(@RequestBody RmSingleDnsRecordParams params) {
        proxyIpBusiness.rmSingleDnsRecord(params);
        return ResponseData.successData("删除该代理域名所有dns记录成功");
    }

    @PostMapping(path = "/addSingleDnsRecord")
    public ResponseData<Void> addSingleDnsRecord(@RequestBody AddSingleDnsRecordParams params) {
        proxyIpBusiness.addSingleDnsRecord(params);
        return ResponseData.successData("添加dns记录成功");
    }

    @PostMapping(path = "/addDnsRecordsBatch")
    public ResponseData<Void> addDnsRecordsBatch(@RequestBody AddDnsRecordsBatchParams params) {
        proxyIpBusiness.addDnsRecordsBatch(params);
        return ResponseData.successData("添加多条dns记录成功");
    }

    @PostMapping(path = "/addProxyIpToDbBatch")
    public ResponseData<Void> addProxyIpToDbBatch(@RequestBody AddProxyIpToDbParams params) {
        proxyIpBusiness.addProxyIpToDbBatch(params);
        return ResponseData.successData("添加多个proxyIp到数据库成功");
    }
}
