package com.tnong.boot.system.user.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户第三方账号绑定实体
 */
@Data
public class SysUserThirdAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 本地用户ID，对应sys_user.id
     */
    private Long userId;

    /**
     * 账号类型：wecom(企业微信) / beisen(北森) / dingtalk(钉钉) 等
     */
    private String type;

    /**
     * 第三方用户唯一标识，如企业微信userId
     */
    private String thirdUserId;

    /**
     * 第三方unionId（如有）
     */
    private String thirdUnionId;

    /**
     * 冗余一些第三方信息，JSON格式，如邮箱/工号/部门快照
     */
    private String extraInfo;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 创建人ID
     */
    private Long createdUser;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 更新人ID
     */
    private Long updatedUser;

    /**
     * 乐观锁版本号
     */
    private Integer version;

    /**
     * 删除标记：0未删除，1已删除
     */
    private Integer deleteFlag;
}
