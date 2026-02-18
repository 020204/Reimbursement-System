package com.example.reimbursement.config.shiro;

import com.example.reimbursement.entity.Employee;
import com.example.reimbursement.service.EmployeeService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * 自定义Realm
 */
public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private EmployeeService employeeService;

    public CustomRealm() {
        // 使用 MD5 匹配器：Token 为明文密码，Realm 返回 DB 中的 MD5，由匹配器做 MD5(明文) 与 DB 比对
        setCredentialsMatcher(new Md5CredentialsMatcher());
    }

    /**
     * 授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        
        // 获取用户角色
        Set<String> roles = employeeService.getRoleCodesByUsername(username);
        authorizationInfo.setRoles(roles);
        
        return authorizationInfo;
    }

    /**
     * 认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) 
            throws AuthenticationException {
        
        String username = (String) token.getPrincipal();
        Employee employee = employeeService.getByUsername(username);
        
        if (employee == null) {
            throw new UnknownAccountException("用户不存在");
        }
        
        if (employee.getStatus() == 0) {
            throw new LockedAccountException("账号已被锁定");
        }
        
        // 注意:这里返回的密码是MD5加密后的,Shiro会自动进行匹配
        // 如果需要自定义密码验证,可以使用CredentialsMatcher
        return new SimpleAuthenticationInfo(username, employee.getPassword(), getName());
    }
}
