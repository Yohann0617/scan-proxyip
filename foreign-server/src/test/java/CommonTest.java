import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select
 * @className: CommonTest
 * @author: Yohann
 * @date: 2024/3/28 20:54
 */
public class CommonTest {
    private static final Log log = LogFactory.get();

    private static final String GET_IP_LOCATION_API1 = "https://api.iplocation.net/?cmd=ip-country&ip=%s";
    private static final String GET_IP_LOCATION_API2 = "https://ipapi.co/%s/json";
    private static final String GET_GEO_IP_LOCATION_API = "https://geolite.info/geoip/v2.1/country/%s";

    /**
     * 获取IP位置（API 1）
     *
     * @param ip IP地址
     * @return API响应的结果
     */
    public static String getIpLocation1(String ip) {
        String url = String.format(GET_IP_LOCATION_API1, ip);

        HttpResponse response = HttpRequest.get(url)
                .execute();

        return response.body();
    }

    /**
     * 获取IP位置（API 2）
     *
     * @param ip IP地址
     * @return API响应的结果
     */
    public static String getIpLocation2(String ip) {
        String url = String.format(GET_IP_LOCATION_API2, ip);

        HttpResponse response = HttpRequest.get(url)
                .execute();

        return response.body();
    }

    /**
     * 获取GeoIP位置
     *
     * @param ip        IP地址
     * @param authToken 授权Token
     * @return API响应的结果
     */
    public static String getGeoIpLocation(String ip, String authToken) {
        String url = String.format(GET_GEO_IP_LOCATION_API, ip);

        HttpResponse response = HttpRequest.get(url)
                .header("Authorization", "Basic " + authToken)
                .execute();

        return response.body();
    }

    /**
     * 上传文件到网络磁盘
     *
     * @param filePath   文件路径
     * @param apiAddress API地址
     */
    public static void uploadFileToNetDisc(String filePath, String apiAddress) {
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


    public static void main(String[] args) {
        String ip = "8.8.8.8";
        String authToken = "xxx"; // 需要base64编码后的认证信息

//        String response1 = getIpLocation1(ip);
//        System.out.println("API 1 Response: " + response1);
//        // 将字符串解析为JSONObject
//        JSONObject jsonObject = JSONUtil.parseObj(response1);
//        // 获取country_code2
//        String countryCode2 = jsonObject.getStr("country_code2");
//        // 输出country_code2
//        System.out.println("Country Code2: " + countryCode2);

//        String response2 = getIpLocation2(ip);
//        System.out.println("API 2 Response: " + response2);

//        String response3 = getGeoIpLocation(ip, authToken);
//        System.out.println("GeoIP API Response: " + response3);
//
//        // 将字符串解析为JSONObject
//        JSONObject jsonObject = JSONUtil.parseObj(response3);
//
//        // 获取country对象中的iso_code
//        String isoCode = jsonObject.getByPath("country.iso_code", String.class);
//        System.out.println(isoCode);


        String filePath = "path/file";
        String apiAddress = "https://a.b.c/netdisc/api";

        uploadFileToNetDisc(filePath, apiAddress);
    }
}
