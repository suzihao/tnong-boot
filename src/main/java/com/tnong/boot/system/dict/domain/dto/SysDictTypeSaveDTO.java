package com.tnong.boot.system.dict.domain.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SysDictTypeSaveDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String typeCode;
    private String name;
    private Integer status;
    private String remark;
    private Integer version;
}
