package com.tnong.boot.system.dict.domain.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 字典项实体
 */
@Data
public class SysDictItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long tenantId;
    private String typeCode;
    private String itemValue;
    private String itemLabel;
    private String cssClass;
    private String color;
    private Integer sort;
    private Integer status;
    private String remark;
    private Integer deleteFlag;
    private LocalDateTime createdTime;
    private Long createdUser;
    private LocalDateTime updatedTime;
    private Long updatedUser;
    private Integer version;
}
