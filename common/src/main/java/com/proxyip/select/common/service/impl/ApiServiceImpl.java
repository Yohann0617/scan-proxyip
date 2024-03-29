package com.proxyip.select.common.service.impl;

import com.proxyip.select.common.service.IApiService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * ApiServiceImpl
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/3/29 13:57
 */
@Service
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
                        System.out.println("√√√ 文件：" + filePath + " 已发送至个人网盘 √√√");
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
                    System.out.println("Error executing nslookup command");
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
        } catch (Exception e) {
            e.printStackTrace();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("√√√ 域名：" + proxyDomain + "的dns记录已清除！ √√√");
        });
    }
}
