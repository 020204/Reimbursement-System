package com.example.reimbursement.config.shiro;

import com.example.reimbursement.common.MD5Util;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;

/**
 * 使用 MD5 比对密码的凭证匹配器。
 * Token 中为用户输入的明文密码，Realm 返回的为数据库中的 MD5，由本类做加密后比对。
 */
public class Md5CredentialsMatcher extends SimpleCredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        Object tokenCredentials = getSubmittedCredentials(token);
        Object storedCredentials = getStoredCredentials(info);
        if (tokenCredentials == null || storedCredentials == null) {
            return false;
        }
        String submitted = toPlainString(tokenCredentials);
        String stored = toPlainString(storedCredentials);
        // 数据库存的是 MD5，提交的是明文：对明文做 MD5 后与数据库比对
        return MD5Util.verify(submitted, stored);
    }

    private Object getSubmittedCredentials(AuthenticationToken token) {
        if (token instanceof UsernamePasswordToken) {
            return ((UsernamePasswordToken) token).getCredentials();
        }
        return token.getCredentials();
    }

    private Object getStoredCredentials(AuthenticationInfo info) {
        return info.getCredentials();
    }

    private String toPlainString(Object credentials) {
        if (credentials == null) {
            return null;
        }
        if (credentials instanceof char[]) {
            return new String((char[]) credentials);
        }
        return String.valueOf(credentials);
    }
}
