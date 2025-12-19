package com.tnong.boot.system.role.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色分配菜单参数
 */
@Data
public class RoleMenuAssignDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 菜单ID列表
     */
    private List<Long> menuIds;
}
