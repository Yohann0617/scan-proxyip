package com.proxyip.select.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @projectName: select-proxyip
 * @package: com.proxyip.select.utils
 * @className: DnsUtils
 * @author: Yohann
 * @date: 2024/2/23 23:51
 */
public class DnsUtils {

    /**
     * cf添加dns记录api
     */
    public static final String CF_ADD_DNS_RECORDS_API = "curl -X POST \"https://api.cloudflare.com/client/v4/zones/%s/dns_records\"" +
            "     -H \"Authorization: Bearer %s\" " +
            "     -H \"Content-Type: application/json\" " +
            "     --data '{\"type\":\"A\",\"name\":\"%s\",\"content\":\"%s\",\"proxied\":false}'";

    /**
     * cf删除所有dns记录api
     */
    public static String CF_REMOVE_DNS_RECORDS_API = "curl -X GET \"https://api.cloudflare.com/client/v4/zones/%s/dns_records?type=A&name=%s\" " +
            "     -H \"Authorization: Bearer %s\" " +
            "     -H \"Content-Type: application/json\" | " +
            "jq -c '.result[] | .id' | " +
            "xargs -n 1 -I {} curl -X DELETE \"https://api.cloudflare.com/client/v4/zones/%s/dns_records/{}\" " +
            "     -H \"Authorization: Bearer %s\" " +
            "     -H \"Content-Type: application/json\"";

    /**
     * 获取ip归属国家api
     */
    public static String GET_IP_LOCATION_API = "curl 'https://api.iplocation.net/?cmd=ip-country&ip=%s'";
    public static String GET_GEO_IP_LOCATION_API = "curl https://geolite.info/geoip/v2.1/country/%s" +
            " -H \"Authorization: Basic %s\"";

    /**
     * 解析域名
     *
     * @param domain    域名
     * @param dnsServer 域名解析服务器地址
     * @return 解析后的ip地址
     * @throws IOException          异常
     * @throws InterruptedException 异常
     */
    public static List<String> resolveDomain(String domain, String dnsServer) throws IOException, InterruptedException {
        List<String> ipAddresses = new ArrayList<>();
        ProcessBuilder processBuilder = new ProcessBuilder("nslookup", domain, dnsServer);
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
            System.out.println("Error executing nslookup command");
//            System.exit(1);
        }

        return ipAddresses;
    }

    /**
     * 获取ip地址归属国家
     *
     * @param ipAddress ip地址
     * @return 国家代码（例如：香港-HK）
     */
    public static String getIpCountry(String ipAddress) {
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

    /**
     * 获取ip地址归属国家（Geoip2）（更精准）（有限额：一天1000次查询）
     *
     * @param ipAddress ip地址
     * @param geoIpAuth geoIpAuth
     * @return 国家代码（例如：香港-HK）
     */
    public static String getIpCountry(String ipAddress, String geoIpAuth) {
        String curlCommand = String.format(GET_GEO_IP_LOCATION_API, ipAddress, geoIpAuth);
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", curlCommand);
        String country = null;
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

    /**
     * 添加cf域名dns记录
     *
     * @param domainPrefix 域名前缀
     * @param ipAddress    ip地址
     * @param zoneId       zoneId
     * @param apiToken     apiToken
     * @throws IOException          异常
     * @throws InterruptedException 异常
     */
    public static void addCfDnsRecords(String domainPrefix, String ipAddress, String zoneId, String apiToken)
            throws IOException, InterruptedException {
        String curlCommand = String.format(CF_ADD_DNS_RECORDS_API, zoneId, apiToken, domainPrefix, ipAddress);
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", curlCommand);
        Process process = processBuilder.start();

//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//            String line;
//            System.out.println("add ------------------" + domainPrefix + "-------------------");
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
//            System.out.println("add ------------------" + domainPrefix + "-------------------");
//        }

        // Wait for the process to finish
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            System.out.println("Error executing addCfDnsRecords task");
        }
//        System.out.println("域名：" + domainPrefix + "." + dnsCfg.getRootDomain() + " 的dns记录添加成功！ip地址：" + ipAddress);
    }

    /**
     * 清除cf域名dns记录
     *
     * @param proxyDomainList 域名列表
     * @param zoneId          zoneId
     * @param apiToken        apiToken
     */
    public static void removeCfDnsRecords(List<String> proxyDomainList, String zoneId, String apiToken) {
        proxyDomainList.parallelStream().forEach(proxyDomain -> {
            String curlCommand = String.format(CF_REMOVE_DNS_RECORDS_API, zoneId, proxyDomain, apiToken, zoneId, apiToken);
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", curlCommand);
            try {
                Process process = processBuilder.start();
                // Wait for the process to finish
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.out.println("Error executing addCfDnsRecords task");
                }
//                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
//                    String line;
//                    System.out.println("rm ------------------" + proxyDomain + "-------------------");
//                    while ((line = reader.readLine()) != null) {
//                        System.out.println(line);
//                    }
//                    System.out.println("rm ------------------" + proxyDomain + "-------------------");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("√√√ 域名：" + proxyDomain + "的dns记录已清除！ √√√");
        });

    }
}
