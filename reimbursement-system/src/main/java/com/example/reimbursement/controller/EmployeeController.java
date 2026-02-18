package com.example.reimbursement.controller;

import com.example.reimbursement.common.Result;
import com.example.reimbursement.entity.Employee;
import com.example.reimbursement.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 员工控制器
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        Employee employee = employeeService.login(username, password);

        // Shiro 登录：Token 传明文密码，Realm 返回 DB 中的 MD5，由 Md5CredentialsMatcher 做比对
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        subject.login(token);

        Map<String, Object> data = new HashMap<>();
        data.put("employee", employee);
        data.put("token", subject.getSession().getId());

        return Result.success("登录成功", data);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<?> logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return Result.success("登出成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public Result<Employee> getCurrentUser() {
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipal();
        Employee employee = employeeService.getByUsername(username);
        return Result.success(employee);
    }

    /**
     * 更新当前登录用户信息（个人信息）
     */
    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody Employee employee) {
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipal();
        Employee currentUser = employeeService.getByUsername(username);
        
        // 只能修改自己的信息
        if (!currentUser.getId().equals(employee.getId())) {
            return Result.error("只能修改自己的信息");
        }
        
        // 不允许修改的字段
        employee.setUsername(null);
        employee.setPassword(null);
        employee.setStatus(null);
        employee.setDepartment(null);
        employee.setDepartmentId(null);
        employee.setPosition(null);
        
        employeeService.update(employee);
        return Result.success("更新成功");
    }

    /**
     * 修改密码
     */
    @PutMapping("/change-password")
    public Result<?> changePassword(@RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        if (oldPassword == null || newPassword == null) {
            return Result.error("旧密码和新密码不能为空");
        }
        
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipal();
        
        employeeService.changePassword(username, oldPassword, newPassword);
        return Result.success("密码修改成功");
    }

    /**
     * 根据ID查询员工
     */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Integer id) {
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 查询所有员工
     */
    @GetMapping("/list")
    public Result<List<Employee>> getAll() {
        List<Employee> employees = employeeService.getAll();
        return Result.success(employees);
    }

    /**
     * 条件查询员工
     */
    @GetMapping("/search")
    public Result<List<Employee>> search(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) String department) {
        List<Employee> employees = employeeService.getByCondition(name, department);
        return Result.success(employees);
    }

    /**
     * 添加员工（仅 ADMIN）
     */
    @PostMapping
    @RequiresRoles("ADMIN")
    public Result<?> add(@RequestBody Employee employee) {
        employeeService.add(employee);
        return Result.success("添加成功");
    }

    /**
     * 更新员工（仅 ADMIN）
     */
    @PutMapping
    @RequiresRoles("ADMIN")
    public Result<?> update(@RequestBody Employee employee) {
        employeeService.update(employee);
        return Result.success("更新成功");
    }

    /**
     * 删除员工（仅 ADMIN）
     */
    @DeleteMapping("/{id}")
    @RequiresRoles("ADMIN")
    public Result<?> delete(@PathVariable Integer id) {
        employeeService.delete(id);
        return Result.success("删除成功");
    }
}
