import React, { useState, useEffect } from 'react';
import api from '../api/api';
import { useNavigate } from 'react-router-dom';

function OrderPage() {
    const [products, setProducts] = useState([]);
    const [selectedProductId, setSelectedProductId] = useState('');
    const [quantity, setQuantity] = useState(1);
    const username = localStorage.getItem('username');
    const navigate = useNavigate();

    useEffect(() => {
        // Fetch products from the store API
        const fetchProducts = async () => {
            try {
                const response = await api.get('/api/products');
                setProducts(response.data);
            } catch (error) {
                console.error('Failed to fetch products:', error);
            }
        };
        fetchProducts();
    }, []);

    const handleOrder = async (e) => {
        e.preventDefault();
        try {
            const response = await api.post('/api/orders', {
                username,
                productId: selectedProductId,
                quantity: parseInt(quantity),
            });
            alert(response.data);
            navigate('/order-status');
        } catch (error) {
            console.error('Order creation failed:', error);
            alert('Order creation failed: ' + (error.response?.data || error.message));
        }
    };

    return (
        <div>
            <h2>Order Page</h2>
            <form onSubmit={handleOrder}>
                <div>
                    <label>Product:</label>
                    <select
                        value={selectedProductId}
                        onChange={(e) => setSelectedProductId(e.target.value)}
                        required
                    >
                        <option value="" disabled>Select a product</option>
                        {products.map((product) => (
                            <option value={product.id} key={product.id}>
                                {product.name} - ${product.price}
                            </option>
                        ))}
                    </select>
                </div>
                <div>
                    <label>Quantity:</label>
                    <input
                        type="number"
                        min="1"
                        value={quantity}
                        onChange={(e) => setQuantity(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Place Order</button>
            </form>
        </div>
    );
}

export default OrderPage;
