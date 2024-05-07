package com.proxyip.select.business.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.proxyip.select.common.bean.ProxyIp;
import com.proxyip.select.common.exception.BusinessException;
import com.proxyip.select.common.service.IApiService;
import com.proxyip.select.common.service.IProxyIpService;
import com.proxyip.select.common.utils.IdGen;
import com.proxyip.select.common.utils.NetUtils;
import com.proxyip.select.common.enums.EnumUtils;
import com.proxyip.select.common.enums.dict.CountryEnum;
import com.proxyip.select.config.CloudflareCfg;
import com.proxyip.select.config.DnsCfg;
import com.proxyip.select.business.IDnsRecordBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * <p>
 * DnsRecordServiceImpl
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/3/21 14:26
 */
@Service
@Slf4j
public class DnsRecordBusinessImpl implements IDnsRecordBusiness {

    @Resource
    private DnsCfg dnsCfg;
    @Resource
    private CloudflareCfg cloudflareCfg;
    @Resource
    private IProxyIpService proxyIpService;
    @Resource
    private IApiService apiService;
    @Resource
    private IdGen idGen;

    @Override
    public void addLimitDnsRecordAndWriteToFile(List<String> ipAddresses, String outputFile) {
        List<String> countryCodeList = Arrays.stream(CountryEnum.values()).map(CountryEnum::getCode).collect(Collectors.toList());
        List<String> releaseIps = dnsCfg.getReleaseIps();
        // 添加常年稳定的ip
        ipAddresses.addAll(releaseIps);
        log.info("√√√ 共获取到{}个proxyIp √√√", ipAddresses.size());
        List<ProxyIp> list = ipAddresses.parallelStream()
                .filter(x -> {
                    if (releaseIps.contains(x)) {
                        return true;
                    }
                    return NetUtils.getPingResult(x);
                })
                .filter(ip -> CollectionUtil.isEmpty(proxyIpService.list(new LambdaQueryWrapper<ProxyIp>().eq(ProxyIp::getIp, ip))))
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

        // 持久化到数据库
        proxyIpService.saveBatch(list);

        // 分组并保留五条记录
        Map<String, List<ProxyIp>> ipGroupByCountryMap = list.parallelStream()
                .collect(Collectors.groupingBy(
                        ProxyIp::getCountry,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                subList -> {
                                    // 根据ping值排序
                                    subList.sort(Comparator.comparing(ProxyIp::getPingValue));
                                    if (subList.size() < 5) {
                                        subList.addAll(Optional.ofNullable(proxyIpService.list(new LambdaQueryWrapper<ProxyIp>()
                                                        .eq(ProxyIp::getCountry, subList.get(0).getCountry())
                                                        .orderByAsc(ProxyIp::getPingValue)
                                                        .last("limit " + (5 - subList.size()))))
                                                .filter(CollectionUtil::isNotEmpty).orElseGet(Collections::emptyList).stream()
                                                .filter(x -> !subList.contains(x))
                                                .collect(Collectors.toList()));
                                    }
                                    return new ArrayList<>(subList.subList(0, Math.min(5, subList.size())));
                                }
                        )));

        // 没有解析到的国家便使用数据库中的proxyIp
        countryCodeList.parallelStream().forEach(countryCode -> {
            if (!ipGroupByCountryMap.containsKey(countryCode)) {
                List<ProxyIp> proxyIpList = proxyIpService.list(new LambdaQueryWrapper<ProxyIp>()
                        .eq(ProxyIp::getCountry, countryCode)
                        .orderByAsc(ProxyIp::getPingValue)
                        .last("limit 5"));
                if (CollectionUtil.isNotEmpty(proxyIpList)) {
                    ipGroupByCountryMap.put(countryCode, proxyIpList);
                }
            }
        });

