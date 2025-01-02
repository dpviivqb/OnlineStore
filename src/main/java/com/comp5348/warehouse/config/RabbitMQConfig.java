package com.comp5348.warehouse.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Use the same exchanges, queues, and routing keys as StoreApp
    public static final String ORDER_REQUEST_EXCHANGE = "store.order.request.exchange";

    public static final String WAREHOUSE_UPDATE_QUEUE = "store.warehouse.update.queue";
    public static final String ORDER_RESPONSE_EXCHANGE = "store.order.response.exchange";
    public static final String WAREHOUSE_REQUEST_QUEUE = "store.warehouse.request.queue";
    public static final String WAREHOUSE_RESPONSE_QUEUE = "store.warehouse.response.queue";
    public static final String WAREHOUSE_REQUEST_ROUTING_KEY = "warehouse.request";
    public static final String WAREHOUSE_RESPONSE_ROUTING_KEY = "warehouse.response";

    public static final String WAREHOUSE_UPDATE_ROUTING_KEY = "warehouse.update";

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

    // Define request queue
    @Bean
    public Queue warehouseRequestQueue() {
        return new Queue(WAREHOUSE_REQUEST_QUEUE);
    }

    @Bean
    public Queue warehouseUpdateQueue() {
        return new Queue(WAREHOUSE_UPDATE_QUEUE);
    }

    // Define response queue
    @Bean
    public Queue warehouseResponseQueue() {
        return new Queue(WAREHOUSE_RESPONSE_QUEUE);
    }

    // Bind request queue to request exchange
    @Bean
    public Binding warehouseRequestBinding() {
        return BindingBuilder.bind(warehouseRequestQueue())
                .to(orderRequestExchange())
                .with(WAREHOUSE_REQUEST_ROUTING_KEY);
    }

    // Bind warehouse update queue to request exchange
    @Bean
    public Binding warehouseUpdateBinding() {
        return BindingBuilder.bind(warehouseUpdateQueue())
                .to(orderRequestExchange())
                .with(WAREHOUSE_UPDATE_ROUTING_KEY);
    }

    // Bind response queue to response exchange
    @Bean
    public Binding warehouseResponseBinding() {
        return BindingBuilder.bind(warehouseResponseQueue())
                .to(orderResponseExchange())
                .with(WAREHOUSE_RESPONSE_ROUTING_KEY);
    }
}
