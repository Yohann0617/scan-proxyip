package com.proxyip.select;

import com.proxyip.select.config.CloudflareCfg;
import com.proxyip.select.config.DnsCfg;
import com.proxyip.select.service.IDnsRecordService;
import com.proxyip.select.utils.DnsUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private IDnsRecordService dnsRecordService;
    @Resource
    private TaskScheduler taskScheduler;

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
            dnsRecordService.rmCfDnsRecords();

            // 添加DNS记录并保存到文件
            dnsRecordService.addLimitDnsRecordAndWriteToFile(ipAddresses, dnsCfg.getOutPutFile());
        }

        long end = System.currentTimeMillis();

        System.out.println("√√√ 更新DNS记录任务完成!!!总耗时：" + (end - begin) + " ms √√√");
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
