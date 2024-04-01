package com.proxyip.select.bean.params;

import lombok.Data;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.bean.params
 * @className: ProxyIpPageParams
 * @author: Yohann
 * @date: 2024/3/30 14:37
 */
@Data
public class ProxyIpPageParams {

    private String keyword;
    private long currentPage;
    private long pageSize;
}
