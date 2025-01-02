package com.comp5348.bank;

import com.comp5348.bank.model.Account;
import com.comp5348.bank.model.Customer;
import com.comp5348.bank.repository.AccountRepository;
import com.comp5348.bank.repository.CustomerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:application-bank.properties")
@SpringBootApplication(scanBasePackages = "com.comp5348.bank")
public class BankApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankApplication.class, args);
    }

}