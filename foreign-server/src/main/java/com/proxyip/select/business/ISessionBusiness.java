package com.proxyip.select.business;

import com.proxyip.select.bean.params.LoginParams;

/**
 * @author Yohann
 */
public interface ISessionBusiness {

    String login(LoginParams params);
}
