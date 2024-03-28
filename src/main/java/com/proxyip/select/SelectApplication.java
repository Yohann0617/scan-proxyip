package com.proxyip.select;

import com.proxyip.select.utils.IdGen;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
@MapperScan("com.proxyip.select.mapper")
public class SelectApplication {

    public static void main(String[] args) {
        SpringApplication.run(SelectApplication.class, args);
    }

    @Bean
    public IdGen idGen(@Value("${snow.id.worker-id}") long workerId, @Value("${snow.id.datacenter-id}") long datacenterId) {
        return new IdGen(workerId, datacenterId);
    }
}
