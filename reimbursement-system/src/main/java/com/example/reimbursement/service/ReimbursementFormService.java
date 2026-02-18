package com.example.reimbursement.service;

import com.example.reimbursement.common.BusinessException;
import com.example.reimbursement.common.PageResult;
import com.example.reimbursement.entity.ApprovalRecord;
import com.example.reimbursement.entity.ReimbursementDetail;
import com.example.reimbursement.entity.ReimbursementForm;
import com.example.reimbursement.mapper.ApprovalRecordMapper;
import com.example.reimbursement.mapper.EmployeeMapper;
import com.example.reimbursement.mapper.ReimbursementDetailMapper;
import com.example.reimbursement.mapper.ReimbursementFormMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 报销单服务类
 */
@Slf4j
@Service
public class ReimbursementFormService {

    @Autowired
    private ReimbursementFormMapper formMapper;

    @Autowired
    private ReimbursementDetailMapper detailMapper;

    @Autowired
    private ApprovalRecordMapper approvalRecordMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 根据ID查询报销单
     */
    @Cacheable(value = "reimbursementForm", key = "#id")
    public ReimbursementForm getById(Integer id) {
        if (id == null) {
            throw new BusinessException("报销单ID不能为空");
        }
        ReimbursementForm form = formMapper.selectById(id);
        if (form == null) {
            throw new BusinessException("报销单不存在");
        }
        // 查询明细
        List<ReimbursementDetail> details = detailMapper.selectByFormId(id);
        form.setDetails(details);

        return form;
    }

    /**
     * 分页查询报销单（带权限控制）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param employeeId 员工ID（普通员工只能看自己的）
     * @param status 状态筛选
     * @param type 类型筛选
     * @param currentUser 当前登录用户
     * @param userRoles 当前用户角色列表
     */
    public PageResult<ReimbursementForm> getByPageWithAuth(Integer pageNum, Integer pageSize,
            Integer employeeId, String status, String type,
            com.example.reimbursement.entity.Employee currentUser, java.util.List<String> userRoles) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        PageHelper.startPage(pageNum, pageSize);
        List<ReimbursementForm> list;

        // 权限控制逻辑
        if (userRoles.contains("ADMIN") || userRoles.contains("FINANCE")) {
            // 管理员/财务：查看全部
            list = formMapper.selectByCondition(null, status, type);
        } else {
            // 其他角色（包括 MANAGER、普通员工）：只能看自己
            list = formMapper.selectByCondition(currentUser.getId(), status, type);
        }

