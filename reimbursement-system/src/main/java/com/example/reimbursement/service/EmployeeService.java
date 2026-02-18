package com.example.reimbursement.service;

import com.example.reimbursement.common.BusinessException;
import com.example.reimbursement.common.MD5Util;
import com.example.reimbursement.entity.Employee;
import com.example.reimbursement.mapper.EmployeeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * 员工服务类
 */
@Slf4j
@Service
public class EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 根据ID查询员工
     */
    public Employee getById(Integer id) {
        if (id == null) {
            throw new BusinessException("员工ID不能为空");
        }
        Employee employee = employeeMapper.selectById(id);
        if (employee != null) {
            employee.setPassword(null); // 不返回密码
        }
        return employee;
    }

    /**
     * 根据用户名查询员工
     */
    public Employee getByUsername(String username) {
        if (StringUtils.isBlank(username)) {
            throw new BusinessException("用户名不能为空");
        }
        return employeeMapper.selectByUsername(username);
    }

    /**
     * 查询所有员工
     */
    public List<Employee> getAll() {
        List<Employee> employees = employeeMapper.selectAll();
        // 不返回密码
        employees.forEach(e -> e.setPassword(null));
        return employees;
    }

    /**
     * 条件查询员工
     */
    public List<Employee> getByCondition(String name, String department) {
        List<Employee> employees = employeeMapper.selectByCondition(name, department);
        employees.forEach(e -> e.setPassword(null));
        return employees;
    }

    /**
     * 添加员工
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(Employee employee) {
        // 校验
        if (StringUtils.isBlank(employee.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (StringUtils.isBlank(employee.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        if (StringUtils.isBlank(employee.getName())) {
            throw new BusinessException("姓名不能为空");
        }

        // 检查用户名是否已存在
        Employee existEmployee = employeeMapper.selectByUsername(employee.getUsername());
        if (existEmployee != null) {
            throw new BusinessException("用户名已存在");
        }

        // 密码加密
        employee.setPassword(MD5Util.encrypt(employee.getPassword()));
        employee.setStatus(1); // 默认在职

        int result = employeeMapper.insert(employee);
        if (result <= 0) {
            throw new BusinessException("添加员工失败");
        }
        log.info("添加员工成功, ID: {}", employee.getId());
    }

    /**
     * 更新员工
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Employee employee) {
        if (employee.getId() == null) {
            throw new BusinessException("员工ID不能为空");
        }

        Employee existEmployee = employeeMapper.selectById(employee.getId());
        if (existEmployee == null) {
            throw new BusinessException("员工不存在");
        }

        // 如果修改了密码,需要加密
        if (StringUtils.isNotBlank(employee.getPassword())) {
            employee.setPassword(MD5Util.encrypt(employee.getPassword()));
        }

        int result = employeeMapper.update(employee);
        if (result <= 0) {
            throw new BusinessException("更新员工失败");
        }
        log.info("更新员工成功, ID: {}", employee.getId());
    }

    /**
     * 删除员工
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        if (id == null) {
            throw new BusinessException("员工ID不能为空");
        }

        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new BusinessException("员工不存在");
        }

        int result = employeeMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除员工失败");
        }
        log.info("删除员工成功, ID: {}", id);
    }

    /**
     * 登录验证
     */
    public Employee login(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new BusinessException("用户名或密码不能为空");
        }
        username = username.trim();
        password = password.trim();

        Employee employee = employeeMapper.selectByUsername(username);
        if (employee == null) {
            throw new BusinessException("用户不存在");
        }

        if (!MD5Util.verify(password, employee.getPassword())) {
            throw new BusinessException("密码错误");
        }

        if (employee.getStatus() == 0) {
            throw new BusinessException("该账号已离职");
        }

        employee.setPassword(null); // 不返回密码
        return employee;
    }

    /**
     * 获取用户角色编码集合
     */
    public Set<String> getRoleCodesByUsername(String username) {
        return employeeMapper.selectRoleCodesByUsername(username);
    }
}
