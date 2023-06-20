package com.vj.scanservice.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class CipherUtil {

    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 128; // for AES-128


    public static byte[] hashKey(String key) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(key.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(KEY_SIZE, SecureRandom.getInstanceStrong());
        return Base64.getEncoder().encodeToString(keyGen.generateKey().getEncoded());
    }

    public static String encrypt(String value, String secretKey) throws Exception {
        byte[] hashedKey = hashKey(secretKey);
        Key key = new SecretKeySpec(hashedKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedByteValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedByteValue);
    }

    public static String decrypt(String encryptedValue, String secretKey) throws Exception {
        byte[] hashedKey = hashKey(secretKey);
        Key key = new SecretKeySpec(hashedKey, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.getDecoder().decode(encryptedValue);
        byte[] decryptedByteValue = cipher.doFinal(decryptedValue64);
        return new String(decryptedByteValue, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception{
        String key = generateKey();
        System.out.println(key);
        String saltedKey = key + "-" + "dev";
        String encryptedString = encrypt("vijayanandraj", saltedKey);
        System.out.println(encryptedString);
        String decryptedString = decrypt(encryptedString, saltedKey);
        System.out.println(decryptedString);

    }
}

