import React, { useState, useEffect } from 'react';
import api from '../api/api';
import { useNavigate } from 'react-router-dom';

function LinkBankAccountPage() {
    const [bankCustomerId, setBankCustomerId] = useState('');
    const [bankAccountId, setBankAccountId] = useState('');
    const username = localStorage.getItem('username');
    const navigate = useNavigate();

    useEffect(() => {
        if (!username || !localStorage.getItem('token')) {
            alert('Please log in first.');
            navigate('/login');
        }
    }, [navigate, username]);

    const handleLinkBankAccount = async (e) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('token');

            // 发送请求到后端 API
            await api.post(
                `/api/users/${username}/link-bank-account`,
                {
                    bankCustomerId,
                    bankAccountId,
                },
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            alert('Bank account linked successfully!');
            navigate('/order');
        } catch (error) {
            console.error('Failed to link bank account:', error);
            alert('Failed to link bank account: ' + (error.response?.data || error.message));
        }
    };

    return (
        <div>
            <h2>Link Bank Account</h2>
            <form onSubmit={handleLinkBankAccount}>
                <div>
                    <label>Bank Customer ID:</label>
                    <input
                        type="text"
                        value={bankCustomerId}
                        onChange={(e) => setBankCustomerId(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Bank Account ID:</label>
                    <input
                        type="text"
                        value={bankAccountId}
                        onChange={(e) => setBankAccountId(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Link Account</button>
            </form>
        </div>
    );
}

export default LinkBankAccountPage;
