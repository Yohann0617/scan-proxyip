package com.proxyip.select.service.impl;

import com.proxyip.select.bean.IpWithCountryCode;
import com.proxyip.select.config.CloudflareCfg;
import com.proxyip.select.config.DnsCfg;
import com.proxyip.select.enums.EnumUtils;
import com.proxyip.select.enums.dict.CountryEnum;
import com.proxyip.select.service.IDnsRecordService;
import com.proxyip.select.utils.DnsUtils;
import com.proxyip.select.utils.NetUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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
public class DnsRecordServiceImpl implements IDnsRecordService {

    @Resource
    private DnsCfg dnsCfg;
    @Resource
    private CloudflareCfg cloudflareCfg;

    @Override
    public void addLimitDnsRecordAndWriteToFile(List<String> ipAddresses, String outputFile) {
        List<String> countryCodeList = Arrays.stream(CountryEnum.values()).map(CountryEnum::getCode).collect(Collectors.toList());
        List<String> releaseIps = dnsCfg.getReleaseIps();
        // 添加常年稳定的ip
        ipAddresses.addAll(releaseIps);
        List<IpWithCountryCode> list = ipAddresses.parallelStream().map(ip -> {
            // 获取国家代码
            String countryCode = DnsUtils.getIpCountry(ip, dnsCfg.getGeoipAuth());
            if (countryCode == null || EnumUtils.getEnumByCode(CountryEnum.class, countryCode) == null) {
                countryCode = DnsUtils.getIpCountry(ip);
            }
            return new IpWithCountryCode(ip, countryCode);
        }).collect(Collectors.toList());

        Map<String, List<IpWithCountryCode>> ipGroupByCountryMap = list.parallelStream()
                .filter(x -> {
                    if (releaseIps.contains(x.getIp())) {
                        return true;
                    }
                    return NetUtils.getPingResult(x.getIp());
                })
                .collect(Collectors.groupingBy(
                        IpWithCountryCode::getCountry,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                subList -> new ArrayList<>(subList.subList(0, Math.min(5, subList.size())))
                        )));

        // 添加cf记录
        ipGroupByCountryMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList())
                .parallelStream().forEach(x -> {
            if (countryCodeList.contains(x.getCountry())) {
                String prefix = EnumUtils.getEnumByCode(CountryEnum.class, x.getCountry()).getLowCode() + "." + cloudflareCfg.getProxyDomainPrefix();
                DnsUtils.addCfDnsRecords(prefix, x.getIp(), cloudflareCfg.getZoneId(), cloudflareCfg.getApiToken());
            }
        });
        System.out.println("√√√ 所有DNS记录添加完成!!! √√√");

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
            System.out.println("√√√ 获取proxyIps任务完成，文件位置：" + dnsCfg.getOutPutFile() + " √√√");

            // 发送到网盘api
            if (!"".equals(dnsCfg.getUploadApi())) {
                DnsUtils.uploadFileToNetDisc(dnsCfg.getOutPutFile(), dnsCfg.getUploadApi());
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
                        String countryCode = DnsUtils.getIpCountry(ipAddress, dnsCfg.getGeoipAuth());
                        if (countryCode == null || EnumUtils.getEnumByCode(CountryEnum.class, countryCode) == null) {
                            countryCode = DnsUtils.getIpCountry(ipAddress);
                        }

                        // 添加cf记录
                        if (countryCodeList.contains(countryCode)) {
                            String prefix = EnumUtils.getEnumByCode(CountryEnum.class, countryCode).getLowCode() + "." + cloudflareCfg.getProxyDomainPrefix();
                            DnsUtils.addCfDnsRecords(prefix, ipAddress, cloudflareCfg.getZoneId(), cloudflareCfg.getApiToken());
                        }

                        // 写入文件
                        writer.write(ipAddress + " " + (countryCode == null ? "" : countryCode) + "\n");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("IP：" + ipAddress + " 无法ping通，已跳过");
                }
            });

            System.out.println("√√√ 所有DNS记录添加完成!!! √√√");
            System.out.println("√√√ 获取proxyIps任务完成，文件位置：" + dnsCfg.getOutPutFile() + " √√√");
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
        DnsUtils.removeCfDnsRecords(proxyDomainList, cloudflareCfg.getZoneId(), cloudflareCfg.getApiToken());
        System.out.println("√√√ 所有DNS记录已清除成功，开始添加DNS记录... √√√");
    }
}
