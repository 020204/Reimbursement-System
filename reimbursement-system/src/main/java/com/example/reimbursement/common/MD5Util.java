package com.example.reimbursement.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 */
public class MD5Util {

    /**
     * MD5加密
     */
    public static String encrypt(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
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
     * 验证密码
     */
    public static boolean verify(String text, String md5) {
        String encrypted = encrypt(text);
        return encrypted != null && encrypted.equals(md5);
    }
}
