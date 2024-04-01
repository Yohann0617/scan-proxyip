package com.proxyip.select.bean.params;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * AddProxyIpToDbParams
 * </p >
 *
 * @author yuhui.fan
 * @since 2024/4/1 16:51
 */
@Data
public class AddProxyIpToDbParams {

    List<String> ipList;
}
