import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { jwtDecode } from 'jwt-decode';
import client from "../helpers/client.js";

export const useAuthStore = defineStore('auth', () => {
    const token = ref(localStorage.getItem('token') || null);
    const userId = ref(localStorage.getItem('userId') || null);
    const username = ref(localStorage.getItem('username') || null);

    const logIn = async (user) => {
        try {
            const response = await client.post('/users-service/api/users/auth/login', user);
            console.log('Login response:', response.data);
            token.value = response.data.accessToken;
            userId.value = response.data.userObj.id
            username.value = response.data.userObj.username;
            localStorage.setItem('userId', userId.value);
            console.log('setting token:', token.value);
            localStorage.setItem('token', token.value);
            localStorage.setItem('username', username.value);
            return true;
        } catch (error) {
            console.error('Login failed:', error);
            throw error;
        }
    };

    const signUp = async (user) => {
        try {
            const response = await client.post('/users-service/api/users/auth/signup', user);
            console.log('Sign up response:', response.data);
            token.value = response.data.accessToken;
            userId.value = response.data.userObj.id
            username.value = response.data.userObj.username;
            localStorage.setItem('userId', userId.value);
            localStorage.setItem('token', token.value);
            localStorage.setItem('username', username.value);
            return true;
        } catch (error) {
            console.error('Sign up failed:', error);
            throw error;
        }
    };

    const logOut = () => {
        token.value = null;
        localStorage.removeItem('token');
    };

    const isTokenValid = () => {
        if (!token.value) return false;
        try {
            const decoded = jwtDecode(token.value);
            const now = Date.now() / 1000;
            return decoded.exp && decoded.exp > now;
        } catch (e) {
            return false;
        }
    };

    const loggedInUser = computed(() => {
        if (!token.value) return null;
        try {
            const decoded = jwtDecode(token.value);
            const now = Date.now() / 1000;
            return decoded.exp && decoded.exp > now ? decoded : null;
        } catch (e) {
            return null;
        }
    });

    const isLoggedIn = computed(() => !!loggedInUser.value);

    return {
        token,
        logIn,
        signUp,
        logOut,
        isTokenValid,
        isLoggedIn,
        loggedInUser,
    };
});
