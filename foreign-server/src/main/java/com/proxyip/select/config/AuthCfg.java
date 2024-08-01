package com.proxyip.select.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.config
 * @className: AuthCfg
 * @author: Yohann
 * @date: 2024/8/1 21:58
 */
@Configuration
@ConfigurationProperties(prefix = "auth-cfg")
@Data
public class AuthCfg {

    private String username;
    private String password;
    private String tokenSecretKey;
}
