package com.kakaxicm.geekming.utils;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


/**
 * Created by yy on 2014/11/4.
 */
public class RSAUtils {


    public static int MAX_LENGTH = 10;

    public static final String DEFAULT_PUBLIC_KEY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCuTAUgGUwKcC245UdIZktrU+Xf" + "\r" +
                    "WQ/b6TOm0RONuSSSNCDq3fbTX25UnvxAQi6C46fhtquRjIoIz77MihqmMlXrSPPT" + "\r" +
                    "lO291VjmqJFwAMlMbeEhKStOpEzlAjuTd+sqh7S4AoDrgCdt88UA4pE6JUPcQ6/7" + "\r" +
                    "mgqEq9F3PyB7rmMjQQIDAQAB" + "\r";

    public static final String DEFAULT_PRIVATE_KEY =
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAoGBAK5MBSAZTApwLbjl" + "\r" +
                    "R0hmS2tT5d9ZD9vpM6bRE425JJI0IOrfblSe/EBCLoLjp+G2q5GMigjPvsyK" + "\r" +
                    "GqYyVetI89OU7b3VWOaokXAAyUxt4K06kTOUCO5N36yqHtLgCgOuAJ23zxQDi" + "\r" +
                    "kTolQ9xDr/uaCoSr0Xc/IHuBAgMBAAECgYBMAPZTMvTHsHcfo0tcNM86dBFP" + "\r" +
                    "PaT+vkg5u/4M1Okvn++iun4Bsrx0r/d3+0Av5H64Rvz8zrcts35cp" + "\r" +
                    "SYpAiThVnP+ZwlSIyb1CW7UoplhjFAj2vUDe6MJcxBTbcFs/Co4eokqocMWBd+zZ" + "\r" +
                    "uMRMAP6B5n5NVYI4CQJBAN9FlgrhWGCUmHfdDa3dEqvgNPNLOAaW0LGzdE" + "\r" +
                    "epSXANISYP/j2LkYoZ32if+7c16jQC6xPkc3yhGSINcCQQDH2J9Eg4qR07JNBLY6" + "\r" +
                    "Jdkp0y8bqnVCAnWhFww/FenaMLVIPyg9kGBy1xtPxVCUoioJXVYYe+S0CcRIu" + "\r" +
                    "TCGnAkBELHAuMtBnCsr9AXdWf0uYeSvf0UhBpG64HI6UYB7ISl2Pf2gdvMTnfX/Q" + "\r" +
                    "TMgUfpH2hxscG4beevpELrHBmbfAmlyjIKuuE9d9qGnUS3PCmsDJaUgvzC3U" + "\r" +
                    "J/qCdhP6DzFXgw3ajeGYI8tQGcQbHxPMo9Tguo6Py0rSTkmCLoqVjwJAEUXJfO48" + "\r" +
                    "Stv1pXrfIh04KFIoS+wPxLHPAIvY/oQvrLzegQKqzd+qh4q/5J2Nascbkhqlmklt" + "\r" +
                    "ffQukz6bfTqOQg==" + "\r";


//    public static final String DEFAULT_PRIVATE_KEY_DICTIONARY =
//            "MIICWwIBAAKBgQDMEvaVZ6o9ZxVsBizajEV+1WLh5TzkLr1jCzyhm+z1h6fbDWQl" + "\r" +
//                    "ph74bZExtuT+PJZz5pnoBAZua2/86gO7di/FBsWsNLGPl6FFTbKpCly2ovz8k6kR" + "\r" +
//                    "bTyuqcI/S0IMXQA43evlnNTzxSO4SLTKySlUreFE5kVHtqX3RfJwerwG3QIDAQAB" + "\r" +
//                    "AoGAbf9mtD9tTq2KqXBmquJi7yubogFLIf9ji8hZxQ5j2jBltLfyHCz/ko6UbYSU" + "\r" +
//                    "x8jmm9BXcu0AfnIgqeWFX30j34uiesyyIdkAnLejwFTjqbva4rGjIW+fcuSI+KcZ" + "\r" +
//                    "H8Bc092yTHXDH4d7VICuHrEq0qpdGqOlK0Ji6h4t++IxgG0CQQDyfLw3glGikcqd" + "\r" +
//                    "5yo5mStLRk9yD66AhLGpAXg4OYkHyw4DuWjgC3oy4B9GiyqV9iNwNpJQ+G5E6KGS" + "\r" +
//                    "TKPrsiV7AkEA13I8I52nO7yvNC33u/F6fVe8mYuftP17s8Tr6uTO5MnD62SkfndF" + "\r" +
//                    "KB5OBhQmdoN37xbdvaj7KRgvyOja4n/ZhwJAbvWfFvN+MZrvEYRnMQYi5oYc8n+0" + "\r" +
//                    "hUCzHzf58Bh72OIU3qVoFSWLXF5sUVZ7PTXtj3qZR432v3eH/68sTz0sowJAGHVP" + "\r" +
//                    "fnuXUKDLAxF03lE7pp8fOg8vwkxEjtP8NF5lXnPJWh/CzNidcUB/exEujKXAJ9Rg" + "\r" +
//                    "t/dHDBNC2XwV3UDGYQJASCHwZSsznjGcQDTDpE7NwXhkrgNgt4+KnmSxRAS/JK9E" + "\r" +
//                    "AXLn8HjFircQSGHUZTOJcTcm0fBpvh0Dq/FQnaRhuA==" + "\r" ;


