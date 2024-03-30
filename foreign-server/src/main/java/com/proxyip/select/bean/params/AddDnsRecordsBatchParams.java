package com.proxyip.select.bean.params;

import java.util.List;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.bean.params
 * @className: AddDnsRecordsBatchParams
 * @author: Yohann
 * @date: 2024/3/30 20:47
 */
public class AddDnsRecordsBatchParams {

    List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
