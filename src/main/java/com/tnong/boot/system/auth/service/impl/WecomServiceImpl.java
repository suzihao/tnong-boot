package com.tnong.boot.system.auth.service.impl;

import com.tnong.boot.common.exception.BusinessException;
import com.tnong.boot.common.util.HttpRequestUtil;
import com.tnong.boot.system.auth.service.WecomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * 企业微信服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WecomServiceImpl implements WecomService {

    private final HttpClient client;

    @Value("${wecom.corp-id:}")
    private String wecomCorpId;

    @Value("${wecom.secret:}")
    private String wecomSecret;

    /**
     * 获取企业微信 access_token（带缓存）
     * 缓存key: wecomAccessToken
     * 缓存时间: 7000秒（在 CacheConfig 中配置）
     */
    @Override
    @Cacheable(value = "wecomAccessToken", key = "'token'")
    public String getAccessToken() {
        log.info("从企业微信API获取access_token（未命中缓存）");

        // 检查配置是否完整
        if (!StringUtils.hasText(wecomCorpId) || !StringUtils.hasText(wecomSecret)) {
            throw new BusinessException("企业微信配置未完成，请先在配置文件中设置 wecom.corp-id / wecom.secret");
        }

        // 获取 access_token
        String tokenUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid="
                + wecomCorpId + "&corpsecret=" + wecomSecret;

        try {
            HttpRequest tokenRequest = HttpRequestUtil.getHttpRequest(tokenUrl);
            HttpResponse<String> tokenResponse = client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());
            String tokenBody = tokenResponse.body();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode tokenNode = mapper.readTree(tokenBody);
            int errcode = tokenNode.path("errcode").asInt(0);
            if (errcode != 0) {
                String errmsg = tokenNode.path("errmsg").stringValue(null);
                throw new BusinessException("获取企业微信access_token失败:" + errmsg);
            }

            String accessToken = tokenNode.path("access_token").asText(null);
            if (!StringUtils.hasText(accessToken)) {
                throw new BusinessException("企业微信返回的access_token为空");
            }

            log.info("成功获取企业微信access_token，已缓存7000秒");
            return accessToken;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("调用企业微信接口异常:" + e.getMessage());
        }
    }
}