    public static final String DEFAULT_PRIVATE_KEY_DICTIONARY =
            "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANzqNZlWUOzT7t3r" + "\r" +
                    "qeKyTR6salBqE2KSqAA0KXVhZ8eFeda6O/BbaR+ejb/sJjKf3/Xj5RfLvPXLlBOR" + "\r" +
                    "l4vC+1fPxTuMB651hrXJS7WxWmMPXHeAyd2ewXrU5bFWiytrKN6t8Sx9+TyI8TxO" + "\r" +
                    "aY3PXtRzKWaLRZwPt/7MAm2oGi7HAgMBAAECgYB247zwxtlKe0xENn35OMf0SAn3" + "\r" +
                    "OP8xv4eWhHwDZeZ6JEVL4HI9ipRqpePXc/miqRPYu0shoU4SYftRBLuwRcEJooax" + "\r" +
                    "5hGnd310/tuZ2Rp4ZmY7gh/PhWx2hiqyrwEwoDx84quKmcFQbzQYqZtAXDqdpGff" + "\r" +
                    "F21wETrhoI3ViMAxcQJBAP5E2ieRsAnJLGS+Wigmxj1xES6iodLaVlMQQunjzXrK" + "\r" +
                    "lQiNIVvnORHzkzWEp1y/mGgeTFOFtzHSFqpLRm+YgxsCQQDeazoITGDOVJBQndfh" + "\r" +
                    "QcUVK5e7l5gzO2lprwsW7ystuMpbMYQ8TW21O/AKvbhcgZP94V7xwuNaKu1KCCtt" + "\r" +
                    "IZHFAkBYBT+72OwmxxRuH5MYOSwBEwYOrc/1LQ4bHRL84pY8PqrO1gRJS0bvVvGL" + "\r" +
                    "4ksJiW3aBTsI4s4jGgYsl3hfo10nAkEAoR7F/Erv+afSn0OophoR3cMleJRZggtw" + "\r" +
                    "0kkmXGfHPKjXlfYp19EzPw17VxyWWBTaExjWwYWvUps0J+QBKV4fDQJAHPsL+h/c" + "\r" +
                    "CzyLrPIyo560l9mFmky/3hJnNUv4dwnaco3qI0i6I3wcKJNTaRZn0UC69G0eqygd" + "\r" +
                    "6fFd+lwWWV3dJA==" + "\r";


//    public static final String DEFAULT_PUBLIC_KEY_DICTIONARY =
//            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMEvaVZ6o9ZxVsBizajEV+1WLh" + "\r" +
//            "5TzkLr1jCzyhm+z1h6fbDWQlph74bZExtuT+PJZz5pnoBAZua2/86gO7di/FBsWs" + "\r" +
//            "NLGPl6FFTbKpCly2ovz8k6kRbTyuqcI/S0IMXQA43evlnNTzxSO4SLTKySlUreFE" + "\r" +
//            "5kVHtqX3RfJwerwG3QIDAQAB" + "\r" ;

