package com.example.reimbursement.mapper;

import com.example.reimbursement.entity.ApprovalRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 审批记录Mapper接口
 */
@Mapper
public interface ApprovalRecordMapper {

    /**
     * 根据ID查询审批记录
     */
    ApprovalRecord selectById(Integer id);

    /**
     * 根据报销单ID查询审批记录列表
     */
    List<ApprovalRecord> selectByFormId(Integer formId);

    /**
     * 插入审批记录
     */
    int insert(ApprovalRecord record);

    /**
     * 更新审批记录
     */
    int update(ApprovalRecord record);

    /**
     * 删除审批记录
     */
    int deleteById(Integer id);
}
