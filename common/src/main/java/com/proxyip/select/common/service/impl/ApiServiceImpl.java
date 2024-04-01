package com.proxyip.select.common.service.impl;

import com.proxyip.select.common.exception.BusinessException;
import com.proxyip.select.common.service.IApiService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * ApiServiceImpl
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/3/29 13:57
 */
@Service
@Slf4j
public class ApiServiceImpl implements IApiService {

    /**
     * cf添加dns记录api
     */
    private static final String CF_ADD_DNS_RECORDS_API = "curl -X POST \"https://api.cloudflare.com/client/v4/zones/%s/dns_records\"" +
            "     -H \"Authorization: Bearer %s\" " +
            "     -H \"Content-Type: application/json\" " +
            "     --data '{\"type\":\"A\",\"name\":\"%s\",\"content\":\"%s\",\"proxied\":false}'";

    /**
     * cf删除所有dns记录api
     */
    private static String CF_REMOVE_DNS_RECORDS_API = "curl -X GET \"https://api.cloudflare.com/client/v4/zones/%s/dns_records?type=A&name=%s\" " +
            "     -H \"Authorization: Bearer %s\" " +
            "     -H \"Content-Type: application/json\" | " +
            "jq -c '.result[] | .id' | " +
            "xargs -n 1 -I {} curl -X DELETE \"https://api.cloudflare.com/client/v4/zones/%s/dns_records/{}\" " +
            "     -H \"Authorization: Bearer %s\" " +
            "     -H \"Content-Type: application/json\"";

    /**
     * cf删除指定代理域名的某个ip地址的dns记录api
     */
    private static String CF_REMOVE_SINGLE_DNS_RECORDS_API = "curl -X GET \"https://api.cloudflare.com/client/v4/zones/%s/dns_records?type=A&name=%s\" " +
            "-H \"Authorization: Bearer %s\" " +
            "-H \"Content-Type: application/json\" | jq -c '.result[] | select(.content == \"%s\") | .id' | xargs -n 1 -I {} curl -X DELETE \"https://api.cloudflare.com/client/v4/zones/%s/dns_records/{}\" " +
            "-H \"Authorization: Bearer %s\" " +
            "-H \"Content-Type: application/json\"";

    /**
     * 获取ip归属国家api
     */
    private static String GET_IP_LOCATION_API = "curl \"https://api.iplocation.net/?cmd=ip-country&ip=%s\"";
    private static String GET_GEO_IP_LOCATION_API = "curl \"https://geolite.info/geoip/v2.1/country/%s\"" +
            " -H \"Authorization: Basic %s\"";

    /**
     * 个人网盘api
     */
    private static String NET_DISC_API = "curl -X POST -F \"image=@%s;type=application/octet-stream\" %s";

    @Override
    public void uploadFileToNetDisc(String filePath, String apiAddress) {
        String curlCommand = String.format(NET_DISC_API, filePath, apiAddress);
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", curlCommand);
        try {
            Process process = processBuilder.start();
            // Read the output of the command
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("\"code\":1")) {
                        log.info("√√√ 文件：{} 已发送至个人网盘 √√√", filePath);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<String> resolveDomain(List<String> domainList, String dnsServer) {
        Set<String> ipAddresses = new HashSet<>();
        domainList.stream().parallel().forEach(domain -> {
            ProcessBuilder processBuilder = new ProcessBuilder("nslookup", domain, dnsServer);

            try {
                Process process = processBuilder.start();
                // Read the output of the command
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Address:") && !line.contains("#")) {
                            String ipAddress = line.substring(line.indexOf(":") + 1).trim();
                            ipAddresses.add(ipAddress);
                        }
                    }
                }

                // Wait for the process to finish
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    log.error("Error executing nslookup command");
//                    System.exit(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return new ArrayList<>(ipAddresses);
    }

    @Override
    public String getIpCountry(String ipAddress) {
        String curlCommand = String.format(GET_IP_LOCATION_API, ipAddress);
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", curlCommand);
        String country = null;
        try {
            Process process = processBuilder.start();
            // Read the output of the command
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("country_code2")) {
                        int index = line.indexOf("country_code2");
                        country = line.substring(index + 16, index + 18).trim();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return country;
    }

    @Override
    public String getIpCountry(String ipAddress, String geoIpAuth) {
        String country = null;

        // 不使用GeoIP2
        if ("".equals(geoIpAuth)) {
            return getIpCountry(ipAddress);
        }

        String curlCommand = String.format(GET_GEO_IP_LOCATION_API, ipAddress, geoIpAuth);
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", curlCommand);
        try {
            Process process = processBuilder.start();
            // Read the output of the command
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("\"iso_code\"")) {
                        int index = line.indexOf("\"iso_code\"");
                        country = line.substring(index + 12, index + 14);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return country;
    }

    @Override
    public void addCfDnsRecords(String domainPrefix, String ipAddress, String zoneId, String apiToken) {
        String curlCommand = String.format(CF_ADD_DNS_RECORDS_API, zoneId, apiToken, domainPrefix, ipAddress);
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", curlCommand);
        try {
            Process process = processBuilder.start();

//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//            String line;
//            log.info("add ------------------" + domainPrefix + "-------------------");
//            while ((line = reader.readLine()) != null) {
//                log.info(line);
//            }
//            log.info("add ------------------" + domainPrefix + "-------------------");
//        }

            // Wait for the process to finish
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Error executing addCfDnsRecords task");
            }
//        log.info("域名：" + domainPrefix + "." + dnsCfg.getRootDomain() + " 的dns记录添加成功！ip地址：" + ipAddress);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(-1, "添加cf记录失败：" + e.getLocalizedMessage());
        }
    }

    @Override
    public void removeCfDnsRecords(List<String> proxyDomainList, String zoneId, String apiToken) {
        proxyDomainList.parallelStream().forEach(proxyDomain -> {
            String curlCommand = String.format(CF_REMOVE_DNS_RECORDS_API, zoneId, proxyDomain, apiToken, zoneId, apiToken);
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", curlCommand);
            try {
                Process process = processBuilder.start();
                // Wait for the process to finish
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    log.error("Error executing removeCfDnsRecords task");
                }
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                    String line;
//                    log.info("rm ------------------" + proxyDomain + "-------------------");
//                    while ((line = reader.readLine()) != null) {
//                        log.info(line);
//                    }
//                    log.info("rm ------------------" + proxyDomain + "-------------------");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                throw new BusinessException(-1, "清除DNS记录失败：" + e.getLocalizedMessage());
            }
            log.info("√√√ 域名：{} 的dns记录已清除！ √√√", proxyDomain);
        });
    }

    @Override
    public void removeCfSingleDnsRecords(String proxyDomain, String ip, String zoneId, String apiToken) {
        String curlCommand = String.format(CF_REMOVE_SINGLE_DNS_RECORDS_API, zoneId, proxyDomain, apiToken, ip, zoneId, apiToken);
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", curlCommand);
        try {
            Process process = processBuilder.start();
            // Wait for the process to finish
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("Error executing removeCfSingleDnsRecords task");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new BusinessException(-1, "清除DNS记录失败：" + e.getLocalizedMessage());
        }
        log.info("√√√ 域名：{} 的A记录ip：{} 的dns记录已清除！ √√√", proxyDomain, ip);
    }
}
