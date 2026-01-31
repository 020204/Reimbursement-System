# 报销管理系统（Reimbursement System）

一个基于 **Spring Boot + Vue 3** 的企业级报销管理系统，  
完整实现了 **员工提交 → 主管/财务审批 → 状态流转 → 数据统计** 的真实业务流程。

## 🚀 项目特点

- 🔐 **完善的权限控制**：基于 Shiro 的角色权限管理  
- 🧾 **真实报销流程**：支持多明细报销、审批意见、状态流转  
- ⚡ **批量审批功能**：支持多条报销单同时通过/驳回（核心亮点）  
- 📊 **数据统计仪表盘**：实时展示审批与报销数据  
- 🧩 **前后端分离**：RESTful API + Vue 3 + Element Plus

## 🛠 技术栈

**后端**
- Spring Boot 2.7
- MyBatis + PageHelper
- MySQL + Redis
- Apache Shiro
- Druid

**前端**
- Vue 3 (Composition API)
- Element Plus
- Pinia
- Vite

## 👥 系统角色

- 员工：创建并提交报销单  
- 主管 / 财务：审批、批量审批  
- 管理员：员工与权限管理  
