package com.comp5348.storeapp.controller;

import com.comp5348.storeapp.dto.UserDTO;
import com.comp5348.storeapp.service.UserService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody CreateUserRequest request) {
        log.info("Received registration request for username: " + request.username);

        try {
            // 调用 UserService 进行注册
            UserDTO userDTO = userService.registerUser(
                    request.username, request.password, request.email
            );

            log.info("User registered successfully: " + userDTO.getUsername());
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            log.severe("User registration failed: " + e.getMessage());
            // 返回 500 状态码，并附上错误信息
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Login a user
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequest request) {
        log.info("Received login request for username: " + request.username);

        String token = userService.authenticateUser(request.username, request.password);
        if (token != null) {
            log.info("User authenticated successfully: " + request.username);

            // 将 token 包装成 JSON 格式返回
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("username", request.username);

            return ResponseEntity.ok(response);  // 返回 JSON 格式的 token 和用户名
        } else {
            log.warning("Authentication failed for username: " + request.username);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid credentials");
            return ResponseEntity.status(401).body(errorResponse); // 返回 401 和错误消息
        }
    }

    /**
     * Link a bank account to the user's account
     */
    @PostMapping("/{username}/link-bank-account")
    public ResponseEntity<?> linkBankAccount(
            @PathVariable String username,
            @RequestBody LinkBankAccountRequest request) {
        log.info("Received request to link bank account for userId: " + username);

        try {
            // Validate request
            if (request.bankCustomerId == null || request.bankAccountId == null) {
                log.warning("Missing required fields in bank account linking request");
                return ResponseEntity.badRequest().body("Missing required fields");
            }

            // Link bank account
            UserDTO userDTO = userService.linkBankAccount(username, request.bankCustomerId, request.bankAccountId);

            log.info("Bank account linked successfully for userId: " + username);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            log.severe("Failed to link bank account for userId: " + username + ". Error: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to link bank account");
        }
    }
    /**
     * Request body for user registration
     */
    public static class CreateUserRequest {
        public String username;
        public String password;
        public String email;
    }

    /**
     * Request body for user login
     */
    public static class LoginRequest {
        public String username;
        public String password;
    }

    /**
     * Request body for linking a bank account
     */
    public static class LinkBankAccountRequest {

        public Long uerId;
        public Long bankCustomerId;
        public Long bankAccountId;
    }
}
