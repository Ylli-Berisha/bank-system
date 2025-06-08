import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { jwtDecode } from 'jwt-decode';
import usersClient from "../helpers/usersClient.js";

export const useAuthStore = defineStore('auth', () => {
    const token = ref(localStorage.getItem('token') || null);

    const logIn = async (user) => {
        try {
            const response = await usersClient.post('/users-service/api/users/auth/login', user);
            console.log('Login response:', response.data);
            token.value = response.data.accessToken;
            localStorage.setItem('token', token.value);
            return true;
        } catch (error) {
            console.error('Login failed:', error);
            throw error;
        }
    };

    const signUp = async (user) => {
        try {
            const response = await usersClient.post('/users-service/api/users/auth/signup', user);
            console.log('Sign up response:', response.data);
            token.value = response.data.accessToken;
            localStorage.setItem('token', token.value);
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

    const loggedInUser = computed(() => {
        if (!token.value) return null;

        try {
            const decoded = jwtDecode(token.value);
            const now = Date.now() / 1000; // seconds
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
        isLoggedIn,
        loggedInUser,
    };
});
