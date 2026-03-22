package de.arvato.mybe.base.security;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;

@Component
public class CryptoService
{
    Crypto encrypt;
    Crypto decrypt;

    @PostConstruct
    public void init() throws Exception {
        decrypt = new Crypto(Cipher.DECRYPT_MODE, getSecret());
        encrypt = new Crypto(Cipher.ENCRYPT_MODE, getSecret());
    }

    /**
     * no good until we have proper key vault integrated.
     */
    private String getSecret()
    {
        return "PPLoXZlY7pcM2Ts14Ofj";
    }

    public String encrypt(String clearText) throws Exception {
        return encrypt.encrypt(clearText);
    }

    public String decrypt(String encryptedText) throws Exception  {
        return decrypt.decrypt(encryptedText);
    }
}
