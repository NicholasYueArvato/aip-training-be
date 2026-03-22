package de.arvato.mybe.base.security;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class CryptoServiceTest
{
    @Test
    public void testEncryptDecrypt()
    {
        CryptoService cryptoService = new CryptoService();
        try
        {
            cryptoService.init();
            String encrypted = cryptoService.encrypt("TestEncryption");
            log.info("encrypted:"+encrypted);
            String decrypted = cryptoService.decrypt(encrypted);
            log.info("decrypted:"+decrypted);

            Assertions.assertEquals("TestEncryption", decrypted);




        } catch (Exception e) {
            log.error("Failed to crypt", e);
            Assertions.fail("Failed crypt");
        }
    }
}
