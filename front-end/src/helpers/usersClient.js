import axios from 'axios';

const usersClient = axios.create({
    baseURL: 'http://localhost:8072/',
    timeout: 10000, // 10 seconds timeout (adjust if you want)
});

usersClient.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    if (token && token.trim() !== '' && token !== 'undefined') {
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
}, (error) => {
    return Promise.reject(error);
});

usersClient.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            localStorage.removeItem('token');
        }
        return Promise.reject(error);
    }
);

export default usersClient;
