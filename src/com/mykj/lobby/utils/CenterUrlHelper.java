package com.mykj.lobby.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;


public class CenterUrlHelper {

    /** secret */
    public static final String secret = "x2m8@K99Tx?okz";
    
    //http://192.168.7.40:8765/api.php?m=api&c=recommend&page=1&size=10&ver=1.0&sign=4604dfee5bd25a78c24d5bf01e1976c8

    /**
     * 推广分红有使用
     * 采用MD5算法的校验串
     */
	public static String getSign(String params, String secret) {
        // 去除"&"
        String[] s_A = params.split("&");
        // 排序
        Arrays.sort(s_A);
        // 组合
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s_A.length; i++) {
            sb.append(s_A[i]);
        }
        // 加上密钥
        sb.append("secret=").append(secret);
        // 生成参数串
        StringBuffer result_sb = new StringBuffer();
        result_sb.append("&sign=");
        result_sb.append(md5(sb.toString()));
        return result_sb.toString();
    }

	public static String md5(String string) {
		if (string == null || string.trim().length() < 1) {
			return null;
		}
		try {
			return getMD5(string.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private static String getMD5(byte[] source) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			StringBuffer result = new StringBuffer();
			for (byte b : md5.digest(source)) {
				result.append(Integer.toHexString((b & 0xf0) >>> 4));
				result.append(Integer.toHexString(b & 0x0f));
			}
			return result.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
