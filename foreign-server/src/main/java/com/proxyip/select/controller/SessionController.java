package com.proxyip.select.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.proxyip.select.bean.params.LoginParams;
import com.proxyip.select.bean.params.ProxyIpPageParams;
import com.proxyip.select.business.ISessionBusiness;
import com.proxyip.select.common.bean.ProxyIp;
import com.proxyip.select.common.bean.ResponseData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @projectName: scan-proxyip
 * @package: com.proxyip.select.controller
 * @className: SessionController
 * @author: Yohann
 * @date: 2024/8/1 21:44
 */
@RestController
@RequestMapping(path = "/api/session")
public class SessionController {

    @Resource
    private ISessionBusiness sessionBusiness;

    @PostMapping(path = "/login")
    public ResponseData<String> page(@RequestBody LoginParams params) {
        return ResponseData.successData(sessionBusiness.login(params), "登录成功");
    }
}
