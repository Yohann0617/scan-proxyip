package com.proxyip.select.common.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 代理ip表
 *
 * @author Yohann
 */
@Data
public class ProxyIp implements Serializable {
    private String id;

    private String country;

    private String ip;

    private Integer pingValue;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;
}