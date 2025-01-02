package com.comp5348.deliveryCo.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class RabbitMQConfig {

    // Exchange names
    public static final String ORDER_REQUEST_EXCHANGE = "store.order.request.exchange";
    public static final String ORDER_RESPONSE_EXCHANGE = "store.order.response.exchange";

    // Queue names
    public static final String DELIVERY_REQUEST_QUEUE = "store.delivery.request.queue";
    public static final String DELIVERY_RESPONSE_QUEUE = "store.delivery.response.queue";

    // Dead letter queue and exchange
    public static final String DEAD_LETTER_EXCHANGE = "store.dlx.exchange";
    public static final String DEAD_LETTER_QUEUE = "store.deadletter.queue";

    // Routing keys
    public static final String DELIVERY_REQUEST_ROUTING_KEY = "delivery.request";
    public static final String DELIVERY_RESPONSE_ROUTING_KEY = "delivery.response";

    // Define exchanges
    @Bean
    public DirectExchange orderRequestExchange() {
        return new DirectExchange(ORDER_REQUEST_EXCHANGE);
    }

    @Bean
    public DirectExchange orderResponseExchange() {
        return new DirectExchange(ORDER_RESPONSE_EXCHANGE);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    // Define queues

    // Define the queue with dead-letter queue enabled
    @Bean
    public Queue deliveryRequestQueue() {
        return QueueBuilder.durable(DELIVERY_REQUEST_QUEUE)
                .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "deadletter")
                .withArgument("x-max-length", 10)  // Optional: Maximum number of messages in the queue
                .withArgument("x-message-ttl", 60000)  // Optional: Message time-to-live (60 seconds)
                .build();
    }

    @Bean
    public Queue deliveryResponseQueue() {
        return new Queue(DELIVERY_RESPONSE_QUEUE);
    }

    // Define the dead letter queue
    @Bean
    public Queue deadLetterQueue() {
        return new Queue(DEAD_LETTER_QUEUE, true);
    }

    // Bind queues to exchanges
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

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("deadletter");
    }
}
