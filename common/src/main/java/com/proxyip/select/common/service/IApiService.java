package com.proxyip.select.common.service;

import java.util.List;

/**
 * <p>
 * IApiService
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/3/29 13:57
 */
public interface IApiService {

    /**
     * 发送本地文件到个人网盘
     *
     * @param filePath   本地文件全路径
     * @param apiAddress 网盘api地址
     */
    void uploadFileToNetDisc(String filePath, String apiAddress);

    /**
     * 解析域名
     *
     * @param domainList 域名列表
     * @param dnsServer  域名解析服务器地址
     * @return 解析后的ip地址
     */
    List<String> resolveDomain(List<String> domainList, String dnsServer);

    /**
     * 获取ip地址归属国家
     *
     * @param ipAddress ip地址
     * @return 国家代码（例如：香港-HK）
     */
    String getIpCountry(String ipAddress);

    /**
     * 获取ip地址归属国家（Geoip2）（更精准）（有限额：一天1000次查询）
     *
     * @param ipAddress ip地址
     * @param geoIpAuth geoIpAuth
     * @return 国家代码（例如：香港-HK）
     */
    String getIpCountry(String ipAddress, String geoIpAuth);

    /**
     * 添加cf域名dns记录
     *
     * @param domainPrefix 域名前缀
     * @param ipAddress    ip地址
     * @param zoneId       zoneId
     * @param apiToken     apiToken
     */
    void addCfDnsRecords(String domainPrefix, String ipAddress, String zoneId, String apiToken);

    /**
     * 清除cf域名dns记录
     *
     * @param proxyDomainList 域名列表
     * @param zoneId          zoneId
     * @param apiToken        apiToken
     */
    void removeCfDnsRecords(List<String> proxyDomainList, String zoneId, String apiToken);

    /**
     * 清除cf指定代理域名的某个ip地址的dns记录
     *
     * @param proxyDomain 代理域名
     * @param ip          ip地址
     * @param zoneId      zoneId
     * @param apiToken    apiToken
     */
    void removeCfSingleDnsRecords(String proxyDomain, String ip, String zoneId, String apiToken);

    /**
     * 获取ip信息（Geoip2）（更精准）（有限额：一天1000次查询）
     *
     * @param ipAddress ip地址
     * @param geoIpAuth geoIpAuth
     * @return 国家代码（例如：香港-HK）
     */
    String getIpInfo(String ipAddress, String geoIpAuth);

    /**
     * 从压缩包获取proxyip
     *
     * @param zipUrl 压缩包下载url
     * @param ports  端口列表
     * @return proxyip列表
     */
    List<String> getProxyIpFromZip(String zipUrl, List<Integer> ports);
}
