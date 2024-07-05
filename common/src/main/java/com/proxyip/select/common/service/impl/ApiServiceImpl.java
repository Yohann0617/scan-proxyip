package com.proxyip.select.common.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.proxyip.select.common.exception.BusinessException;
import com.proxyip.select.common.service.IApiService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

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
     * 获取ip归属国家api
     */
    private static final String GET_IP_LOCATION_API1 = "https://api.iplocation.net/?cmd=ip-country&ip=%s";
    private static final String GET_IP_LOCATION_API2 = "https://ipapi.co/%s/json";
    private static final String GET_GEO_IP_LOCATION_API = "https://geolite.info/geoip/v2.1/country/%s";

    @Override
    public void uploadFileToNetDisc(String filePath, String apiAddress) {
        File file = new File(filePath);

        if (!file.exists()) {
            log.error("文件 {} 不存在", filePath);
            return;
        }

        // 发送POST请求上传文件
        HttpResponse response = HttpRequest.post(apiAddress)
                .form("image", file)
                .execute();

        // 读取响应
        String responseBody = response.body();
        if (responseBody.contains("\"code\":1")) {
            log.info("√√√ 文件：{} 已发送至个人网盘 √√√", filePath);
        } else {
            log.error("文件：{} 发送失败，响应信息：{}", filePath, responseBody);
        }
    }

    @Override
    public List<String> resolveDomain(List<String> domainList, String dnsServer) {
        if (CollectionUtil.isEmpty(domainList)) {
            return new ArrayList<>();
        }

        Set<String> ipAddresses = new HashSet<>();
        domainList.parallelStream().forEach(domain -> {
            String command = String.format("nslookup %s %s", domain, dnsServer);
            try {
                List<String> results = RuntimeUtil.execForLines(command);

                for (String line : results) {
                    if (line.startsWith("Address:") && !line.contains("#")) {
                        String ipAddress = line.substring(line.indexOf(":") + 1).trim();
                        ipAddresses.add(ipAddress);
                    }
                }
            } catch (Exception e) {
                log.error("Error executing nslookup command", e);
            }
        });

        return new ArrayList<>(ipAddresses);
    }

    @Override
    public String getIpCountry(String ipAddress) {
        String url = String.format(GET_IP_LOCATION_API1, ipAddress);
        HttpResponse response = HttpRequest.get(url).execute();
        // 将字符串解析为JSONObject
        JSONObject jsonObject = JSONUtil.parseObj(response.body());
        // 获取country_code2
        return jsonObject.getStr("country_code2");
    }

    @Override
    public String getIpCountry(String ipAddress, String geoIpAuth) {
        // 不使用GeoIP2
        if ("".equals(geoIpAuth)) {
            return getIpCountry(ipAddress);
        }

        String url = String.format(GET_GEO_IP_LOCATION_API, ipAddress);

        HttpResponse response = HttpRequest.get(url)
                .header("Authorization", "Basic " + geoIpAuth)
                .execute();

        // 将字符串解析为JSONObject
        JSONObject jsonObject = JSONUtil.parseObj(response.body());

        // 获取country对象中的iso_code
        return jsonObject.getByPath("country.iso_code", String.class);
    }

    @Override
    public void addCfDnsRecords(String domainPrefix, String ipAddress, String zoneId, String apiToken) {
        String url = String.format("https://api.cloudflare.com/client/v4/zones/%s/dns_records", zoneId);

        // 构建请求体
        JSONObject data = new JSONObject();
        data.put("type", "A");
        data.put("name", domainPrefix);
        data.put("content", ipAddress);
        data.put("proxied", false);

        // 发送POST请求
        HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiToken)
                .header("Content-Type", "application/json")
                .body(data.toString())
                .execute();

        // 返回响应结果
