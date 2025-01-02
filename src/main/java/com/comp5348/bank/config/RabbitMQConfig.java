package com.comp5348.bank.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange names (consistent with StoreApp)
    public static final String ORDER_REQUEST_EXCHANGE = "store.order.request.exchange";
    public static final String ORDER_RESPONSE_EXCHANGE = "store.order.response.exchange";

    // Queue names
    public static final String PAYMENT_REQUEST_QUEUE = "store.payment.request.queue";
    public static final String PAYMENT_RESPONSE_QUEUE = "store.payment.response.queue";
    public static final String REFUND_REQUEST_QUEUE = "store.refund.request.queue";
    public static final String REFUND_RESPONSE_QUEUE = "store.refund.response.queue";

    // Routing keys
    public static final String PAYMENT_REQUEST_ROUTING_KEY = "payment.request";
    public static final String PAYMENT_RESPONSE_ROUTING_KEY = "payment.response";
    public static final String REFUND_REQUEST_ROUTING_KEY = "refund.request";
    public static final String REFUND_RESPONSE_ROUTING_KEY = "refund.response";

    // Define request exchange
    @Bean
    public DirectExchange orderRequestExchange() {
        return new DirectExchange(ORDER_REQUEST_EXCHANGE);
    }

    // Define response exchange
    @Bean
    public DirectExchange orderResponseExchange() {
        return new DirectExchange(ORDER_RESPONSE_EXCHANGE);
    }

    // Define request queues
    @Bean
    public Queue paymentRequestQueue() {
        return new Queue(PAYMENT_REQUEST_QUEUE);
    }

    @Bean
    public Queue refundRequestQueue() {
        return new Queue(REFUND_REQUEST_QUEUE);
    }

    // Define response queues
    @Bean
    public Queue paymentResponseQueue() {
        return new Queue(PAYMENT_RESPONSE_QUEUE);
    }

    @Bean
    public Queue refundResponseQueue() {
        return new Queue(REFUND_RESPONSE_QUEUE);
    }

    // Bind request queue to request exchange
    @Bean
    public Binding paymentRequestBinding() {
        return BindingBuilder.bind(paymentRequestQueue())
                .to(orderRequestExchange())
                .with(PAYMENT_REQUEST_ROUTING_KEY);
    }

    // Bind refund request queue to request exchange
    @Bean
    public Binding refundRequestBinding() {
        return BindingBuilder.bind(refundRequestQueue())
                .to(orderRequestExchange())
                .with(REFUND_REQUEST_ROUTING_KEY);
    }

    // Bind response queue to response exchange
    @Bean
    public Binding paymentResponseBinding() {
        return BindingBuilder.bind(paymentResponseQueue())
                .to(orderResponseExchange())
                .with(PAYMENT_RESPONSE_ROUTING_KEY);
    }

    @Bean
    public Binding refundResponseBinding() {
        return BindingBuilder.bind(refundResponseQueue())
                .to(orderResponseExchange())
                .with(REFUND_RESPONSE_ROUTING_KEY);
    }
}
