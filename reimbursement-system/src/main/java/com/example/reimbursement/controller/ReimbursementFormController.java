package com.example.reimbursement.controller;

import com.example.reimbursement.common.PageResult;
import com.example.reimbursement.common.Result;
import com.example.reimbursement.entity.ReimbursementDetail;
import com.example.reimbursement.entity.ReimbursementForm;
import com.example.reimbursement.service.ReimbursementFormService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 根据ID查询报销单
     */
    @GetMapping("/{id}")
    public Result<ReimbursementForm> getById(@PathVariable Integer id) {
        ReimbursementForm form = formService.getById(id);
        return Result.success(form);
    }

    /**
     * 分页查询报销单
     */
    @GetMapping("/page")
    public Result<PageResult<ReimbursementForm>> getByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer employeeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type) {
        
        PageResult<ReimbursementForm> pageResult = formService.getByPage(pageNum, pageSize, employeeId, status, type);
        return Result.success(pageResult);
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
    public Result<?> update(@RequestBody ReimbursementForm form) {
        formService.update(form);
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
    @RequiresRoles({"MANAGER", "FINANCE"})
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
    @RequiresRoles({"MANAGER", "FINANCE"})
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
    @SuppressWarnings("unchecked")
    private List<ReimbursementDetail> parseDetails(Map<String, Object> params) {
        // 这里需要根据实际的JSON结构进行解析
        // 简化处理,实际项目中可以使用Jackson或Fastjson进行转换
        return (List<ReimbursementDetail>) params.get("details");
    }
}
