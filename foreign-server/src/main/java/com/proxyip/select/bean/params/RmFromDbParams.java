package com.proxyip.select.bean.params;

import lombok.Data;

import java.util.List;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.bean.params
 * @className: RmFromDbParams
 * @author: Yohann
 * @date: 2024/3/30 18:01
 */
@Data
public class RmFromDbParams {

    List<String> ids;
}
