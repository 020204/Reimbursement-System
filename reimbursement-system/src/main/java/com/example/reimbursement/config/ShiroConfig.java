package com.example.reimbursement.config;

import com.example.reimbursement.config.shiro.CustomRealm;
import com.example.reimbursement.config.shiro.Http401AuthenticationFilter;
import org.apache.shiro.event.support.DefaultEventBus;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
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
     * EventBus（Shiro 事件总线的默认实现）
     */
    @Bean
    public org.apache.shiro.event.EventBus eventBus() {
        return new DefaultEventBus();
    }

    /**
     * 过滤链定义（满足 Shiro Web 配置依赖，与下方 ShiroFilterFactoryBean 规则一致）
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chain = new DefaultShiroFilterChainDefinition();
        chain.addPathDefinition("/employee/login", "anon");
        chain.addPathDefinition("/employee/logout", "anon");
        chain.addPathDefinition("/employee/**", "authc");
        chain.addPathDefinition("/reimbursement/**", "authc");
        chain.addPathDefinition("/**", "authc");
        return chain;
    }

    /**
     * RedisManager
     */
    @Bean
    public RedisManager redisManager(
            @Value("${spring.redis.host:localhost}") String host,
            @Value("${spring.redis.port:6379}") int port,
            @Value("${spring.redis.password:}") String password,
            @Value("${spring.redis.database:0}") int database,
            @Value("${spring.redis.timeout:3000}") int timeout) {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host + ":" + port);
        if (password != null && !password.isEmpty()) {
            redisManager.setPassword(password);
        }
        redisManager.setDatabase(database);
        redisManager.setTimeout(timeout);
        return redisManager;
    }

    /**
     * RedisSessionDAO
     */
    @Bean
    public RedisSessionDAO redisSessionDAO(RedisManager redisManager) {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager);
        // Session过期时间(秒),24小时
        redisSessionDAO.setExpire(86400);
        return redisSessionDAO;
    }

    /**
     * SessionManager
     */
    @Bean
    public DefaultWebSessionManager sessionManager(RedisSessionDAO redisSessionDAO) {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO);
        // 设置Session过期时间(毫秒),24小时
        sessionManager.setGlobalSessionTimeout(86400000);
        // 删除过期Session
        sessionManager.setDeleteInvalidSessions(true);
        // 开启Session定时检查
        sessionManager.setSessionValidationSchedulerEnabled(true);
        // 设置SessionId Cookie
        SimpleCookie sessionIdCookie = new SimpleCookie("JSESSIONID");
        sessionIdCookie.setHttpOnly(true);
        sessionIdCookie.setMaxAge(86400); // 24小时
        sessionManager.setSessionIdCookie(sessionIdCookie);
        return sessionManager;
    }

    /**
     * RedisCacheManager
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisManager redisManager) {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager);
        // 设置缓存过期时间(秒),24小时
        redisCacheManager.setExpire(86400);
        return redisCacheManager;
    }

    /**
     * SecurityManager
     */
    @Bean
    public SecurityManager securityManager(CustomRealm customRealm, DefaultWebSessionManager sessionManager,
                                           RedisCacheManager redisCacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(customRealm);
        securityManager.setSessionManager(sessionManager);
        securityManager.setCacheManager(redisCacheManager);
        return securityManager;
    }

    /**
     * ShiroFilter
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        // 设置登录URL（仅非 API 请求会重定向到此）
        shiroFilterFactoryBean.setLoginUrl("/employee/login");
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");

        // 自定义过滤器：未认证时返回 401 JSON，避免 SPA 被 302 带到登录页
        Map<String, javax.servlet.Filter> filters = new LinkedHashMap<>();
        filters.put("apiAuthc", new Http401AuthenticationFilter());
        shiroFilterFactoryBean.setFilters(filters);

        // 配置访问权限（需认证的接口用 apiAuthc，返回 401 而非 302）
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/employee/login", "anon");
        filterChainDefinitionMap.put("/employee/logout", "anon");
        filterChainDefinitionMap.put("/employee/**", "apiAuthc");
        filterChainDefinitionMap.put("/reimbursement/**", "apiAuthc");
        filterChainDefinitionMap.put("/**", "apiAuthc");

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
