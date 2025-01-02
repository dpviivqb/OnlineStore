package com.comp5348.emailService.message;

import com.comp5348.emailService.config.RabbitMQConfig;
import com.comp5348.emailService.model.EmailLog;
import com.comp5348.emailService.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log
@Component
public class MessageListener {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public MessageListener(EmailService emailService) {
        this.emailService = emailService;
        this.objectMapper = new ObjectMapper();
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_REQUEST_QUEUE)
    public void handleEmailRequest(String message) {
        try {
            Map<String, Object> request = objectMapper.readValue(message, Map.class);

            Long userId = Long.valueOf(request.get("userId").toString());
            String username = request.get("username").toString();
            String subject = request.get("subject").toString();
            String content = request.get("content").toString();

            // Create EmailLog object
            EmailLog emailLog = new EmailLog(userId, username, subject, content);

            // Process email sending
            emailService.processEmail(emailLog);

            log.info("Processed email request: " + emailLog);

        } catch (Exception e) {
            log.severe("Failed to handle email request: " + e.getMessage());
        }
    }
}
