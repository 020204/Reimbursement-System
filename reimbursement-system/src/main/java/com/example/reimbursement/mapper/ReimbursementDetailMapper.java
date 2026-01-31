package com.example.reimbursement.mapper;

import com.example.reimbursement.entity.ReimbursementDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 报销明细Mapper接口
 */
@Mapper
public interface ReimbursementDetailMapper {

    /**
     * 根据ID查询明细
     */
    ReimbursementDetail selectById(Integer id);

    /**
     * 根据报销单ID查询明细列表
     */
    List<ReimbursementDetail> selectByFormId(Integer formId);

    /**
     * 插入明细
     */
    int insert(ReimbursementDetail detail);

    /**
     * 批量插入明细
     */
    int batchInsert(List<ReimbursementDetail> details);

    /**
     * 更新明细
     */
    int update(ReimbursementDetail detail);

    /**
     * 删除明细
     */
    int deleteById(Integer id);

    /**
     * 根据报销单ID删除明细
     */
    int deleteByFormId(Integer formId);
}
