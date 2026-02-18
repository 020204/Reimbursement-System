package com.example.reimbursement.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 员工实体类
 */
@Data
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 员工ID
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 部门名称（冗余字段）
     */
    private String department;

    /**
     * 部门ID（外键关联）
     */
    private Integer departmentId;

    /**
     * 职位
     */
    private String position;

    /**
     * 状态:1-在职,0-离职
     */
    private Integer status;

    /**
     * 入职时间
     */
    private Date hireDate;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
