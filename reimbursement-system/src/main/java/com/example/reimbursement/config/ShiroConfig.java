package com.example.reimbursement.config;

import com.example.reimbursement.config.shiro.CustomRealm;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shiro配置类
 */
@Configuration
public class ShiroConfig {

    /**
     * 自定义Realm
     */
    @Bean
    public CustomRealm customRealm() {
        return new CustomRealm();
    }

    /**
     * SecurityManager
     */
    @Bean
    public SecurityManager securityManager(CustomRealm customRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customRealm);
        return securityManager;
    }

    /**
     * ShiroFilter
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 设置登录URL
        shiroFilterFactoryBean.setLoginUrl("/employee/login");
        // 设置未授权URL
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

        // 配置访问权限
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        
        // 公开接口
        filterChainDefinitionMap.put("/employee/login", "anon");
        filterChainDefinitionMap.put("/employee/logout", "anon");
        
        // 需要认证的接口
        filterChainDefinitionMap.put("/employee/**", "authc");
        filterChainDefinitionMap.put("/reimbursement/**", "authc");
        
        // 需要特定角色的接口
        // filterChainDefinitionMap.put("/reimbursement/approve", "roles[MANAGER,FINANCE]");
        // filterChainDefinitionMap.put("/reimbursement/batch-approve", "roles[MANAGER,FINANCE]");
        
        // 其他请求都需要认证
        filterChainDefinitionMap.put("/**", "authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    /**
     * 开启Shiro注解支持
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
