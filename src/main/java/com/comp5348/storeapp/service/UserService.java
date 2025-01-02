package com.comp5348.storeapp.service;

import com.comp5348.storeapp.dto.UserDTO;
import com.comp5348.storeapp.errors.UserAlreadyExistsException;
import com.comp5348.storeapp.model.User;
import com.comp5348.storeapp.repository.UserRepository;
import com.comp5348.storeapp.util.JwtTokenUtil;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtTokenUtil jwtTokenUtil;


    @Autowired
    public UserService(UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Register a new user
     */
    public UserDTO registerUser(String username, String password, String email) {
        log.info("Attempting to register user: " + username);

        if (userRepository.findByUsername(username).isPresent()) {
            log.warning("Username already exists: " + username);
            throw new UserAlreadyExistsException("Username already exists.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            log.warning("Email already exists: " + email);
            throw new UserAlreadyExistsException("Email already exists.");
        }

        // Hash the password
        String encodedPassword = passwordEncoder.encode(password);
        log.info("Password hashed for user: " + username);

        // Create and save the user
        User user = new User(username, encodedPassword, email);
        user = userRepository.save(user);
        log.info("User registered successfully: " + username);

        // Return UserDTO
        return new UserDTO(user);
    }


    /**
     * Authenticate user login
     */
    public String authenticateUser(String username, String password) {
        log.info("Authenticating user: " + username);

        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            log.warning("User not found: " + username);
            return null;
        }
        User user = userOptional.get();
        // Verify the password using BCryptPasswordEncoder
        boolean isAuthenticated = passwordEncoder.matches(password, user.getPassword());

        if (isAuthenticated) {
            log.info("User authenticated successfully: " + username);
            return jwtTokenUtil.generateToken(username);
        } else {
            log.warning("Password mismatch for user: " + username);
            return null;
        }
    }

    /**
     * Associate a bank account with a user
     */
    @Transactional
    public UserDTO linkBankAccount(String username, Long bankCustomerId, Long bankAccountId) {
        log.info("Linking bank account for user: " + username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setBankCustomerId(bankCustomerId);
        user.setBankAccountId(bankAccountId);
        userRepository.save(user);

        log.info("Bank account linked successfully for user: " + user.getUsername());
        return new UserDTO(user);
    }


}
