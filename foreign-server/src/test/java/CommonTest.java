import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select
 * @className: CommonTest
 * @author: Yohann
 * @date: 2024/3/28 20:54
 */
public class CommonTest {
    public static void main(String[] args) {
        String jsonStr = getIpInfo("141.147.101.8", "");
        System.out.println(jsonStr);
        JSONObject obj = JSONUtil.parseObj(jsonStr);
        System.out.println(obj.get("country_code", String.class) == null ? "kong" : obj.get("country_code", String.class));

        System.out.println(jsonStr.substring(jsonStr.indexOf("country_code") + 15, jsonStr.indexOf("country_code") + 17));
    }

    private static String GET_IP_LOCATION_API2 = "curl -H \"User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36\" " +
            "-H \"Accept: application/json\" \"https://api.ip.sb/geoip/%s\"";
    private static String GET_IP_LOCATION_API1 = "curl \"https://api.iplocation.net/?cmd=ip-country&ip=%s\"";

    public static String getIpInfo(String ipAddress, String geoIpAuth) {
        StringBuilder sb = new StringBuilder();

        String curlCommand = String.format(GET_IP_LOCATION_API2, ipAddress);

        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", curlCommand);
        try {
            Process process = processBuilder.start();
            // Read the output of the command
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
