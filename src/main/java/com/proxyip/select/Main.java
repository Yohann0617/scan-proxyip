package com.proxyip.select;

import com.proxyip.select.config.CloudflareCfg;
import com.proxyip.select.config.DnsCfg;
import com.proxyip.select.enums.EnumUtils;
import com.proxyip.select.enums.dict.CountryEnum;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
     * cf添加dns记录api
     */
    private final String cfAddDnsRecordsApi = "curl -X POST \"https://api.cloudflare.com/client/v4/zones/%s/dns_records\" \\\n" +
            "     -H \"Authorization: Bearer %s\" \\\n" +
            "     -H \"Content-Type: application/json\" \\\n" +
            "     --data '{\"type\":\"A\",\"name\":\"%s\",\"content\":\"%s\",\"proxied\":false}'";

    /**
     * cf删除所有dns记录api
     */
    private final String cfRemoveDnsRecordsApi = "curl -X GET \"https://api.cloudflare.com/client/v4/zones/%s/dns_records?type=A&name=%s\" \\\n" +
            "     -H \"Authorization: Bearer %s\" \\\n" +
            "     -H \"Content-Type: application/json\" | \\\n" +
            "jq -c '.result[] | .id' | \\\n" +
            "xargs -n 1 -I {} curl -X DELETE \"https://api.cloudflare.com/client/v4/zones/%s/dns_records/{}\" \\\n" +
            "     -H \"Authorization: Bearer %s\" \\\n" +
            "     -H \"Content-Type: application/json\"";

    private class MyTask implements Runnable {

        @Override
        public void run() {
            getProxyIpTask();
        }
    }

