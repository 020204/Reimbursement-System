package com.example.reimbursement.controller;

import com.example.reimbursement.common.Result;
import com.example.reimbursement.entity.Department;
import com.example.reimbursement.mapper.DepartmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门控制器
 */
@Slf4j
@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 查询所有部门
     */
    @GetMapping("/list")
    public Result<List<Department>> getAll() {
        List<Department> departments = departmentMapper.selectAll();
        return Result.success(departments);
    }

    /**
     * 根据ID查询部门
     */
    @GetMapping("/{id}")
    public Result<Department> getById(@PathVariable Integer id) {
        Department department = departmentMapper.selectById(id);
        return Result.success(department);
    }

    /**
     * 条件查询部门
     */
    @GetMapping("/search")
    public Result<List<Department>> search(@RequestParam(required = false) String name,
                                           @RequestParam(required = false) String code) {
        List<Department> departments = departmentMapper.selectByCondition(name, code);
        return Result.success(departments);
    }

    /**
     * 添加部门（仅系统管理员）
     */
    @PostMapping
    @RequiresRoles("ADMIN")
    public Result<?> add(@RequestBody Department department) {
        departmentMapper.insert(department);
        return Result.success("添加成功");
    }

    /**
     * 更新部门（仅系统管理员）
     */
    @PutMapping
    @RequiresRoles("ADMIN")
    public Result<?> update(@RequestBody Department department) {
        departmentMapper.update(department);
        return Result.success("更新成功");
    }

    /**
     * 删除部门（仅系统管理员）
     */
    @DeleteMapping("/{id}")
    @RequiresRoles("ADMIN")
    public Result<?> delete(@PathVariable Integer id) {
        departmentMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
