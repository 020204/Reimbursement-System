package com.example.reimbursement.config.shiro;

import com.alibaba.fastjson.JSON;
import com.example.reimbursement.common.Result;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * 未认证时对 /api 请求返回 401 JSON，避免 302 重定向导致前端 SPA 被带到登录页。
 */
public class Http401AuthenticationFilter extends FormAuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        // 仅对 API 请求返回 401 JSON
        String uri = req.getRequestURI();
        if (uri != null && uri.startsWith("/api")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json;charset=UTF-8");
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
            Result<?> result = Result.unauthorized();
            resp.getWriter().write(JSON.toJSONString(result));
            return false;
        }

        return super.onAccessDenied(request, response);
    }
}
