package com.comp5348.storeapp.dto;


import com.comp5348.storeapp.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;
    private String username;
    private String email;

    // New fields for bank account information
    private Long bankCustomerId;
    private Long bankAccountId;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.bankCustomerId = user.getBankCustomerId();
        this.bankAccountId = user.getBankAccountId();
    }
}