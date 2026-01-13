package com.tnong.boot.system.file.domain.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class SysFileDeleteDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer version;
}