//        log.info(response.body());
    }

    @Override
    public void removeCfDnsRecords(List<String> proxyDomainList, String zoneId, String apiToken) {
        proxyDomainList.parallelStream().forEach(proxyDomain -> {
            String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId + "/dns_records?type=A&name=" + proxyDomain;
            HttpRequest request = HttpRequest.get(url)
                    .header("Authorization", "Bearer " + apiToken)
                    .header("Content-Type", "application/json");
            HttpResponse response = request.execute();
            String responseBody = response.body();
            JSONArray recordsArray = JSONUtil.parseArray(JSONUtil.parseObj(responseBody).get("result"));
            for (int i = 0; i < recordsArray.size(); i++) {
                String id = JSONUtil.parseObj(recordsArray.get(i)).getStr("id");
                // 删除 DNS 记录
                String deleteUrl = "https://api.cloudflare.com/client/v4/zones/" + zoneId + "/dns_records/" + id;
                HttpRequest deleteRequest = HttpRequest.delete(deleteUrl)
                        .header("Authorization", "Bearer " + apiToken)
                        .header("Content-Type", "application/json");
                HttpResponse deleteResponse = deleteRequest.execute();
                // 等待删除命令执行完毕
                if (deleteResponse.getStatus() != 200) {
                    log.error("Error executing delete command");
                }
            }
            log.info("√√√ 域名：" + proxyDomain + " 的dns记录已清除！ √√√");
        });
    }

    @Override
    public void removeCfSingleDnsRecords(String proxyDomain, String ip, String zoneId, String apiToken) {
        String url = "https://api.cloudflare.com/client/v4/zones/" + zoneId + "/dns_records?type=A&name=" + proxyDomain;
        HttpRequest request = HttpRequest.get(url)
                .header("Authorization", "Bearer " + apiToken)
                .header("Content-Type", "application/json");
        HttpResponse response = request.execute();
        String responseBody = response.body();
        JSONArray recordsArray = JSONUtil.parseArray(JSONUtil.parseObj(responseBody).get("result"));
        for (int i = 0; i < recordsArray.size(); i++) {
            String address = JSONUtil.parseObj(recordsArray.get(i)).getStr("content");
            if (address.equals(ip)) {
                String id = JSONUtil.parseObj(recordsArray.get(i)).getStr("id");
                // 删除 DNS 记录
                String deleteUrl = "https://api.cloudflare.com/client/v4/zones/" + zoneId + "/dns_records/" + id;
                HttpRequest deleteRequest = HttpRequest.delete(deleteUrl)
                        .header("Authorization", "Bearer " + apiToken)
                        .header("Content-Type", "application/json");
                HttpResponse deleteResponse = deleteRequest.execute();
                // 等待删除命令执行完毕
                if (deleteResponse.getStatus() != 200) {
                    log.error("Error executing delete command");
                }
                break;
            }
        }
        log.info("√√√ 域名：{} 的A记录ip：{} 的dns记录已清除！ √√√", proxyDomain, ip);
    }

    @Override
    public String getIpInfo(String ipAddress, String geoIpAuth) {
        // 不使用GeoIP2
        if ("".equals(geoIpAuth)) {
            String url = String.format(GET_IP_LOCATION_API2, ipAddress);
            HttpResponse response = HttpRequest.get(url).execute();
            return response.body();
        } else {
            String url = String.format(GET_GEO_IP_LOCATION_API, ipAddress);
            HttpResponse response = HttpRequest.get(url)
                    .header("Authorization", "Basic " + geoIpAuth)
                    .execute();
            return response.body();
        }
    }

    @Override
    public List<String> getProxyIpFromZip(String zipUrl, List<Integer> ports) {
        if (StrUtil.isBlank(zipUrl)) {
            return new ArrayList<>();
        }

        String downloadPath = null;
        File unzip = null;
        try {
            // 设置下载路径
            downloadPath = System.getProperty("user.dir") + "/ip.zip";
            // 发送GET请求并下载文件
            HttpUtil.downloadFile(zipUrl, downloadPath);

            unzip = ZipUtil.unzip(downloadPath);
            return Arrays.stream(Objects.requireNonNull(unzip.listFiles())).parallel()
                    .filter(file -> ports.contains(Integer.valueOf(file.getName().split("\\.")[0].split("-")[2])))
                    .map(FileUtil::readUtf8Lines)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("获取URL{}压缩包文件内容失败", zipUrl);
        } finally {
            FileUtil.del(downloadPath);
            FileUtil.del(unzip);
        }
        return null;
    }
}