    public static final String DEFAULT_PUBLIC_KEY_DICTIONARY =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDc6jWZVlDs0+7d66nisk0erGpQ" + "\r" +
                    "ahNikqgANCl1YWfHhXnWujvwW2kfno2/7CYyn9/14+UXy7z1y5QTkZeLwvtXz8U7" + "\r" +
                    "jAeudYa1yUu1sVpjD1x3gMndnsF61OWxVosrayjerfEsffk8iPE8TmmNz17Ucylm" + "\r" +
                    "i0WcD7f+zAJtqBouxwIDAQAB" + "\r";


    /**
     * 私钥
     */
    private RSAPrivateKey privateKey;

    /**
     * 公钥
     */
    private RSAPublicKey publicKey;

    /**
     * 字节数据转字符串专用集合
     */
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};


    /**
     * 获取私钥
     *
     * @return 当前的私钥对象
     */
    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    /**
     * 获取公钥
     *
     * @return 当前的公钥对象
     */
    public RSAPublicKey getPublicKey() {
        return publicKey;
    }


    //测试字符串
    public static final String encryptStr =
            "1111111111111111111111111111111111111111111111111" +
                    "222222222222222222222222222222222222222222222222222" +
                    "333333333333333333333333333333333333333333333333333" +
                    "444444444444444444444444444444444444444444444444444" +
                    "555555555555555555555555555555555555555555555555555" +
                    "666666666666666666666666666666666666666666666666666" +
                    "777777777777777777777777777777777777777777777777777";

    public static void testRSA(String str) {


        RSAUtils rsaEncrypt = new RSAUtils();
        rsaEncrypt.genKeyPair();

        //加载公钥
        try {
            rsaEncrypt.loadPublicKey(RSAUtils.DEFAULT_PUBLIC_KEY);
            System.out.println("加载公钥成功");
        } catch (Exception e) {
            LogForTest.logW(e.getMessage());
            LogForTest.logW("加载公钥失败");
        }

        //加载私钥
        try {
            rsaEncrypt.loadPrivateKey(RSAUtils.DEFAULT_PRIVATE_KEY);
            LogForTest.logW("加载私钥成功");
        } catch (Exception e) {
            LogForTest.logW(e.getMessage());
            LogForTest.logW("加载私钥失败");
        }


        try {

            LogForTest.logW("加密前 : " + encryptStr);
            //加密
            byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), encryptStr.getBytes());
            //解密
            byte[] plainText = rsaEncrypt.decrypt(rsaEncrypt.getPrivateKey(), cipher);
            LogForTest.logW("密文长度:" + cipher.length);
            // System.out.println(RSAEncrypt.byteArrayToString(cipher));
            LogForTest.logW("明文长度:" + plainText.length);
            //System.out.println(RSAEncrypt.byteArrayToString(plainText));
            LogForTest.logW("解密 : " + new String(plainText));
        } catch (Exception e) {

            LogForTest.logW(e.getMessage());
            LogForTest.logE("RSA " + e + " length " + str.length());
        }

    }

    /**
     * 随机生成密钥对
     */
    public void genKeyPair() {
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
        this.publicKey = (RSAPublicKey) keyPair.getPublic();
        LogForTest.logE("privateKey:" + this.privateKey.toString());
        LogForTest.logE("publicKey:" + this.publicKey.toString());
    }

    /**
     * 从文件中输入流中加载公钥
     *
     * @param in 公钥输入流
     * @throws Exception 加载公钥时产生的异常
     */
    public void loadPublicKey(InputStream in) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            loadPublicKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("公钥数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥输入流为空");
        }
    }


    /**
     * 从字符串中加载公钥
     *
     * @param publicKeyStr 公钥数据字符串
     * @throws Exception 加载公钥时产生的异常
     */
    public void loadPublicKey(String publicKeyStr) throws Exception {
        try {
            //BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] buffer = Base64.decode(publicKeyStr, Base64.DEFAULT);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    /**
     * 从文件中加载私钥
     *
     * @param 、、keyFileName 私钥文件名
     * @return 是否成功
     * @throws Exception
     */
    public void loadPrivateKey(InputStream in) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            loadPrivateKey(sb.toString());
        } catch (IOException e) {
            throw new Exception("私钥数据读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥输入流为空");
        }
    }

    public void loadPrivateKey(String privateKeyStr) throws Exception {
        try {
            // BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] buffer = Base64.decode(privateKeyStr, Base64.DEFAULT);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

    /**
     * 加密过程
     *
     * @param publicKey     公钥
     * @param plainTextData 明文数据
     * @return
     * @throws Exception 加密过程中的异常信息
     */
    public byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws Exception {
        if (publicKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(plainTextData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
    }

    /**
     * 解密过程
     *
     * @param privateKey 私钥
     * @param cipherData 密文数据
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws Exception {
        if (privateKey == null) {
            throw new Exception("解密私钥为空, 请设置");
        }

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] output = cipher.doFinal(cipherData);
            return output;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此解密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }


    /**
     * 字节数据转十六进制字符串
     *
     * @param data 输入数据
     * @return 十六进制内容
     */
    public static String byteArrayToString(byte[] data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            //取出字节的高四位 作为索引得到相应的十六进制标识符 注意无符号右移
            stringBuilder.append(HEX_CHAR[(data[i] & 0xf0) >>> 4]);
            //取出字节的低四位 作为索引得到相应的十六进制标识符
            stringBuilder.append(HEX_CHAR[(data[i] & 0x0f)]);
            if (i < data.length - 1) {
                stringBuilder.append(' ');
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 拆分数组
     */
    public static byte[][] splitArray(byte[] data, int len) {
        int x = data.length / len;
        int y = data.length % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        byte[][] arrays = new byte[x + z][];
        byte[] arr;
        for (int i = 0; i < x + z; i++) {
            arr = new byte[len];
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * len, arr, 0, y);
            } else {
                System.arraycopy(data, i * len, arr, 0, len);
            }
            arrays[i] = arr;
        }
        return arrays;
    }

    /**
     * 拆分字符串
     */
    public static String[] splitString(String string, int len) {
        int x = string.length() / len;
        int y = string.length() % len;
        int z = 0;
        if (y != 0) {
            z = 1;
        }
        String[] strings = new String[x + z];
        String str = "";
        for (int i = 0; i < x + z; i++) {
            if (i == x + z - 1 && y != 0) {
                str = string.substring(i * len, i * len + y);
            } else {
                str = string.substring(i * len, i * len + len);
            }
            strings[i] = str;
        }
        return strings;
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, RSAPublicKey publicKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        // 模长
        int key_len = publicKey.getModulus().bitLength() / 8;
        // 加密数据长度 <= 模长-11
        String[] datas = splitString(data, key_len - 11);
        String mi = "";
        //如果明文长度大于模长-11则要分组加密
        for (String s : datas) {
            mi += new String(cipher.doFinal(s.getBytes()));
        }
        return mi;
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String decryptByPrivateKey(String data, RSAPrivateKey privateKey)
            throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        //模长
        int key_len = privateKey.getModulus().bitLength() / 20;
        byte[] bytes = data.getBytes();
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);
        System.err.println(bcd.length);
        //如果密文长度大于模长则要分组解密
        String ming = "";
        byte[][] arrays = splitArray(bcd, key_len);
        for (byte[] arr : arrays) {
            ming += new String(cipher.doFinal(arr));
        }
        return ming;
    }

    /**
     * ASCII码转BCD码
     */
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {
        byte[] bcd = new byte[asc_len / 2];
        int j = 0;
        for (int i = 0; i < (asc_len + 1) / 2; i++) {
            bcd[i] = asc_to_bcd(ascii[j++]);
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));
        }
        return bcd;
    }

    public static byte asc_to_bcd(byte asc) {
        byte bcd;

        if ((asc >= '0') && (asc <= '9'))
            bcd = (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            bcd = (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            bcd = (byte) (asc - 'a' + 10);
        else
            bcd = (byte) (asc - 48);
        return bcd;
    }


    /** *//**
     * <p>
     * RSA公钥/私钥/签名工具包
     * </p>
     * <p>
     * 罗纳德·李维斯特（Ron [R]ivest）、阿迪·萨莫尔（Adi [S]hamir）和伦纳德·阿德曼（Leonard [A]dleman）
     * </p>
     * <p>
     * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
     * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
     * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
     * </p>
     *
     * @author IceWee
     * @date 2012-4-26
     * @version 1.0
     */


    /** */
    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /** */
    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /** */
    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /** */
    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /** */
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /** */
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

//        /** *//**
//         * <p>
//         * 生成密钥对(公钥和私钥)
//         * </p>
//         *
//         * @return
//         * @throws Exception
//         */
//        public static Map<String, Object> genKeyPair() throws Exception {
//            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
//            keyPairGen.initialize(1024);
//            KeyPair keyPair = keyPairGen.generateKeyPair();
//            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
//            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
//            Map<String, Object> keyMap = new HashMap<String, Object>(2);
//            keyMap.put(PUBLIC_KEY, publicKey);
//            keyMap.put(PRIVATE_KEY, privateKey);
//            return keyMap;
//        }

    /** */
    /**
     * <p>
     * 用私钥对信息生成数字签名
     * </p>
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
//    public static String sign(byte[] data, String privateKey) throws Exception {
//        byte[] keyBytes = Base64.decode(privateKey,Base64.DEFAULT);
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
//        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
//        signature.initSign(privateK);
//        signature.update(data);
//        return Base64.encode(signature.sign());
//    }

    /** */
    /**
     * <p>
     * 校验数字签名
     * </p>
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
     * @return
     * @throws Exception
     */
//    public static boolean verify(byte[] data, String publicKey, String sign)
//            throws Exception {
//        byte[] keyBytes = Base64Utils.decode(publicKey);
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        PublicKey publicK = keyFactory.generatePublic(keySpec);
//        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
//        signature.initVerify(publicK);
//        signature.update(data);
//        return signature.verify(Base64Utils.decode(sign));
//    }

    /** */
    /**
     * <P>
     * 私钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64.decode(privateKey, Base64.DEFAULT);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /** */
    /**
     * <p>
     * 公钥解密
     * </p>
     *
     * @param encryptedData 已加密数据
     * @param publicKey     公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64.decode(publicKey, Base64.DEFAULT);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;


        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /** */
    /**
     * <p>
     * 公钥加密
     * </p>
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64.decode(publicKey, Base64.DEFAULT);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /** */
    /**
     * <p>
     * 私钥加密
     * </p>
     *
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
//    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
//            throws Exception {
//        byte[] keyBytes = Base64Utils.decode(privateKey);
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
//        Cipher cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, privateK);
//        int inputLen = data.length;
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        int offSet = 0;
//        byte[] cache;
//        int i = 0;
//        // 对数据分段加密
//        while (inputLen - offSet > 0) {
//            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
//                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
//            } else {
//                cache = cipher.doFinal(data, offSet, inputLen - offSet);
//            }
//            out.write(cache, 0, cache.length);
//            i++;
//            offSet = i * MAX_ENCRYPT_BLOCK;
//        }
//        byte[] encryptedData = out.toByteArray();
//        out.close();
//        return encryptedData;
//    }

    /** */
    /**
     * <p>
     * 获取私钥
     * </p>
     *
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
//    public static String getPrivateKey(Map<String, Object> keyMap)
//            throws Exception {
//        Key key = (Key) keyMap.get(PRIVATE_KEY);
//        return Base64Utils.encode(key.getEncoded());
//    }

    /** */
    /**
     * <p>
     * 获取公钥
     * </p>
     *
     * @param //keyMap 密钥对
     * @return
     * @throws Exception
     */
//    public static String getPublicKey(Map<String, Object> keyMap)
//            throws Exception {
//        Key key = (Key) keyMap.get(PUBLIC_KEY);
//        return Base64Utils.encode(key.getEncoded());
//    }
    public static void test() {

        try {

            //genKeyPair();

            //RSAUtils rsaEncrypt = new RSAUtils();
            LogForTest.logW("公钥加密——私钥解密");
            String source = encryptStr;

            LogForTest.logW("加密后文字：" + encrypt(encryptStr));

            LogForTest.logW("加密前文字：" + source);
            byte[] data = source.getBytes();
            byte[] encodedData = RSAUtils.encryptByPublicKey(data, DEFAULT_PUBLIC_KEY);
            LogForTest.logW("加密后文字：" + new String(encodedData));
            byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData, DEFAULT_PRIVATE_KEY);
            String target = new String(decodedData);
            LogForTest.logW("解密后文字: " + target);

        } catch (Exception e) {

            LogForTest.logE(" " + e);

        }

    }


    public static String encrypt(String str) {
        try {

            String source = str;
            LogForTest.logW("加密前文字：" + source);
            byte[] data = source.getBytes();
            byte[] encodedData = RSAUtils.encryptByPublicKey(data, DEFAULT_PUBLIC_KEY);
            //LogForTest.logW("加密后文字：" + new String(encodedData));
            LogForTest.logW("加密后文字：" + URLEncoder.encode(new String(Base64.encode(encodedData, Base64.DEFAULT))));

            return URLEncoder.encode(new String(Base64.encode(encodedData, Base64.DEFAULT)));

        } catch (Exception e) {

            LogForTest.logE("加密解密错误：" + e);

        }
        return null;
    }


    public static String decrypt(String str) {
        try {
            LogForTest.logW("解密前文字：" + str);
            String source = str; //URLDecoder.decode( str);

            byte[] encodedData = Base64.decode(source.getBytes(), Base64.DEFAULT);
            byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData, DEFAULT_PRIVATE_KEY);
            LogForTest.logW("解密后文字：" + new String(decodedData));
            //LogForTest.logW("解密后文字：" + new String(Base64.encode(encodedData)));

            return new String(decodedData);

        } catch (Exception e) {

            LogForTest.logE("加密解密错误：" + e);

        }
        return null;
    }


    public static String decryptURLDecoder(String str) {
        try {
            LogForTest.logW("解密前文字：" + str);
            String source = URLDecoder.decode(str);

            byte[] encodedData = Base64.decode(source.getBytes(), Base64.DEFAULT);
            byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData, DEFAULT_PRIVATE_KEY);
            LogForTest.logW("解密后文字：" + new String(decodedData));
            //LogForTest.logW("解密后文字：" + new String(Base64.encode(encodedData)));

            return new String(decodedData);

        } catch (Exception e) {

            LogForTest.logE("加密解密错误：" + e);

        }
        return null;
    }


    public static String decryptDictionary(String str) {
        try {
            LogForTest.logW("解密前文字：" + str);
            String source = str; //URLDecoder.decode( str);

            byte[] encodedData = Base64.decode(source.getBytes(), Base64.DEFAULT);
            byte[] decodedData = RSAUtils.decryptByPrivateKey(encodedData, DEFAULT_PRIVATE_KEY_DICTIONARY);
            LogForTest.logW("解密后文字：" + new String(decodedData));
            //LogForTest.logW("解密后文字：" + new String(Base64.encode(encodedData)));

            return new String(decodedData);

        } catch (Exception e) {

            LogForTest.logE("加密解密错误：" + e);

        }
        return null;
    }


    public static String encryptDictionary(String str) {
        try {

            String source = str;
            LogForTest.logW("加密前文字：" + source);
            byte[] data = source.getBytes();
            byte[] encodedData = RSAUtils.encryptByPublicKey(data, DEFAULT_PUBLIC_KEY_DICTIONARY);
            //LogForTest.logW("加密后文字：" + new String(encodedData));
            LogForTest.logW("加密后文字：" + URLEncoder.encode(new String(Base64.encode(encodedData, Base64.DEFAULT))));

            return URLEncoder.encode(new String(Base64.encode(encodedData, Base64.DEFAULT)));

        } catch (Exception e) {

            LogForTest.logE("加密解密错误：" + e);

        }
        return null;
    }
}




