package com.example.reimbursement.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 审批记录实体类
 */
@Data
public class ApprovalRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 审批记录ID
     */
    private Integer id;

    /**
     * 报销单ID
     */
    private Integer formId;

    /**
     * 审批人ID
     */
    private Integer approverId;

    /**
     * 审批级别:1-一级,2-二级,3-三级
     */
    private Integer approvalLevel;

    /**
     * 审批结果:APPROVED-通过,REJECTED-驳回
     */
    private String result;

    /**
     * 审批意见
     */
    private String comment;

    /**
     * 审批时间
     */
    private Date approvalTime;

    /**
     * 审批人姓名(非数据库字段)
     */
    private String approverName;
}
