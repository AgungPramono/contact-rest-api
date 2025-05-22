package com.agung.restful;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootTest
public class KeyGenerator {

    @Test
    void generateSecret(){
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[32];
        random.nextBytes(key);
        String secretKey = Base64.getEncoder().encodeToString(key);
        System.out.println(secretKey);
    }

}
