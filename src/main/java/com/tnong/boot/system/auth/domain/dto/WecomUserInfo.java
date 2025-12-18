package com.tnong.boot.system.auth.domain.dto;

import lombok.Data;

/**
 * 企业微信用户信息（从企业微信接口获取后在系统内部使用）
 */
@Data
public class WecomUserInfo {

    /**
     * 企业微信内部用户ID
     */
    private String userId;

    /**
     * 企业微信 unionId（如已开通）
     */
    private String unionId;

    /**
     * 员工工号ID（需要企业微信侧同步）
     */
    private String employeeId;

    /**
     * 职位
     */
    private String position;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;
}
