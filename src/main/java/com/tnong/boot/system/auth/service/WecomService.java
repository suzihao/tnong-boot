package com.tnong.boot.system.auth.service;

/**
 * 企业微信服务接口
 */
public interface WecomService {

    /**
     * 获取企业微信 access_token（带缓存）
     *
     * @return access_token
     */
    String getAccessToken();
}
