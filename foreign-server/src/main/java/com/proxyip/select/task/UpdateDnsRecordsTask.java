package com.proxyip.select.task;

import com.proxyip.select.business.IDnsRecordBusiness;
import com.proxyip.select.config.CloudflareCfg;
import com.proxyip.select.config.DnsCfg;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * <p>
 * Main
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/2/22 15:41
 */
@Component
public class UpdateDnsRecordsTask implements ApplicationRunner {

    @Resource
    private DnsCfg dnsCfg;
    @Resource
    private CloudflareCfg cloudflareCfg;
    @Resource
    private IDnsRecordBusiness dnsRecordBusiness;
    @Resource
    private TaskScheduler taskScheduler;

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
//        // 校验参数
//        checkCfg();
//
//        if (dnsCfg.getPowerOnExec()) {
//            // 服务启动立马执行一次
//            dnsRecordBusiness.updateProxyIpTask();
//        }
//
//        // 执行定时任务
//        taskScheduler.schedule(dnsRecordBusiness::updateDbTask, new CronTrigger("0 1 0 * * ?"));
//        taskScheduler.schedule(dnsRecordBusiness::updateProxyIpTask, new CronTrigger(dnsCfg.getCron()));
    }
}
