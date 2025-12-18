package com.tnong.boot.system.auth.service;

import com.tnong.boot.system.auth.domain.dto.LoginDTO;
import com.tnong.boot.system.auth.domain.vo.LoginVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证服务接口
 */
public interface AuthService {
    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO, HttpServletRequest request);

    /**
     * 企业微信登录回调
     *
     * @param code  企业微信回传的临时授权码
     * @param state 防重放state
     */
    LoginVO wecomLogin(String code, String state, HttpServletRequest request);

    /**
     * 用户退出
     */
    void logout();
}
