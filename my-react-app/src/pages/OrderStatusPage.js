import React, { useState } from 'react';
import api from '../api/api';

function OrderStatusPage() {
    const [orderId, setOrderId] = useState('');
    const [order, setOrder] = useState(null);

    const handleCheckStatus = async (e) => {
        e.preventDefault();
        try {
            const response = await api.get(`/api/orders/${orderId}`);
            setOrder(response.data);
        } catch (error) {
            console.error('Failed to fetch order status:', error);
            alert('Failed to fetch order status');
        }
    };

    return (
        <div>
            <h2>Order Status Page</h2>
            <form onSubmit={handleCheckStatus}>
                <div>
                    <label>Order ID:</label>
                    <input
                        type="text"
                        value={orderId}
                        onChange={(e) => setOrderId(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Check Status</button>
            </form>
            {order && (
                <div>
                    <h3>Order Details</h3>
                    <p>Order ID: {order.id}</p>
                    <p>Product ID: {order.productId}</p>
                    <p>Quantity: {order.quantity}</p>
                    <p>Total Amount: ${order.totalAmount}</p>
                    <p>Order Status: {order.orderStatus}</p>
                    <p>Payment Status: {order.paymentStatus}</p>
                    <p>Delivery Status: {order.deliveryStatus}</p>
                </div>
            )}
        </div>
    );
}

export default OrderStatusPage;
