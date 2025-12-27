package com.tnong.boot.common.constant;

/**
 * 业务操作类型
 */
public enum BusinessType {

    /**
     * 其它
     */
    OTHER(0, "其它"),

    /**
     * 新增
     */
    INSERT(1, "新增"),

    /**
     * 修改
     */
    UPDATE(2, "修改"),

    /**
     * 删除
     */
    DELETE(3, "删除"),

    /**
     * 导入
     */
    IMPORT(4, "导入"),

    /**
     * 导出
     */
    EXPORT(5, "导出"),

    /**
     * 查询
     */
    SELECT(6, "查询");

    private final Integer code;
    private final String desc;

    BusinessType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
