package com.proxyip.select.business;

import com.proxyip.select.common.bean.ProxyIp;
import com.proxyip.select.common.bean.Tuple3;

import java.util.List;

/**
 * <p>
 * IDnsRecordService
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/3/21 14:26
 */
public interface IDnsRecordBusiness {

    /**
     * 添加dns记录并将ip地址写入文件
     * 每个国家5条dns记录
     *
     * @param ipAddresses ip列表
     * @param outputFile  输出文件全路径
     */
    void addLimitDnsRecordAndWriteToFile(List<String> ipAddresses, String outputFile);

    /**
     * 添加dns记录并将ip地址写入文件
     *
     * @param ipAddresses ip列表
     * @param outputFile  输出文件全路径
     */
    void addDnsRecordAndWriteToFile(List<String> ipAddresses, String outputFile);

    /**
     * 清除dns旧记录
     */
    void rmCfDnsRecords();

    /**
     * 清除数据库中无效的ip
     */
    void rmIpInDb();

    /**
     * 更新dns记录任务
     */
    void updateProxyIpTask();

    /**
     * 更新数据库任务
     */
    void updateDbTask();

    /**
     * 更新数据库中ip的ping值
     */
    void updateIpPingValueInDb();

    /**
     * 从.csv文件获取proxyip
     *
     * @return ip列表
     */
    List<String> getProxyIpFromCsv();

    /**
     * proxyip筛选特殊处理
     *
     * @param proxyIp 代理ip
     * @return 布尔值
     */
    boolean ipFilterByCountryCode(ProxyIp proxyIp);
}
