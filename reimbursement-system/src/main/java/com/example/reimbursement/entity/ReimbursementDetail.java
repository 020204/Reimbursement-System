package com.example.reimbursement.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 报销明细实体类
 */
@Data
public class ReimbursementDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 明细ID
     */
    private Integer id;

    /**
     * 报销单ID
     */
    private Integer formId;

    /**
     * 费用项目
     */
    private String itemName;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 发生日期
     */
    private Date occurrenceDate;

    /**
     * 说明
     */
    private String description;

    /**
     * 创建时间
     */
    private Date createTime;
}
