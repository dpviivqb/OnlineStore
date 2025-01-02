import React, { useState } from 'react';
import api from '../api/api';

function CancelOrderPage() {
    const [orderId, setOrderId] = useState('');

    const handleCancelOrder = async (e) => {
        e.preventDefault();
        try {
            const response = await api.put(`/api/orders/${orderId}/cancel`);
            alert(response.data);
        } catch (error) {
            console.error('Failed to cancel order:', error);
            alert('Failed to cancel order: ' + (error.response?.data || error.message));
        }
    };

    return (
        <div>
            <h2>Cancel Order Page</h2>
            <form onSubmit={handleCancelOrder}>
                <div>
                    <label>Order ID:</label>
                    <input
                        type="text"
                        value={orderId}
                        onChange={(e) => setOrderId(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Cancel Order</button>
            </form>
        </div>
    );
}

export default CancelOrderPage;
