package com.example.reimbursement.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类（统一使用 UTF-8，避免编码不一致导致校验失败）
 */
public class MD5Util {

    /**
     * MD5加密
     */
    public static String encrypt(String str) {
        if (str == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes(StandardCharsets.UTF_8));
            byte[] byteDigest = md.digest();
            StringBuilder buf = new StringBuilder();
            for (byte b : byteDigest) {
                buf.append(String.format("%02x", b & 0xff));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证密码（text 为明文，md5 为数据库中的密文）
     */
    public static boolean verify(String text, String md5) {
        if (text == null || md5 == null) return false;
        String encrypted = encrypt(text.trim());
        return encrypted != null && encrypted.equals(md5.trim());
    }
}
