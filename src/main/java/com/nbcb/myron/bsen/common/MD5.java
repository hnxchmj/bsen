package com.nbcb.myron.bsen.common;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

/**
 * MD5通用类
 */
public class MD5 {
    private static final Logger logger = LoggerFactory.getLogger(MD5.class);
    /**
     * MD5方法
     *
     * @param text 明文
     * @param key 密钥
     * @return 密文
     * @throws Exception
     */
    public static String md5(String text, String key) throws Exception {
        //加密后的字符串
        String encodeStr=DigestUtils.md5Hex(text + key);
        logger.info("MD5加密后的字符串为:encodeStr="+encodeStr);
        return encodeStr;
    }

    /**
     * MD5验证方法
     *
     * @param text 明文
     * @param key 密钥
     * @param md5 密文
     * @return true/false
     * @throws Exception
     */
    public static boolean verify(String text, String key, String md5) throws Exception {
        //根据传入的密钥进行验证
        String md5Text = md5(text, key);
        if(md5Text.equalsIgnoreCase(md5))
        {
            logger.info("MD5验证通过");
            return true;
        }
        return false;
    }
    /**
     * MD5生成预支付随机32位字符串
     *
     * @return String
     */
    public static String randomStr(){
        //返回随机字符串，长度32位
        UUID uuid = UUID.randomUUID();
        String random = uuid.toString().replace("-", "");
        return random;
    }
}
