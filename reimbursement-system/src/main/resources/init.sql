-- 创建数据库
CREATE DATABASE IF NOT EXISTS reimbursement_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE reimbursement_db;

-- 员工表
DROP TABLE IF EXISTS employee;
CREATE TABLE employee (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '员工ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    department VARCHAR(50) COMMENT '部门',
    position VARCHAR(50) COMMENT '职位',
    status TINYINT DEFAULT 1 COMMENT '状态:1-在职,0-离职',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_department (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 角色表
DROP TABLE IF EXISTS role;
CREATE TABLE role (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '角色ID',
    name VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
DROP TABLE IF EXISTS user_role;
CREATE TABLE user_role (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    employee_id INT NOT NULL COMMENT '员工ID',
    role_id INT NOT NULL COMMENT '角色ID',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_employee_role (employee_id, role_id),
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 报销单表
DROP TABLE IF EXISTS reimbursement_form;
CREATE TABLE reimbursement_form (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '报销单ID',
    form_no VARCHAR(50) NOT NULL UNIQUE COMMENT '报销单号',
    employee_id INT NOT NULL COMMENT '申请人ID',
    title VARCHAR(200) NOT NULL COMMENT '报销标题',
    type VARCHAR(20) NOT NULL COMMENT '报销类型:TRAVEL-差旅,OFFICE-办公,COMMUNICATION-通讯,ENTERTAINMENT-招待,OTHER-其他',
    amount DECIMAL(10, 2) NOT NULL COMMENT '报销金额',
    description TEXT COMMENT '报销说明',
    attachment VARCHAR(500) COMMENT '附件URL',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态:DRAFT-草稿,PENDING-待审批,APPROVED-已通过,REJECTED-已驳回,CANCELLED-已取消',
    submit_time TIMESTAMP NULL COMMENT '提交时间',
    approve_time TIMESTAMP NULL COMMENT '审批完成时间',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (employee_id) REFERENCES employee(id),
    INDEX idx_employee_id (employee_id),
    INDEX idx_status (status),
    INDEX idx_form_no (form_no),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报销单表';

-- 报销明细表
DROP TABLE IF EXISTS reimbursement_detail;
CREATE TABLE reimbursement_detail (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    form_id INT NOT NULL COMMENT '报销单ID',
    item_name VARCHAR(100) NOT NULL COMMENT '费用项目',
    amount DECIMAL(10, 2) NOT NULL COMMENT '金额',
    occurrence_date DATE NOT NULL COMMENT '发生日期',
    description VARCHAR(500) COMMENT '说明',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (form_id) REFERENCES reimbursement_form(id) ON DELETE CASCADE,
    INDEX idx_form_id (form_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报销明细表';

-- 审批记录表
DROP TABLE IF EXISTS approval_record;
CREATE TABLE approval_record (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '审批记录ID',
    form_id INT NOT NULL COMMENT '报销单ID',
    approver_id INT NOT NULL COMMENT '审批人ID',
    approval_level INT NOT NULL COMMENT '审批级别:1-一级,2-二级,3-三级',
    result VARCHAR(20) NOT NULL COMMENT '审批结果:APPROVED-通过,REJECTED-驳回',
    comment TEXT COMMENT '审批意见',
    approval_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '审批时间',
    FOREIGN KEY (form_id) REFERENCES reimbursement_form(id) ON DELETE CASCADE,
    FOREIGN KEY (approver_id) REFERENCES employee(id),
    INDEX idx_form_id (form_id),
    INDEX idx_approver_id (approver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审批记录表';

-- 操作日志表
DROP TABLE IF EXISTS operation_log;
CREATE TABLE operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID',
    employee_id INT COMMENT '操作人ID',
    module VARCHAR(50) COMMENT '操作模块',
    operation VARCHAR(50) COMMENT '操作类型',
    method VARCHAR(200) COMMENT '方法名',
    params TEXT COMMENT '请求参数',
    result TEXT COMMENT '返回结果',
    ip VARCHAR(50) COMMENT 'IP地址',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX idx_employee_id (employee_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 插入初始数据
-- 插入角色
INSERT INTO role (name, code, description) VALUES
('管理员', 'ADMIN', '系统管理员'),
('财务', 'FINANCE', '财务人员'),
('部门主管', 'MANAGER', '部门主管'),
('普通员工', 'EMPLOYEE', '普通员工');

-- 插入员工 (密码都是: 123456, 使用MD5加密后的值)
INSERT INTO employee (username, password, name, email, phone, department, position) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 'admin@example.com', '13800000001', 'IT部', '系统管理员'),
('finance', 'e10adc3949ba59abbe56e057f20f883e', '财务张三', 'finance@example.com', '13800000002', '财务部', '财务主管'),
('manager', 'e10adc3949ba59abbe56e057f20f883e', '主管李四', 'manager@example.com', '13800000003', '销售部', '销售主管'),
('employee', 'e10adc3949ba59abbe56e057f20f883e', '员工王五', 'employee@example.com', '13800000004', '销售部', '销售专员');

-- 分配角色
INSERT INTO user_role (employee_id, role_id) VALUES
(1, 1), -- admin -> ADMIN
(2, 2), -- finance -> FINANCE
(3, 3), -- manager -> MANAGER
(4, 4); -- employee -> EMPLOYEE

-- 插入示例报销单
INSERT INTO reimbursement_form (form_no, employee_id, title, type, amount, description, status, submit_time) VALUES
('RB202401001', 4, '北京出差报销', 'TRAVEL', 3500.00, '2024年1月北京客户拜访差旅费', 'APPROVED', '2024-01-10 10:00:00'),
('RB202401002', 4, '办公用品采购', 'OFFICE', 580.00, '部门办公用品采购', 'PENDING', '2024-01-15 14:30:00'),
('RB202401003', 3, '客户招待费', 'ENTERTAINMENT', 1200.00, '重要客户商务宴请', 'PENDING', '2024-01-18 16:00:00');

-- 插入报销明细
INSERT INTO reimbursement_detail (form_id, item_name, amount, occurrence_date, description) VALUES
(1, '高铁票', 1200.00, '2024-01-05', '北京往返高铁票'),
(1, '酒店住宿', 1800.00, '2024-01-06', '3晚酒店费用'),
(1, '餐费', 500.00, '2024-01-06', '出差期间餐费'),
(2, '打印纸', 300.00, '2024-01-12', 'A4打印纸20包'),
(2, '文具', 280.00, '2024-01-12', '签字笔、笔记本等'),
(3, '餐饮费', 1200.00, '2024-01-17', '某某餐厅商务宴请');

-- 插入审批记录
INSERT INTO approval_record (form_id, approver_id, approval_level, result, comment) VALUES
(1, 3, 1, 'APPROVED', '同意报销'),
(1, 2, 2, 'APPROVED', '审批通过');
