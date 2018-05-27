package org.snail.rocket.common.utils;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * ${DESCRIPTION}
 *
 * @author shouchen<shouchen21647@hundsun.com>
 * @create 2018-01-18 14:17
 */

public class DESUtils {
    private static final String DES = "DES";
    private static final String PADDING = "DES/ECB/PKCS5Padding";
    private static final String DEFAULT_INCODING = "utf-8";
    public static final String DEFAULT_DES_KEY = "BOMP_ROCKET";

    public static void main(String[] args) {
       String text = "J6stiVZs";
       String encryptText = encrypt(text,DEFAULT_DES_KEY);
       System.out.println(encryptText);
       String decryptText = decrypt(encryptText,DEFAULT_DES_KEY);
       System.out.println(decryptText);

    }

    public final static String encrypt(String code, String key) {
        try{
            return Base64.byteArrayToBase64(encrypt(code.getBytes(DEFAULT_INCODING), key.getBytes(DEFAULT_INCODING)));
        }catch(Exception e) {
            e.printStackTrace();
        }
         return null;

    }


    public static byte[] encrypt(byte[] code, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);
        return cipher.doFinal(code);
    }


    public final static String decrypt(String code, String key) {
        try{
            return new String(decrypt(Base64.base64ToByteArray(code), key.getBytes(DEFAULT_INCODING)), DEFAULT_INCODING);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey sectetKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(PADDING);
        cipher.init(Cipher.DECRYPT_MODE, sectetKey, sr);
        return cipher.doFinal(src);
    }
}
