package com.proxyip.select.business;

import java.util.List;

/**
 * <p>
 * IDnsRecordService
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/3/21 14:26
 */
public interface IDnsRecordService {

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
}