//    public static void main(String[] args) {
//        try {
//            // Step 1: Execute nslookup command
//            List<String> ipAddresses = executeNslookupCommand("cdn-all.xn--b6gac.eu.org", "208.67.222.222");
//
//            ipAddresses.forEach(System.out::println);
//
//            // Step 2: Fetch ISO codes using curl command and write to file
//            fetchAndWriteISOCodes(ipAddresses, "output.txt");
//            System.out.println("完成");
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
//    }

    private static List<String> executeNslookupCommand(String domain, String dnsServer) throws IOException, InterruptedException {
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

    private void fetchAndWriteISOCodes(List<String> ipAddresses, String outputFile) throws IOException, InterruptedException {
        try (FileWriter writer = new FileWriter(outputFile)) {
            for (String ipAddress : ipAddresses) {
                // Step 3: Execute curl command
                String isoCode = executeCurlCommand(ipAddress);

                // 添加cf记录
                CountryEnum enumByCode = EnumUtils.getEnumByCode(CountryEnum.class, isoCode);
                if (enumByCode != null) {
                    String domainPrefix;
                    switch (enumByCode) {
                        case HK:
                            domainPrefix = dnsCfg.getHkDomainPrefix();
                            break;
                        case SG:
                            domainPrefix = dnsCfg.getSgDomainPrefix();
                            break;
                        case KR:
                            domainPrefix = dnsCfg.getKrDomainPrefix();
                            break;
                        case JP:
                            domainPrefix = dnsCfg.getJpDomainPrefix();
                            break;
                        case US:
                            domainPrefix = dnsCfg.getUsDomainPrefix();
                            break;
                        case UK:
                            domainPrefix = dnsCfg.getUkDomainPrefix();
                            break;
                        case NL:
                            domainPrefix = dnsCfg.getNlDomainPrefix();
                            break;
                        case DE:
                            domainPrefix = dnsCfg.getDeDomainPrefix();
                            break;
                        default:
                            domainPrefix = dnsCfg.getHkDomainPrefix();
                    }
                    addCfDnsRecords(domainPrefix, ipAddress);
                }

                // Step 4: Write IP and ISO code to file
                writer.write(ipAddress + " " + isoCode + "\n");
            }
        }
    }

    private String executeCurlCommand(String ipAddress) throws IOException, InterruptedException {
        String curlCommand = "curl https://geolite.info/geoip/v2.1/country/" + ipAddress +
                " -H \"Authorization: Basic " + dnsCfg.getGeoipAuth() + "\"";
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", curlCommand);
        Process process = processBuilder.start();
        String country = "";

        // Read the output of the command
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("iso_code")) {
                    if (line.contains(CountryEnum.HK.getCode())) {
                        country = CountryEnum.HK.getCode();
                        break;
                    } else if (line.contains(CountryEnum.US.getCode())) {
                        country = CountryEnum.US.getCode();
                        break;
                    } else if (line.contains(CountryEnum.NL.getCode())) {
                        country = CountryEnum.NL.getCode();
                        break;
                    } else if (line.contains(CountryEnum.SG.getCode())) {
                        country = CountryEnum.SG.getCode();
                        break;
                    } else if (line.contains(CountryEnum.KR.getCode())) {
                        country = CountryEnum.KR.getCode();
                        break;
                    } else if (line.contains(CountryEnum.DE.getCode())) {
                        country = CountryEnum.DE.getCode();
                        break;
                    } else if (line.contains(CountryEnum.UK.getCode())) {
                        country = CountryEnum.UK.getCode();
                        break;
                    } else if (line.contains(CountryEnum.JP.getCode())) {
                        country = CountryEnum.JP.getCode();
                        break;
                    }
                }
            }
        }

        return country;
    }

    private void getProxyIpTask() {
        try {
            // Step 1: Execute nslookup command
            List<String> ipAddresses = executeNslookupCommand(dnsCfg.getProxyDomain(), dnsCfg.getDnsServer());

//            ipAddresses.forEach(System.out::println);

            // 清除dns旧记录
            removeCfDnsRecords();

            // Step 2: Fetch ISO codes using curl command and write to file
            fetchAndWriteISOCodes(ipAddresses, dnsCfg.getOutPutFile());
            System.out.println("√√√获取proxyIps任务完成，文件位置：" + dnsCfg.getOutPutFile() + "√√√");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addCfDnsRecords(String domainPrefix, String ipAddress) {
        String curlCommand = String.format(cfAddDnsRecordsApi, cloudflareCfg.getZoneId(), cloudflareCfg.getApiToken(), domainPrefix, ipAddress);
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", curlCommand);
        try {
            Process process = processBuilder.start();
            // Wait for the process to finish
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("Error executing addCfDnsRecords task");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("域名：" + domainPrefix + "." + dnsCfg.getRootDomain() + " 的dns记录添加成功！");
    }

    private void removeCfDnsRecords() {
        List<String> proxyDomainList = Arrays.asList(
                dnsCfg.getHkDomainPrefix() + "." + dnsCfg.getRootDomain(),
                dnsCfg.getSgDomainPrefix() + "." + dnsCfg.getRootDomain(),
                dnsCfg.getKrDomainPrefix() + "." + dnsCfg.getRootDomain(),
                dnsCfg.getJpDomainPrefix() + "." + dnsCfg.getRootDomain(),
                dnsCfg.getUsDomainPrefix() + "." + dnsCfg.getRootDomain(),
                dnsCfg.getUkDomainPrefix() + "." + dnsCfg.getRootDomain(),
                dnsCfg.getNlDomainPrefix() + "." + dnsCfg.getRootDomain(),
                dnsCfg.getDeDomainPrefix() + "." + dnsCfg.getRootDomain()
        );
        proxyDomainList.forEach(proxyDomain -> {
            String curlCommand = String.format(cfRemoveDnsRecordsApi, cloudflareCfg.getZoneId(), proxyDomain, cloudflareCfg.getApiToken(), cloudflareCfg.getZoneId(), cloudflareCfg.getApiToken());
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", curlCommand);
            try {
                Process process = processBuilder.start();
                // Wait for the process to finish
                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    System.out.println("Error executing addCfDnsRecords task");
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("域名：" + proxyDomain + "的dns记录已清除！");
        });

    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        System.out.println(dnsCfg.getDnsServer());
//        System.out.println(dnsCfg.getProxyDomain());
//        System.out.println(dnsCfg.getGeoipAuth());
//        System.out.println(cloudflareCfg.getZoneId());
//        System.out.println(cloudflareCfg.getApiToken());

        // 服务启动立马执行一次
        getProxyIpTask();

        // 每天凌晨两点执行
        taskScheduler.schedule(new MyTask(), new CronTrigger("0 0 2 * * ?"));
    }
}
