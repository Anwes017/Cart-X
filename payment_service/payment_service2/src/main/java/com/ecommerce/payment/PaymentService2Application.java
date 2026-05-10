package com.ecommerce.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class PaymentService2Application {

    public static void main(String[] args) {
        SpringApplication.run(PaymentService2Application.class, args);
    }

}
