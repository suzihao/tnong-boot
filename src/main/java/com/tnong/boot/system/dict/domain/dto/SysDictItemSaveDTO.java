package com.tnong.boot.system.dict.domain.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SysDictItemSaveDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String typeCode;
    private String itemValue;
    private String itemLabel;
    private String cssClass;
    private String color;
    private Integer sort;
    private Integer status;
    private String remark;
    private Integer version;
}
