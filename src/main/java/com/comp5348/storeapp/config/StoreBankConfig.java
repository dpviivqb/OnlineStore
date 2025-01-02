package com.comp5348.storeapp.config;

import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Log
@Component
@Getter
public class StoreBankConfig {

    private Long storeCustomerId;
    private Long storeAccountId;

    public StoreBankConfig() {

        this.storeCustomerId = 4L; // Replace with the actual value from the banking application
        this.storeAccountId = 4L;  // Replace with the actual value from the banking application

        log.info("StoreBankConfig initialized with storeCustomerId=" + storeCustomerId + ", storeAccountId=" + storeAccountId);
    }
}
