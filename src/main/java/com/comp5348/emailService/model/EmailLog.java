package com.comp5348.emailService.model;



import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
@Table(name = "email_logs")
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime emailTime;

    @Version
    private int version;

    public EmailLog(Long userId, String username, String subject, String content) {
        this.userId = userId;
        this.username = username;
        this.subject = subject;
        this.content = content;
        this.emailTime = LocalDateTime.now();
    }
}
