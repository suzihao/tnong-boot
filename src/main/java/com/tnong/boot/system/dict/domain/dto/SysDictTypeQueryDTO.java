package com.tnong.boot.system.dict.domain.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SysDictTypeQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String typeCode;
    private String name;
    private Integer status;
    private Long current = 1L;
    private Long size = 10L;
    
    public Long getOffset() {
        return (current - 1) * size;
    }
}
