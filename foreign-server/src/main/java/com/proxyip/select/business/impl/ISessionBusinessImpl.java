package com.proxyip.select.business.impl;

import cn.hutool.jwt.JWTUtil;
import com.proxyip.select.bean.params.LoginParams;
import com.proxyip.select.business.ISessionBusiness;
import com.proxyip.select.common.exception.BusinessException;
import com.proxyip.select.config.AuthCfg;
import com.proxyip.select.utils.ExpiringCache;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.business.impl
 * @className: ISessionBusinessImpl
 * @author: Yohann
 * @date: 2024/8/1 21:27
 */
@Service
public class ISessionBusinessImpl implements ISessionBusiness {

    @Resource
    private ExpiringCache<String, String> expiringCache;
    @Resource
    private AuthCfg authCfg;

    @Override
    public String login(LoginParams params) {
        if (!params.getUsername().equals(authCfg.getUsername()) || !params.getPassword().equals(authCfg.getPassword())) {
            throw new BusinessException(-1, "用户名或密码错误");
        }
        Map<String, Object> payload = new HashMap<>(1);
        payload.put("username", params.getUsername());
        String token = JWTUtil.createToken(payload, authCfg.getTokenSecretKey().getBytes());
        expiringCache.put("token", token, 10, TimeUnit.MINUTES);
        return token;
    }
}
