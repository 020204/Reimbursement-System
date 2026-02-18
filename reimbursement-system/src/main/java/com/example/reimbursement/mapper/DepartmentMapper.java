package com.example.reimbursement.mapper;

import com.example.reimbursement.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 部门Mapper接口
 */
@Mapper
public interface DepartmentMapper {

    /**
     * 根据ID查询
     */
    Department selectById(@Param("id") Integer id);

    /**
     * 根据编码查询
     */
    Department selectByCode(@Param("code") String code);

    /**
     * 查询所有
     */
    List<Department> selectAll();

    /**
     * 条件查询
     */
    List<Department> selectByCondition(@Param("name") String name, @Param("code") String code);

    /**
     * 插入
     */
    int insert(Department department);

    /**
     * 更新
     */
    int update(Department department);

    /**
     * 删除
     */
    int deleteById(@Param("id") Integer id);
}
