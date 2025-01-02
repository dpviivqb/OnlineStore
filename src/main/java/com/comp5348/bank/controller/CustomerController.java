package com.comp5348.bank.controller;

import com.comp5348.bank.dto.CustomerDTO;
import com.comp5348.bank.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CreateCustomerRequest request) {
        CustomerDTO customerDTO = customerService.createCustomer(request.firstName, request.lastName);
        return ResponseEntity.ok(customerDTO);
    }

    public static class CreateCustomerRequest {
        public String firstName;
        public String lastName;
    }

}
