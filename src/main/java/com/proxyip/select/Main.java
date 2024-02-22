package com.proxyip.select;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Main
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/2/22 15:41
 */
public class Main {

    public static void main(String[] args) {
        try {
            // Step 1: Execute nslookup command
            List<String> ipAddresses = executeNslookupCommand("cdn-all.xn--b6gac.eu.org", "208.67.222.222");

            ipAddresses.forEach(System.out::println);

            // Step 2: Fetch ISO codes using curl command and write to file
            fetchAndWriteISOCodes(ipAddresses, "output.txt");
            System.out.println("完成");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

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
            System.exit(1);
        }

        return ipAddresses;
    }

    private static void fetchAndWriteISOCodes(List<String> ipAddresses, String outputFile) throws IOException, InterruptedException {
        try (FileWriter writer = new FileWriter(outputFile)) {
            for (String ipAddress : ipAddresses) {
                // Step 3: Execute curl command
                String isoCode = executeCurlCommand(ipAddress);

                // Step 4: Write IP and ISO code to file
                writer.write(ipAddress + " " + isoCode + "\n");
            }
        }
    }

    private static String executeCurlCommand(String ipAddress) throws IOException, InterruptedException {
        String curlCommand = "curl https://geolite.info/geoip/v2.1/country/" + ipAddress +
                " -H \"Authorization: Basic OTc3MTUyOk9XZXlZRl9pR3BZYkszZkhmaEhnUmhrVFFNbVptVmYzcTU1b19tbWs=\"";
        ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", curlCommand);
        Process process = processBuilder.start();
        String country = "";

        // Read the output of the command
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("iso_code")) {
                    if (line.contains("HK")) {
                        country = "HK";
                        break;
                    } else if (line.contains("US")) {
                        country = "US";
                        break;
                    } else if (line.contains("NL")) {
                        country = "NL";
                        break;
                    } else if (line.contains("SG")) {
                        country = "SG";
                        break;
                    } else if (line.contains("KR")) {
                        country = "KR";
                        break;
                    } else if (line.contains("DE")) {
                        country = "DE";
                        break;
                    } else if (line.contains("UK")) {
                        country = "UK";
                        break;
                    }
                }
//                output.append(line);
            }
        }

        return country;
//
//        // Wait for the process to finish
//        int exitCode = process.waitFor();
//        if (exitCode != 0) {
//            System.out.println("Error executing curl command");
//            System.exit(1);
//        }
//
//        // Parse JSON response and extract iso_code
//        String jsonResponse = output.toString();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        // 将 JSON 字符串转为 GeoIPResponse 对象
//        GeoIPResponse geoIPResponse = objectMapper.readValue(jsonResponse, GeoIPResponse.class);
//
//        return geoIPResponse.getCountry().getIsoCode();
    }
}
