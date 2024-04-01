package com.proxyip.select.bean.params;

import lombok.Data;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.bean.params
 * @className: RmSingleDnsRecordParams
 * @author: Yohann
 * @date: 2024/3/30 17:59
 */
@Data
public class RmSingleIpDnsRecordParams {

    private String proxyDomain;
    private String ip;
}
