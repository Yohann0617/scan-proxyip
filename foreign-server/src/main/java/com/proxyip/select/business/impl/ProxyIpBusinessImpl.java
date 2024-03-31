package com.proxyip.select.business.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.proxyip.select.bean.params.*;
import com.proxyip.select.business.IDnsRecordBusiness;
import com.proxyip.select.common.bean.ProxyIp;
import com.proxyip.select.business.IProxyIpBusiness;
import com.proxyip.select.common.enums.EnumUtils;
import com.proxyip.select.common.enums.dict.CountryEnum;
import com.proxyip.select.common.service.IApiService;
import com.proxyip.select.common.service.IProxyIpService;
import com.proxyip.select.common.utils.CommonUtils;
import com.proxyip.select.config.CloudflareCfg;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Optional;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.service.impl
 * @className: ProxyIpServiceImpl
 * @author: Yohann
 * @date: 2024/3/30 14:44
 */
@Service
public class ProxyIpBusinessImpl implements IProxyIpBusiness {

    @Resource
    private CloudflareCfg cloudflareCfg;
    @Resource
    private IProxyIpService proxyIpService;
    @Resource
    private IDnsRecordBusiness dnsRecordService;
    @Resource
    private IApiService apiService;

    @Override
    public IPage<ProxyIp> pageList(ProxyIpPageParams params) {
        return proxyIpService.page(new Page<>(params.getCurrentPage(), params.getPageSize()),
                new LambdaQueryWrapper<ProxyIp>()
                        .likeRight(ProxyIp::getCountry, params.getKeyword()).or()
                        .likeRight(ProxyIp::getIp, params.getKeyword())
                        .orderByAsc(ProxyIp::getPingValue));
    }

    @Override
    public void rmAllDnsRecords() {
        dnsRecordService.rmCfDnsRecords();
    }

    @Override
    public void rmSingleDnsRecord(RmSingleDnsRecordParams params) {
        apiService.removeCfDnsRecords(
                Collections.singletonList(params.getProxyDomain() + "." + cloudflareCfg.getProxyDomainPrefix() + "." + cloudflareCfg.getRootDomain()),
                cloudflareCfg.getZoneId(),
                cloudflareCfg.getApiToken());
    }

    @Override
    public void addSingleDnsRecord(AddSingleDnsRecordParams params) {
        Optional.ofNullable(proxyIpService.getById(params.getId())).ifPresent(proxyIp -> {
            String prefix = EnumUtils.getEnumByCode(CountryEnum.class, proxyIp.getCountry()).getLowCode() + "."
                    + cloudflareCfg.getProxyDomainPrefix();
            apiService.addCfDnsRecords(
                    prefix,
                    proxyIp.getIp(),
                    cloudflareCfg.getZoneId(),
                    cloudflareCfg.getApiToken());
        });
    }

    @Override
    public void rmMoveFromDb(RmFromDbParams params) {
        proxyIpService.removeByIds(params.getIds());
    }

    @Override
    public void addDnsRecordsBatch(AddDnsRecordsBatchParams params) {
        Optional.ofNullable(proxyIpService.listByIds(params.getIds()))
                .filter(CommonUtils::isNotEmpty).ifPresent(list -> {
                    list.parallelStream().forEach(proxyIp -> {
                        String prefix = EnumUtils.getEnumByCode(CountryEnum.class, proxyIp.getCountry()).getLowCode() + "."
                                + cloudflareCfg.getProxyDomainPrefix();
                        apiService.addCfDnsRecords(
                                prefix,
                                proxyIp.getIp(),
                                cloudflareCfg.getZoneId(),
                                cloudflareCfg.getApiToken());
                    });
                });
    }
}
