package com.comp5348.deliveryCo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

@PropertySource("classpath:application-deliveryCo.properties")
@SpringBootApplication(scanBasePackages = "com.comp5348.deliveryCo")
public class DeliveryCoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryCoApplication.class, args);
    }
}
