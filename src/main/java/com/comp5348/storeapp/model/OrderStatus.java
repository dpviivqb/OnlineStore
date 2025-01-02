package com.comp5348.storeapp.model;

public enum OrderStatus {
    PENDING, // Order created, waiting to be processed
    PROCESSING, // Processing (inventory or payment not completed)
    COMPLETED, // Order completed
    FAILED, // Order failed (insufficient inventory or payment failed)
    CANCELLED // Order canceled
}