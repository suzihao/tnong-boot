package com.tnong.boot.system.dict.domain.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class SysDictItemVO implements Serializable {
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
    private LocalDateTime createdTime;
    private Long createdUser;
    private LocalDateTime updatedTime;
    private Long updatedUser;
    private Integer version;
}
