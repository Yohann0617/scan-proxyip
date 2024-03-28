package com.proxyip.select.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 * @projectName: select-proxyip
 * @package: com.proxyip.select.utils
 * @className: NetUtils
 * @author: Yohann
 * @date: 2024/2/24 13:26
 */
public class NetUtils {

    public static boolean getPingResult(String ipAddress) {
        boolean reachable = false;
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            reachable = inetAddress.isReachable(500); // Timeout in milliseconds
//            if (reachable) {
//                System.out.println(ipAddress + " is reachable");
//            } else {
//                System.out.println(ipAddress + " is not reachable");
//            }
        } catch (IOException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
        return reachable;
    }

    public static Integer getPingValue(String ipAddress) {
        String pingValue = null;
        try {
            Process process = new ProcessBuilder("ping", "-c", "1", ipAddress).start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("time=")) {
                    int startIndex = line.indexOf("time=") + 5;
                    int endIndex = line.indexOf(" ms", startIndex);
                    pingValue = line.substring(startIndex, endIndex).trim();
//                    System.out.println("Ping value for " + ipAddress + ": " + pingValue + " ms");
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
        return pingValue == null || "".equals(pingValue) ? null : Double.valueOf(pingValue).intValue();
    }
}
