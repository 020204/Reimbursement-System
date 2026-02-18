package com.example.reimbursement.mapper;

import com.example.reimbursement.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 员工Mapper接口
 */
@Mapper
public interface EmployeeMapper {

    /**
     * 根据ID查询员工
     */
    Employee selectById(Integer id);

    /**
     * 根据用户名查询员工
     */
    Employee selectByUsername(String username);

    /**
     * 查询所有员工
     */
    List<Employee> selectAll();

    /**
     * 插入员工
     */
    int insert(Employee employee);

    /**
     * 更新员工
     */
    int update(Employee employee);

    /**
     * 删除员工
     */
    int deleteById(Integer id);

    /**
     * 根据用户名获取角色编码集合
     */
    Set<String> selectRoleCodesByUsername(String username);

    /**
     * 条件查询员工
     */
    List<Employee> selectByCondition(@Param("name") String name,
                                     @Param("department") String department);

    /**
     * 根据部门查询员工ID列表
     */
    List<Integer> selectIdsByDepartment(@Param("department") String department);
}
