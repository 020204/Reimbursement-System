package com.example.reimbursement.controller;

import com.example.reimbursement.common.PageResult;
import com.example.reimbursement.common.Result;
import com.example.reimbursement.entity.Employee;
import com.example.reimbursement.entity.ReimbursementDetail;
import com.example.reimbursement.entity.ReimbursementForm;
import com.example.reimbursement.service.EmployeeService;
import com.example.reimbursement.service.ReimbursementFormService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 报销单控制器
 */
@Slf4j
@RestController
@RequestMapping("/reimbursement")
public class ReimbursementFormController {

    @Autowired
    private ReimbursementFormService formService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 根据ID查询报销单
     */
    @GetMapping("/{id}")
    public Result<ReimbursementForm> getById(@PathVariable Integer id) {
        ReimbursementForm form = formService.getById(id);
        return Result.success(form);
    }

    /**
     * 分页查询报销单（带权限控制）
     */
    @GetMapping("/page")
    public Result<PageResult<ReimbursementForm>> getByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        
        // 获取当前登录用户
        Subject subject = SecurityUtils.getSubject();
        String username = (String) subject.getPrincipal();
        Employee currentUser = employeeService.getByUsername(username);
        
        // 获取用户角色
        List<String> roles = getCurrentUserRoles(subject);
        
        PageResult<ReimbursementForm> pageResult = formService.getByPageWithAuth(
                pageNum, pageSize, employeeId, status, type, currentUser, roles);
        return Result.success(pageResult);
    }
    
    /**
     * 获取当前用户角色列表
     */
    private List<String> getCurrentUserRoles(Subject subject) {
        List<String> roles = new ArrayList<>();
        if (subject.hasRole("ADMIN")) {
            roles.add("ADMIN");
        }
        if (subject.hasRole("FINANCE")) {
            roles.add("FINANCE");
        }
        if (subject.hasRole("MANAGER")) {
            roles.add("MANAGER");
        }
        // 默认所有用户都是员工
        roles.add("EMPLOYEE");
        return roles;
    }

    /**
     * 查询所有报销单
     */
    @GetMapping("/list")
    public Result<List<ReimbursementForm>> getAll() {
        List<ReimbursementForm> forms = formService.getAll();
        return Result.success(forms);
    }

    /**
     * 条件查询报销单
     */
    @GetMapping("/search")
    public Result<List<ReimbursementForm>> search(
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        
        List<ReimbursementForm> forms = formService.getByCondition(employeeId, status, type);
        return Result.success(forms);
    }

    /**
     * 添加报销单
     */
    @PostMapping
    public Result<?> add(@RequestBody Map<String, Object> params) {
        // 解析报销单和明细
        ReimbursementForm form = parseForm(params);
        List<ReimbursementDetail> details = parseDetails(params);
        
        formService.add(form, details);
        return Result.success("添加成功");
    }

    /**
     * 更新报销单
     */
    @PutMapping
    public Result<?> update(@RequestBody Map<String, Object> params) {
        // 解析报销单和明细
        ReimbursementForm form = parseForm(params);
        // 从params中获取id设置到form
        form.setId((Integer) params.get("id"));
        List<ReimbursementDetail> details = parseDetails(params);

        formService.updateWithDetails(form, details);
        return Result.success("更新成功");
    }

    /**
     * 提交报销单
     */
    @PutMapping("/submit/{id}")
    public Result<?> submit(@PathVariable Integer id) {
        formService.submit(id);
        return Result.success("提交成功");
    }

    /**
     * 审批报销单
     */
    @PutMapping("/approve")
    @RequiresRoles("ADMIN")
    public Result<?> approve(@RequestBody Map<String, Object> params) {
        Integer id = (Integer) params.get("id");
        Integer approverId = (Integer) params.get("approverId");
        String result = (String) params.get("result");
        String comment = (String) params.get("comment");
        
        formService.approve(id, approverId, result, comment);
        return Result.success("审批成功");
    }

    /**
     * 批量审批报销单
     */
    @PutMapping("/batch-approve")
    @RequiresRoles("ADMIN")
    public Result<?> batchApprove(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Integer> ids = (List<Integer>) params.get("ids");
        Integer approverId = (Integer) params.get("approverId");
        String result = (String) params.get("result");
        String comment = (String) params.get("comment");
        
        formService.batchApprove(ids, approverId, result, comment);
        return Result.success("批量审批成功");
    }

    /**
     * 删除报销单
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        formService.delete(id);
        return Result.success("删除成功");
    }

    /**
     * 解析报销单对象
     */
    private ReimbursementForm parseForm(Map<String, Object> params) {
        ReimbursementForm form = new ReimbursementForm();
        form.setEmployeeId((Integer) params.get("employeeId"));
        form.setTitle((String) params.get("title"));
        form.setType((String) params.get("type"));
        form.setDescription((String) params.get("description"));
        form.setAttachment((String) params.get("attachment"));
        return form;
    }

    /**
     * 解析报销明细列表
     */
    private List<ReimbursementDetail> parseDetails(Map<String, Object> params) {
        Object detailsObj = params.get("details");
        if (detailsObj == null) {
            return null;
        }
        // 使用Jackson将LinkedHashMap列表转换为ReimbursementDetail列表
        return objectMapper.convertValue(detailsObj,
                objectMapper.getTypeFactory().constructCollectionType(List.class, ReimbursementDetail.class));
    }
}
