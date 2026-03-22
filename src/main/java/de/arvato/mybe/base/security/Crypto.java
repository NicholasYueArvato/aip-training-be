package de.arvato.mybe.base.security;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

public class Crypto
{
    private Cipher cipher;

    public Crypto(int mode, String password) throws Exception
    {
        // create byte-Array
        byte[] key = (password).getBytes(StandardCharsets.UTF_8);
        // create Hash value from Array with MD5
        MessageDigest sha = MessageDigest.getInstance("MD5");
        key = sha.digest(key);
        // use the first 128 Bits
        key = Arrays.copyOf(key, 16);
        // Key
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

        cipher = Cipher.getInstance("AES");
        cipher.init(mode, secretKeySpec);
    }


    public String encrypt(String plaintext) throws Exception
    {
        Base64.Encoder base64Encoder = Base64.getEncoder();

        // Encrypt
        byte[] encrypted = cipher.doFinal(plaintext.getBytes());

        // convert bytes to Base64 for transfer
        return base64Encoder.encodeToString(encrypted);
    }

    public String decrypt(String encrypted) throws Exception
    {
        Base64.Decoder base64Decoder = Base64.getDecoder();

        // convert BASE64 String to Byte-Array
        byte[] encryptedRaw = base64Decoder.decode(encrypted);

        byte[] decryptedData = cipher.doFinal(encryptedRaw);

        String decryptedString = new String(decryptedData);

        return decryptedString;
    }
}