        // 添加cf记录
        ipGroupByCountryMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList())
                .parallelStream().forEach(x -> {
                    if (countryCodeList.contains(x.getCountry())) {
                        String prefix =
                                EnumUtils.getEnumByCode(CountryEnum.class, x.getCountry()).getLowCode() + "." + cloudflareCfg.getProxyDomainPrefix();
                        apiService.addCfDnsRecords(prefix, x.getIp(), cloudflareCfg.getZoneId(), cloudflareCfg.getApiToken());
                    }
                });
        log.info("√√√ 所有DNS记录添加完成!!! √√√");

        // 写入文件
        CompletableFuture.runAsync(() -> {
            try (FileWriter writer = new FileWriter(outputFile)) {
                ipGroupByCountryMap.forEach((country, ipList) -> {
                    String ipStr = ipList.parallelStream().map(x -> "\t" + x.getIp()).collect(Collectors.joining("\n"));
                    try {
                        writer.write(country + ":\n" + ipStr + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new RuntimeException("写入文件：" + outputFile + "失败");
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException("写入文件：" + outputFile + "失败");
            }
            log.info("√√√ 获取proxyIps任务完成，文件位置：{} √√√", outputFile);

            // 发送到网盘api
            if (!"".equals(dnsCfg.getUploadApi())) {
                apiService.uploadFileToNetDisc(outputFile, dnsCfg.getUploadApi());
            }
        });
    }

    @Override
    public void addDnsRecordAndWriteToFile(List<String> ipAddresses, String outputFile) {
        List<String> countryCodeList = Arrays.stream(CountryEnum.values()).map(CountryEnum::getCode).collect(Collectors.toList());
        List<String> releaseIps = dnsCfg.getReleaseIps();
        try (FileWriter writer = new FileWriter(outputFile)) {
            // 添加常年稳定的ip
            ipAddresses.addAll(releaseIps);
            // 循环异步执行
            ipAddresses.parallelStream().forEach(ipAddress -> {
                if (releaseIps.contains(ipAddress) || NetUtils.getPingResult(ipAddress)) {
                    try {
                        // 获取国家代码
                        String countryCode = apiService.getIpCountry(ipAddress, dnsCfg.getGeoipAuth());
                        if (countryCode == null || EnumUtils.getEnumByCode(CountryEnum.class, countryCode) == null) {
                            countryCode = apiService.getIpCountry(ipAddress);
                        }

                        // 添加cf记录
                        if (countryCodeList.contains(countryCode)) {
                            String prefix =
                                    EnumUtils.getEnumByCode(CountryEnum.class, countryCode).getLowCode() + "." + cloudflareCfg.getProxyDomainPrefix();
                            apiService.addCfDnsRecords(prefix, ipAddress, cloudflareCfg.getZoneId(), cloudflareCfg.getApiToken());
                        }

                        // 写入文件
                        writer.write(ipAddress + " " + (countryCode == null ? "" : countryCode) + "\n");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    log.info("IP：{} 无法ping通，已跳过", ipAddress);
                }
            });

            log.info("√√√ 所有DNS记录添加完成!!! √√√");
            log.info("√√√ 获取proxyIps任务完成，文件位置：{} √√√", outputFile);

            // 发送到网盘api
            if (!"".equals(dnsCfg.getUploadApi())) {
                apiService.uploadFileToNetDisc(outputFile, dnsCfg.getUploadApi());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("写入文件：" + outputFile + "失败");
        }
    }

    @Override
    public void rmCfDnsRecords() {
        List<String> proxyDomainList = Arrays.stream(CountryEnum.values())
                .map(x -> x.getLowCode() + "." + cloudflareCfg.getProxyDomainPrefix() + "." + cloudflareCfg.getRootDomain())
                .collect(Collectors.toList());
        apiService.removeCfDnsRecords(proxyDomainList, cloudflareCfg.getZoneId(), cloudflareCfg.getApiToken());
        log.info("√√√ 所有DNS记录已清除成功，开始添加DNS记录... √√√");
    }

    @Override
    public void rmIpInDb() {
        log.info("当前时间：{}，开始清除数据库中无效的ip...", getNowDateTime());
        List<String> voidIdList = Optional.ofNullable(proxyIpService.list())
                .filter(CollectionUtil::isNotEmpty).orElseGet(Collections::emptyList).parallelStream()
                .filter(x -> !dnsCfg.getReleaseIps().contains(x.getIp()))
                .filter(x -> !NetUtils.getPingResult(x.getIp()))
                .map(ProxyIp::getId)
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(voidIdList)) {
            proxyIpService.removeByIds(voidIdList);
        }
        log.info("√√√ 清除数据库中无效ip任务已完成，共清理{}个无效ip √√√", voidIdList.size());
    }

    @Override
    public void updateProxyIpTask() {
        log.info("当前时间：{}，开始更新DNS记录...", getNowDateTime());
        long begin = System.currentTimeMillis();
        // 获取proxyIps
        List<String> proxyIpsFromZip = apiService.getProxyIpFromZip(dnsCfg.getZipUrl(), dnsCfg.getZipPorts());
        List<String> proxyIpsFromDomain = apiService.resolveDomain(dnsCfg.getProxyDomain(), dnsCfg.getDnsServer());
        proxyIpsFromDomain.addAll(proxyIpsFromZip);
        Set<String> set = new HashSet<>(proxyIpsFromDomain);
        set.addAll(getProxyIpFromCsv());

        if (set.size() != 0) {
            // 清除dns旧记录
            rmCfDnsRecords();

            // 添加DNS记录并保存到文件
            addLimitDnsRecordAndWriteToFile(new ArrayList<>(set), dnsCfg.getOutPutFile());
        }

        long end = System.currentTimeMillis();

        log.info("√√√ 更新DNS记录任务完成!!!总耗时：{} ms √√√", (end - begin));
    }

    @Override
    public void updateDbTask() {
        rmIpInDb();
        updateIpPingValueInDb();
    }

    @Override
    public void updateIpPingValueInDb() {
        log.info("当前时间：{}，开始更新数据库中ip的ping值...", getNowDateTime());
        List<ProxyIp> proxyIpList = Optional.ofNullable(proxyIpService.list())
                .filter(CollectionUtil::isNotEmpty).orElseGet(Collections::emptyList).parallelStream()
                .peek(x -> {
                    Integer pingValue = NetUtils.getPingValue(x.getIp());
                    x.setPingValue(pingValue == null ? 999999 : pingValue);
                })
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(proxyIpList)) {
            proxyIpService.updateBatchById(proxyIpList);
        }
        log.info("√√√ 更新数据库中ip的ping值任务已完成 √√√");
    }

    @Override
    public List<String> getProxyIpFromCsv() {
        if (StrUtil.isBlank(dnsCfg.getCsvDir())) {
            return new ArrayList<>();
        }

        File csvDir = new File(dnsCfg.getCsvDir());
        if (!csvDir.exists() || !csvDir.isDirectory()) {
            throw new BusinessException(-1, "目录：" + dnsCfg.getCsvDir() + " 不存在");
        }
        return Arrays.stream(Objects.requireNonNull(csvDir.listFiles()))
                .filter(file -> file.getName().contains(".csv"))
                .map(file -> {
                    try (CsvReader reader = new CsvReader()) {
                        CsvData data = reader.read(file, CharsetUtil.CHARSET_UTF_8);
                        return data.getRows().stream()
                                .map(row -> row.getRawList().get(0))
                                .filter(Validator::isIpv4)
                                .collect(Collectors.toList());
                    } catch (IOException e) {
                        log.error("读取文件：{} 内容失败", file.getName(), e);
                        throw new RuntimeException(e);
                    }
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    private String getNowDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
