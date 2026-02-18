package com.example.reimbursement.entity;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 报销单实体类
 */
@Data
public class ReimbursementForm implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 报销单ID
     */
    private Integer id;

    /**
     * 报销单号
     */
    private String formNo;

    /**
     * 申请人ID
     */
    private Integer employeeId;

    /**
     * 报销标题
     */
    private String title;

    /**
     * 报销类型:TRAVEL-差旅,OFFICE-办公,COMMUNICATION-通讯,ENTERTAINMENT-招待,OTHER-其他
     */
    private String type;

    /**
     * 报销金额
     */
    private BigDecimal amount;

    /**
     * 报销说明
     */
    private String description;

    /**
     * 附件URL
     */
    private String attachment;

    /**
     * 状态:DRAFT-草稿,PENDING-待审批,APPROVED-已通过,REJECTED-已驳回,CANCELLED-已取消
     */
    private String status;

    /**
     * 提交时间
     */
    private Date submitTime;

    /**
     * 审批完成时间
     */
    private Date approveTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 申请人姓名(非数据库字段)
     */
    private String employeeName;

    /**
     * 报销明细列表(非数据库字段)
     */
    private List<ReimbursementDetail> details;
}
