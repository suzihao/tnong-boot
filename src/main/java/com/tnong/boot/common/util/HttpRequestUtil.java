package com.tnong.boot.common.util;

import java.net.URI;
import java.net.http.HttpRequest;

/**
 * @Author: suhe
 * @Date: 2025/12/29 15:08
 */
public class HttpRequestUtil {

    /**
     * 构建GET请求
     * @param detailUrl
     * @return {@link HttpRequest }
     */
    public static HttpRequest getHttpRequest(String detailUrl) {
        return HttpRequest.newBuilder()
                .uri(URI.create(detailUrl))
                .GET()
                .build();
    }
}
