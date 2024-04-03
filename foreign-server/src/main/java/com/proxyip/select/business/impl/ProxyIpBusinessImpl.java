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
import com.proxyip.select.common.utils.IdGen;
import com.proxyip.select.common.utils.NetUtils;
import com.proxyip.select.config.CloudflareCfg;
import com.proxyip.select.config.DnsCfg;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private DnsCfg dnsCfg;
    @Resource
    private CloudflareCfg cloudflareCfg;
    @Resource
    private IProxyIpService proxyIpService;
    @Resource
    private IDnsRecordBusiness dnsRecordService;
    @Resource
    private IApiService apiService;
    @Resource
    private IdGen idGen;

    @Override
    public IPage<ProxyIp> pageList(ProxyIpPageParams params) {
        return proxyIpService.page(new Page<>(params.getCurrentPage(), params.getPageSize()),
                new LambdaQueryWrapper<ProxyIp>()
                        .likeRight(ProxyIp::getCountry, params.getKeyword()).or()
                        .likeRight(ProxyIp::getIp, params.getKeyword())
                        .orderByDesc(ProxyIp::getCreateTime));
    }

    @Override
    public void rmAllDnsRecords() {
        dnsRecordService.rmCfDnsRecords();
    }

    @Override
    public void rmSingleIpDnsRecord(RmSingleIpDnsRecordParams params) {
        apiService.removeCfSingleDnsRecords(
                params.getProxyDomain() + "." + cloudflareCfg.getProxyDomainPrefix() + "." + cloudflareCfg.getRootDomain(),
                params.getIp(),
                cloudflareCfg.getZoneId(),
                cloudflareCfg.getApiToken());
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
            String prefix = proxyIp.getCountry().toLowerCase(Locale.ROOT) + "." + cloudflareCfg.getProxyDomainPrefix();
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
                        String prefix = proxyIp.getCountry().toLowerCase(Locale.ROOT) + "." + cloudflareCfg.getProxyDomainPrefix();
                        apiService.addCfDnsRecords(
                                prefix,
                                proxyIp.getIp(),
                                cloudflareCfg.getZoneId(),
                                cloudflareCfg.getApiToken());
                    });
                });
    }

    @Override
    public void addProxyIpToDbBatch(AddProxyIpToDbParams params) {
        Optional.ofNullable(params.getIpList())
                .filter(CommonUtils::isNotEmpty).ifPresent(ipList -> {
                    List<ProxyIp> list = ipList.parallelStream()
                            .map(String::trim)
                            .map(ip -> {
                                // 获取国家代码
                                String countryCode = apiService.getIpCountry(ip, dnsCfg.getGeoipAuth());
                                if (countryCode == null || EnumUtils.getEnumByCode(CountryEnum.class, countryCode) == null) {
                                    countryCode = apiService.getIpCountry(ip);
                                }

                                ProxyIp proxyIp = new ProxyIp();
                                proxyIp.setId(String.valueOf(idGen.nextId()));
                                proxyIp.setCountry(countryCode);
                                proxyIp.setIp(ip);
                                Integer pingValue = NetUtils.getPingValue(ip);
                                proxyIp.setPingValue(pingValue == null ? 999999 : pingValue);
                                proxyIp.setCreateTime(LocalDateTime.now());
                                return proxyIp;
                            }).collect(Collectors.toList());
                    list.removeIf(x -> Optional.ofNullable(proxyIpService.getOne(new LambdaQueryWrapper<ProxyIp>()
                            .eq(ProxyIp::getIp, x.getIp()))).isPresent());
                    proxyIpService.saveBatch(list);
                });
    }

    @Override
    public String getIpInfo(GetIpInfoParams params) {
        return apiService.getIpInfo(params.getIp(), "");
    }
}
