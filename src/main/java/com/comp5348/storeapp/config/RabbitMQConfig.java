package com.comp5348.storeapp.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Request exchanges, queues, and routing keys
    public static final String ORDER_REQUEST_EXCHANGE = "store.order.request.exchange";
    public static final String EMAIL_REQUEST_EXCHANGE = "store.email.request.exchange";
    public static final String DEAD_LETTER_EXCHANGE = "store.dlx.exchange";
    public static final String WAREHOUSE_REQUEST_QUEUE = "store.warehouse.request.queue";
    public static final String PAYMENT_REQUEST_QUEUE = "store.payment.request.queue";
    public static final String DELIVERY_REQUEST_QUEUE = "store.delivery.request.queue";
    public static final String EMAIL_REQUEST_QUEUE = "store.email.request.queue";
    public static final String REFUND_REQUEST_QUEUE = "store.refund.request.queue";
    public static final String WAREHOUSE_UPDATE_QUEUE = "store.warehouse.update.queue";
    public static final String WAREHOUSE_REQUEST_ROUTING_KEY = "warehouse.request";
    public static final String PAYMENT_REQUEST_ROUTING_KEY = "payment.request";
    public static final String DELIVERY_REQUEST_ROUTING_KEY = "delivery.request";
    public static final String EMAIL_REQUEST_ROUTING_KEY = "email.request";
    public static final String REFUND_REQUEST_ROUTING_KEY = "refund.request";
    public static final String WAREHOUSE_UPDATE_ROUTING_KEY = "warehouse.update"; // For cancel order

    // Response exchanges, queues, and routing keys
    public static final String ORDER_RESPONSE_EXCHANGE = "store.order.response.exchange";
    public static final String WAREHOUSE_RESPONSE_QUEUE = "store.warehouse.response.queue";
    public static final String PAYMENT_RESPONSE_QUEUE = "store.payment.response.queue";
    public static final String DELIVERY_RESPONSE_QUEUE = "store.delivery.response.queue";
    public static final String REFUND_RESPONSE_QUEUE = "store.refund.response.queue";
    public static final String WAREHOUSE_RESPONSE_ROUTING_KEY = "warehouse.response";
    public static final String PAYMENT_RESPONSE_ROUTING_KEY = "payment.response";
    public static final String DELIVERY_RESPONSE_ROUTING_KEY = "delivery.response";
    public static final String REFUND_RESPONSE_ROUTING_KEY = "refund.response";

    public static final String DEAD_LETTER_QUEUE = "store.deadletter.queue";
    public static final String DEAD_LETTER_ROUTING_KEY = "deadletter";

    // Define request exchanges
    @Bean
    public DirectExchange orderRequestExchange() {
        return new DirectExchange(ORDER_REQUEST_EXCHANGE);
    }

    // Define response exchanges
    @Bean
    public DirectExchange orderResponseExchange() {
        return new DirectExchange(ORDER_RESPONSE_EXCHANGE);
    }

    @Bean
    public DirectExchange emailRequestExchange() {
        return new DirectExchange(EMAIL_REQUEST_EXCHANGE);
    }

    // Define dead letter exchange
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    // Define request queues
    @Bean
    public Queue warehouseRequestQueue() {
        return new Queue(WAREHOUSE_REQUEST_QUEUE);
    }

    @Bean
    public Queue paymentRequestQueue() {
        return new Queue(PAYMENT_REQUEST_QUEUE);
    }

    @Bean
    public Queue emailRequestQueue() {
        return new Queue(EMAIL_REQUEST_QUEUE);
    }

    @Bean
    public Queue refundRequestQueue() {
        return new Queue(REFUND_REQUEST_QUEUE);
    }

    @Bean
    public Queue warehouseUpdateQueue() {
        return new Queue(WAREHOUSE_UPDATE_QUEUE);
    }

    // Define response queues
    @Bean
    public Queue warehouseResponseQueue() {
        return new Queue(WAREHOUSE_RESPONSE_QUEUE);
    }

    @Bean
    public Queue paymentResponseQueue() {
        return new Queue(PAYMENT_RESPONSE_QUEUE);
    }

    @Bean
    public Queue deliveryRequestQueue() {
        return QueueBuilder.durable(DELIVERY_REQUEST_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE) // Configure dead letter exchange
                .withArgument("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY) // Dead letter routing key
                .withArgument("x-message-ttl", 60000) // Set message TTL (60 seconds)
                .withArgument("x-max-length", 10) // Limit queue length
                .build();
    }

    @Bean
    public Queue deliveryResponseQueue() {
        return new Queue(DELIVERY_RESPONSE_QUEUE);
    }

    @Bean
    public Queue refundResponseQueue() {
        return new Queue(REFUND_RESPONSE_QUEUE);
    }

    // Define dead letter queue
    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DEAD_LETTER_QUEUE, true);
    }

    // Bind request queues to request exchanges
    @Bean
    public Binding warehouseRequestBinding() {
        return BindingBuilder.bind(warehouseRequestQueue()).to(orderRequestExchange()).with(WAREHOUSE_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding paymentRequestBinding() {
        return BindingBuilder.bind(paymentRequestQueue()).to(orderRequestExchange()).with(PAYMENT_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding emailRequestBinding() {
        return BindingBuilder.bind(emailRequestQueue())
                .to(emailRequestExchange())
                .with(EMAIL_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding refundRequestBinding() {
        return BindingBuilder.bind(refundRequestQueue())
                .to(orderRequestExchange())
                .with(REFUND_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding warehouseUpdateBinding() {
        return BindingBuilder.bind(warehouseUpdateQueue())
                .to(orderRequestExchange())
                .with(WAREHOUSE_UPDATE_ROUTING_KEY);
    }

    // Bind response queues to response exchanges
    @Bean
    public Binding warehouseResponseBinding() {
        return BindingBuilder.bind(warehouseResponseQueue()).to(orderResponseExchange()).with(WAREHOUSE_RESPONSE_ROUTING_KEY);
    }

    @Bean
    public Binding paymentResponseBinding() {
        return BindingBuilder.bind(paymentResponseQueue()).to(orderResponseExchange()).with(PAYMENT_RESPONSE_ROUTING_KEY);
    }

    @Bean
    public Binding deliveryRequestBinding() {
        return BindingBuilder.bind(deliveryRequestQueue())
                .to(orderRequestExchange())
                .with(DELIVERY_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding deliveryResponseBinding() {
        return BindingBuilder.bind(deliveryResponseQueue())
                .to(orderResponseExchange())
                .with(DELIVERY_RESPONSE_ROUTING_KEY);
    }

    // Bind refund response queue to response exchange
    @Bean
    public Binding refundResponseBinding() {
        return BindingBuilder.bind(refundResponseQueue())
                .to(orderResponseExchange())
                .with(REFUND_RESPONSE_ROUTING_KEY);
    }

    // Bind dead letter queue to dead letter exchange
    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DEAD_LETTER_ROUTING_KEY);
    }
}
