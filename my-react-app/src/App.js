import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';

import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import OrderPage from './pages/OrderPage';
import OrderStatusPage from './pages/OrderStatusPage';
import CancelOrderPage from './pages/CancelOrderPage';
import LinkBankAccountPage from './pages/LinkBankAccountPage';

function App() {
    const [username, setUsername] = useState(localStorage.getItem('username') || null);

    useEffect(() => {
        const storedUsername = localStorage.getItem('username');
        if (storedUsername) {
            setUsername(storedUsername);
        }
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('username');
        localStorage.removeItem('token');
        setUsername(null);
    };

    return (
        <Router>
            <div>
                {username && (
                    <nav>
                        <a href="/order">Order</a> |{' '}
                        <a href="/order-status">Order Status</a> |{' '}
                        <a href="/cancel-order">Cancel Order</a> |{' '}
                        <a href="/link-bank-account">Link Bank Account</a> |{' '}
                        <button onClick={handleLogout}>Logout</button>
                    </nav>
                )}
                <Routes>
                    <Route path="/" element={username ? <Navigate to="/order" /> : <Navigate to="/login" />} />
                    <Route path="/login" element={<LoginPage setUsername={setUsername} />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route
                        path="/order"
                        element={username ? <OrderPage /> : <Navigate to="/login" />}
                    />
                    <Route
                        path="/order-status"
                        element={username ? <OrderStatusPage /> : <Navigate to="/login" />}
                    />
                    <Route
                        path="/cancel-order"
                        element={username ? <CancelOrderPage /> : <Navigate to="/login" />}
                    />
                    <Route
                        path="/link-bank-account"
                        element={username ? <LinkBankAccountPage /> : <Navigate to="/login" />}
                    />
                    <Route path="*" element={<h2>Page not found</h2>} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
