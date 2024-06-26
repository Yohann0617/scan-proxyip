package com.proxyip.select.bean.params;

import lombok.Data;

import java.util.List;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.bean.params
 * @className: AddDnsRecordsBatchParams
 * @author: Yohann
 * @date: 2024/3/30 20:47
 */
@Data
public class AddDnsRecordsBatchParams {

    List<String> ids;
}
