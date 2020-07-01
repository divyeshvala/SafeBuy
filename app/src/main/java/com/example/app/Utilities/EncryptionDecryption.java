package com.example.app.Utilities;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionDecryption
{
    private String key = "appefizzvisa2020";
    private String ivKey = "safebuyhackathon";

    public String encryptAES256(String value)
    {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.encodeToString(encrypted, android.util.Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "null";
    }

    public String decryptAES256(String text)
    {
        try {
            IvParameterSpec iv = new IvParameterSpec(ivKey.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decode(text, Base64.DEFAULT));
            return new String(original);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return null;
    }

}
