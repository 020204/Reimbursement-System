package com.example.reimbursement.mapper;

import com.example.reimbursement.entity.ReimbursementForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 报销单Mapper接口
 */
@Mapper
public interface ReimbursementFormMapper {

    /**
     * 根据ID查询报销单
     */
    ReimbursementForm selectById(Integer id);

    /**
     * 根据报销单号查询
     */
    ReimbursementForm selectByFormNo(String formNo);

    /**
     * 查询所有报销单
     */
    List<ReimbursementForm> selectAll();

    /**
     * 条件查询报销单
     */
    List<ReimbursementForm> selectByCondition(@Param("employeeId") Integer employeeId,
                                              @Param("status") String status,
                                              @Param("type") String type);

    /**
     * 根据员工ID列表查询报销单（用于部门主管查看本部门报销）
     */
    List<ReimbursementForm> selectByEmployeeIds(@Param("employeeIds") List<Integer> employeeIds,
                                                @Param("status") String status,
                                                @Param("type") String type);

    /**
     * 插入报销单
     */
    int insert(ReimbursementForm form);

    /**
     * 批量插入报销单
     */
    int batchInsert(List<ReimbursementForm> forms);

    /**
     * 更新报销单
     */
    int update(ReimbursementForm form);

    /**
     * 批量更新状态
     */
    int batchUpdateStatus(@Param("ids") List<Integer> ids, 
                         @Param("status") String status);

    /**
     * 删除报销单
     */
    int deleteById(Integer id);

    /**
     * 统计报销单数量
     */
    int countByCondition(@Param("employeeId") Integer employeeId,
                        @Param("status") String status);
}
