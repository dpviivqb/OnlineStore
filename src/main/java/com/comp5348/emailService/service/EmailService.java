package com.comp5348.emailService.service;

import com.comp5348.emailService.model.EmailLog;
import com.comp5348.emailService.repository.EmailLogRepository;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Log
@Service
public class EmailService {

    private final EmailLogRepository emailLogRepository;

    public EmailService(EmailLogRepository emailLogRepository) {
        this.emailLogRepository = emailLogRepository;
    }

    public void processEmail(EmailLog emailLog) {
        // Simulate sending email (print to console)
        log.info("Sending email to: Dear " + emailLog.getUsername());
        log.info("Subject: " + emailLog.getSubject());
        log.info("Content: " + emailLog.getContent());

        // Set email time and save log
        emailLog.setEmailTime(LocalDateTime.now());
        emailLogRepository.save(emailLog);
    }
}