        PageInfo<ReimbursementForm> pageInfo = new PageInfo<>(list);
        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, pageInfo.getList());
    }

    /**
     * 分页查询报销单（兼容旧接口）
     */
    public PageResult<ReimbursementForm> getByPage(Integer pageNum, Integer pageSize,
            Integer employeeId, String status, String type) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        PageHelper.startPage(pageNum, pageSize);
        List<ReimbursementForm> list = formMapper.selectByCondition(employeeId, status, type);
        PageInfo<ReimbursementForm> pageInfo = new PageInfo<>(list);

        return new PageResult<>(pageInfo.getTotal(), pageNum, pageSize, pageInfo.getList());
    }

    /**
     * 查询所有报销单
     */
    public List<ReimbursementForm> getAll() {
        return formMapper.selectAll();
    }

    /**
     * 条件查询报销单
     */
    public List<ReimbursementForm> getByCondition(Integer employeeId, String status, String type) {
        return formMapper.selectByCondition(employeeId, status, type);
    }

    /**
     * 添加报销单
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "reimbursementForm", allEntries = true)
    public void add(ReimbursementForm form, List<ReimbursementDetail> details) {
        // 校验
        if (form.getEmployeeId() == null) {
            throw new BusinessException("申请人不能为空");
        }
        if (StringUtils.isBlank(form.getTitle())) {
            throw new BusinessException("报销标题不能为空");
        }
        if (StringUtils.isBlank(form.getType())) {
            throw new BusinessException("报销类型不能为空");
        }

        // 生成报销单号
        String formNo = generateFormNo();
        form.setFormNo(formNo);
        form.setStatus("DRAFT"); // 默认状态为草稿

        // 计算总金额
        if (details != null && !details.isEmpty()) {
            BigDecimal totalAmount = details.stream()
                    .map(ReimbursementDetail::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            form.setAmount(totalAmount);
        }

        // 插入报销单
        int result = formMapper.insert(form);
        if (result <= 0) {
            throw new BusinessException("添加报销单失败");
        }

        // 插入明细
        if (details != null && !details.isEmpty()) {
            for (ReimbursementDetail detail : details) {
                detail.setFormId(form.getId());
            }
            detailMapper.batchInsert(details);
        }

        log.info("添加报销单成功, 单号: {}", formNo);
    }

    /**
     * 更新报销单
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "reimbursementForm", key = "#form.id")
    public void update(ReimbursementForm form) {
        if (form.getId() == null) {
            throw new BusinessException("报销单ID不能为空");
        }

        ReimbursementForm existForm = formMapper.selectById(form.getId());
        if (existForm == null) {
            throw new BusinessException("报销单不存在");
        }

        // 只有草稿状态才能修改
        if (!"DRAFT".equals(existForm.getStatus())) {
            throw new BusinessException("只有草稿状态的报销单才能修改");
        }

        int result = formMapper.update(form);
        if (result <= 0) {
            throw new BusinessException("更新报销单失败");
        }
        log.info("更新报销单成功, ID: {}", form.getId());
    }

    /**
     * 更新报销单（包含明细）
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "reimbursementForm", key = "#form.id")
    public void updateWithDetails(ReimbursementForm form, List<ReimbursementDetail> details) {
        if (form.getId() == null) {
            throw new BusinessException("报销单ID不能为空");
        }

        ReimbursementForm existForm = formMapper.selectById(form.getId());
        if (existForm == null) {
            throw new BusinessException("报销单不存在");
        }

        // 只有草稿状态才能修改
        if (!"DRAFT".equals(existForm.getStatus())) {
            throw new BusinessException("只有草稿状态的报销单才能修改");
        }

        // 计算总金额
        if (details != null && !details.isEmpty()) {
            BigDecimal totalAmount = details.stream()
                    .map(ReimbursementDetail::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            form.setAmount(totalAmount);
        } else {
            form.setAmount(BigDecimal.ZERO);
        }

        // 更新报销单
        int result = formMapper.update(form);
        if (result <= 0) {
            throw new BusinessException("更新报销单失败");
        }

        // 删除原有明细
        detailMapper.deleteByFormId(form.getId());

        // 插入新明细
        if (details != null && !details.isEmpty()) {
            for (ReimbursementDetail detail : details) {
                detail.setFormId(form.getId());
            }
            detailMapper.batchInsert(details);
        }

        log.info("更新报销单成功, ID: {}", form.getId());
    }

    /**
     * 提交报销单
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "reimbursementForm", key = "#id")
    public void submit(Integer id) {
        if (id == null) {
            throw new BusinessException("报销单ID不能为空");
        }

        ReimbursementForm form = formMapper.selectById(id);
        if (form == null) {
            throw new BusinessException("报销单不存在");
        }

        if (!"DRAFT".equals(form.getStatus())) {
            throw new BusinessException("只有草稿状态的报销单才能提交");
        }

        // 检查是否有明细
        List<ReimbursementDetail> details = detailMapper.selectByFormId(id);
        if (details == null || details.isEmpty()) {
            throw new BusinessException("报销单没有明细,无法提交");
        }

        form.setStatus("PENDING");
        form.setSubmitTime(new Date());
        formMapper.update(form);

        log.info("提交报销单成功, ID: {}", id);
    }

    /**
     * 批量审批报销单
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "reimbursementForm", allEntries = true)
    public void batchApprove(List<Integer> ids, Integer approverId, String result, String comment) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("报销单ID列表不能为空");
        }
        if (approverId == null) {
            throw new BusinessException("审批人不能为空");
        }
        if (StringUtils.isBlank(result)) {
            throw new BusinessException("审批结果不能为空");
        }

        for (Integer id : ids) {
            approve(id, approverId, result, comment);
        }

        log.info("批量审批报销单成功, 数量: {}", ids.size());
    }

    /**
     * 审批报销单
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "reimbursementForm", key = "#id")
    public void approve(Integer id, Integer approverId, String result, String comment) {
        if (id == null) {
            throw new BusinessException("报销单ID不能为空");
        }

        ReimbursementForm form = formMapper.selectById(id);
        if (form == null) {
            throw new BusinessException("报销单不存在");
        }

        if (!"PENDING".equals(form.getStatus())) {
            throw new BusinessException("只有待审批状态的报销单才能审批");
        }

        // 记录审批记录
        ApprovalRecord record = new ApprovalRecord();
        record.setFormId(id);
        record.setApproverId(approverId);
        record.setApprovalLevel(1); // 简化处理,都是一级审批
        record.setResult(result);
        record.setComment(comment);
        approvalRecordMapper.insert(record);

        // 更新报销单状态
        if ("APPROVED".equals(result)) {
            form.setStatus("APPROVED");
        } else if ("REJECTED".equals(result)) {
            form.setStatus("REJECTED");
        }
        form.setApproveTime(new Date());
        formMapper.update(form);

        log.info("审批报销单成功, ID: {}, 结果: {}", id, result);
    }

    /**
     * 删除报销单
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "reimbursementForm", key = "#id")
    public void delete(Integer id) {
        if (id == null) {
            throw new BusinessException("报销单ID不能为空");
        }

        ReimbursementForm form = formMapper.selectById(id);
        if (form == null) {
            throw new BusinessException("报销单不存在");
        }

        // 只有草稿状态才能删除
        if (!"DRAFT".equals(form.getStatus())) {
            throw new BusinessException("只有草稿状态的报销单才能删除");
        }

        // 删除明细
        detailMapper.deleteByFormId(id);

        // 删除报销单
        int result = formMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除报销单失败");
        }
        log.info("删除报销单成功, ID: {}", id);
    }

    /**
     * 生成报销单号
     */
    private String generateFormNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(new Date());
        String random = String.format("%04d", (int) (Math.random() * 10000));
        return "RB" + date + random;
    }
}
