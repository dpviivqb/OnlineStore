package com.comp5348.emailService.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange name (consistent with StoreApp)
    public static final String EMAIL_REQUEST_EXCHANGE = "store.email.request.exchange";

    // Queue name
    public static final String EMAIL_REQUEST_QUEUE = "store.email.request.queue";

    // Routing key
    public static final String EMAIL_REQUEST_ROUTING_KEY = "email.request";

    // Define the email request exchange
    @Bean
    public DirectExchange emailRequestExchange() {
        return new DirectExchange(EMAIL_REQUEST_EXCHANGE);
    }

    // Define the email request queue
    @Bean
    public Queue emailRequestQueue() {
        return new Queue(EMAIL_REQUEST_QUEUE, true);
    }

    // Bind the email request queue to the exchange
    @Bean
    public Binding emailRequestBinding() {
        return BindingBuilder.bind(emailRequestQueue())
                .to(emailRequestExchange())
                .with(EMAIL_REQUEST_ROUTING_KEY);
    }
}
