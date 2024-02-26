package com.proxyip.select;

import com.proxyip.select.config.CloudflareCfg;
import com.proxyip.select.config.DnsCfg;
import com.proxyip.select.enums.EnumUtils;
import com.proxyip.select.enums.dict.CountryEnum;
import com.proxyip.select.utils.DnsUtils;
import com.proxyip.select.utils.NetUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Main
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/2/22 15:41
 */
@Component
public class Main implements ApplicationRunner {

    @Resource
    private DnsCfg dnsCfg;
    @Resource
    private CloudflareCfg cloudflareCfg;
    @Resource
    private TaskScheduler taskScheduler;

    /**
     * 添加dns记录并将ip地址写入文件
     *
     * @param ipAddresses ip列表
     * @param outputFile  输出文件全路径
     * @throws IOException 异常
     */
    private void addDnsRecordAndWriteToFile(List<String> ipAddresses, String outputFile) {
        List<String> countryCodeList = Arrays.stream(CountryEnum.values()).map(CountryEnum::getCode).collect(Collectors.toList());
        try (FileWriter writer = new FileWriter(outputFile)) {
            // 添加常年稳定的ip
            ipAddresses.addAll(DnsUtils.RELEASE_IP_LIST);
            // 循环异步执行
            ipAddresses.parallelStream().forEach(ipAddress -> {
                if (DnsUtils.RELEASE_IP_LIST.contains(ipAddress) || NetUtils.getPingResult(ipAddress)) {
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

    /**
     * 更新dns记录任务
     */
    private void updateProxyIpTask() {
        System.out.println("当前时间：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "，开始更新DNS记录...");
        long begin = System.currentTimeMillis();
        // 获取proxyIps
        List<String> ipAddresses = DnsUtils.resolveDomain(dnsCfg.getProxyDomain(), dnsCfg.getDnsServer());

        if (ipAddresses.size() != 0) {
            // 清除dns旧记录
            rmCfDnsRecords();

            // 添加DNS记录并保存到文件
            addDnsRecordAndWriteToFile(ipAddresses, dnsCfg.getOutPutFile());

            // 发送到网盘api
            if (!"".equals(dnsCfg.getUploadApi())) {
                DnsUtils.uploadFileToNetDisc(dnsCfg.getOutPutFile(), dnsCfg.getUploadApi());
            }
        }

        long end = System.currentTimeMillis();

        System.out.println("总耗时：" + (end - begin) + " ms");
    }

    /**
     * 清除dns旧记录
     */
    private void rmCfDnsRecords() {
        List<String> proxyDomainList = Arrays.stream(CountryEnum.values())
                .map(x -> x.getLowCode() + "." + cloudflareCfg.getProxyDomainPrefix() + "." + cloudflareCfg.getRootDomain())
                .collect(Collectors.toList());
        DnsUtils.removeCfDnsRecords(proxyDomainList, cloudflareCfg.getZoneId(), cloudflareCfg.getApiToken());
        System.out.println("√√√ 所有DNS记录已清除成功，开始添加DNS记录... √√√");
    }

    /**
     * 校验参数是否配置
     */
    private void checkCfg() {
        if ("".equals(cloudflareCfg.getZoneId())) {
            throw new RuntimeException("请配置zoneId");
        }
        if ("".equals(cloudflareCfg.getApiToken())) {
            throw new RuntimeException("请配置apiToken");
        }
        if ("".equals(cloudflareCfg.getRootDomain())) {
            throw new RuntimeException("请配置root域名（cf托管的域名）");
        }
        if ("".equals(cloudflareCfg.getProxyDomainPrefix())) {
            throw new RuntimeException("请配置优选ip域名前缀");
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        // 校验参数
        checkCfg();

        if (dnsCfg.getPowerOnExec()) {
            // 服务启动立马执行一次
            updateProxyIpTask();
        }

        // 执行定时任务
        taskScheduler.schedule(this::updateProxyIpTask, new CronTrigger(dnsCfg.getCron()));
    }
}
