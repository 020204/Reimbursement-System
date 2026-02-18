package com.example.reimbursement.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 部门实体类
 */
@Data
public class Department implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private Integer id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门编码
     */
    private String code;

    /**
     * 部门描述
     */
    private String description;

    /**
     * 部门经理ID
     */
    private Integer managerId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
