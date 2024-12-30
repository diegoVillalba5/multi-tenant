package com.multitenant.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.multitenant"})
public class MultiTenantApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiTenantApplication.class, args);
    }

}
