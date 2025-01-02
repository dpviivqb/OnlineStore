package com.comp5348.storeapp.model;

public enum DeliveryStatus {
    PENDING,          // Awaiting delivery request
    RECEIVED_REQUEST, // Delivery request received
    PICKED_UP,        // Goods picked up from warehouse(s)
    IN_TRANSIT,       // Currently being delivered
    DELIVERED,        // Successfully delivered
    FAILED            // Delivery attempt failed
}


