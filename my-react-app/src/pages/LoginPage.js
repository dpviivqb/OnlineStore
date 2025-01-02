import React, { useState } from 'react';
import api from '../api/api';
import { useNavigate, Link } from 'react-router-dom';

function LoginPage({ setUsername }) {
    const [usernameInput, setUsernameInput] = useState('');
    const [password, setPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        setErrorMessage('');

        try {
            const response = await api.post('/api/users/login', {
                username: usernameInput,
                password,
            });

            const token = response.data.token;
            localStorage.setItem('token', token);
            localStorage.setItem('username', usernameInput);

            setUsername(usernameInput); // 更新 App 组件中的状态

            setTimeout(() => navigate('/order'), 100);
        } catch (error) {
            setErrorMessage(error.response?.data?.message || 'Invalid credentials');
            console.error('Login failed:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div>
            <h2>Login Page</h2>
            {errorMessage && (
                <p style={{ color: 'red' }}>{errorMessage}</p>
            )}
            <form onSubmit={handleLogin}>
                <div>
                    <label>Username:</label>
                    <input
                        type="text"
                        value={usernameInput}
                        onChange={(e) => setUsernameInput(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>Password:</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit" disabled={loading}>
                    {loading ? 'Logging in...' : 'Login'}
                </button>
            </form>
            <p>
                New user? <Link to="/register">Register here</Link>
            </p>
        </div>
    );
}

export default LoginPage;
