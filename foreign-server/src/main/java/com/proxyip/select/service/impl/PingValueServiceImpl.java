package com.proxyip.select.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.proxyip.select.common.bean.ProxyIp;
import com.proxyip.select.common.service.IProxyIpService;
import com.proxyip.select.common.utils.CommonUtils;
import com.proxyip.select.service.IPingValueService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.service.impl
 * @className: PingValueServiceImpl
 * @author: Yohann
 * @date: 2024/3/29 20:12
 */
@Service
public class PingValueServiceImpl implements IPingValueService {

    @Resource
    private IProxyIpService proxyIpService;

    @Override
    public List<ProxyIp> getIpWithPingValueList() {
        return new ArrayList<>(Optional.ofNullable(proxyIpService.list(new LambdaQueryWrapper<ProxyIp>().isNotNull(ProxyIp::getPingValue)))
                .filter(CommonUtils::isNotEmpty).orElseGet(Collections::emptyList));
    }
}
