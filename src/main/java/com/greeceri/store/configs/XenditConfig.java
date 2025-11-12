package com.greeceri.store.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.xendit.Xendit;

@Component
public class XenditConfig implements CommandLineRunner {
    @Value("${xendit.api.key}")
    private String apiKey;

    @Override
    public void run(String... args) throws Exception {
        Xendit.apiKey = apiKey;
        
        System.out.println(">>> Xendit API Key Diinisialisasi untuk Sandbox <<<");
    }
}
