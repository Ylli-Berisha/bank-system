import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import { jwtDecode } from 'jwt-decode';
import client from "../helpers/client.js";
import { apiWrapper, globalError } from '@/helpers/apiWrapper.js';

export const useAuthStore = defineStore('auth', () => {
    const token = ref(localStorage.getItem('token') || null);
    const userId = ref(localStorage.getItem('userId') || null);
    const username = ref(localStorage.getItem('username') || null);
    const error = globalError;

    const decodedToken = computed(() => {
        if (!token.value) {
            return null;
        }
        try {
            return jwtDecode(token.value);
        } catch (e) {
            console.error("Error decoding token:", e);
            return null;
        }
    });

    const logIn = async (user) => {
        const result = await apiWrapper(async () => {
            return client.post('/users-service/api/users/auth/login', user);
        }, 'Login failed.');

        if (result.success) {
            const { accessToken, userObj } = result.data;
            token.value = accessToken;
            userId.value = userObj.id;
            username.value = userObj.username;
            localStorage.setItem('token', accessToken);
            localStorage.setItem('userId', userObj.id);
            localStorage.setItem('username', userObj.username);
        }
        return result;
    };

    const signUp = async (user) => {
        const result = await apiWrapper(async () => {
            return client.post('/users-service/api/users/auth/signup', user);
        }, 'Sign up failed.');

        if (result.success) {
            const { accessToken, userObj } = result.data;
            token.value = accessToken;
            userId.value = userObj.id;
            username.value = userObj.username;
            localStorage.setItem('token', accessToken);
            localStorage.setItem('userId', userObj.id);
            localStorage.setItem('username', userObj.username);
        }
        return result;
    };

    const logOut = () => {
        token.value = null;
        userId.value = null;
        username.value = null;
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        localStorage.removeItem('username');
    };

    const isTokenValid = computed(() => {
        if (!token.value || !decodedToken.value) {
            return false;
        }
        try {
            const now = Date.now() / 1000;
            return decodedToken.value.exp > now;
        } catch (e) {
            return false;
        }
    });

    const activeUserPayload = computed(() => {
        if (!isTokenValid.value) {
            return null;
        }
        return decodedToken.value;
    });

    const isLoggedIn = computed(() => {
        return !!activeUserPayload.value;
    });

    const userHasRole = (roleName) => {
        if (!isTokenValid.value || !decodedToken.value) {
            return false;
        }
        const roles = decodedToken.value.roles || [];
        return roles.includes(roleName);
    };

    const getUserRoles = computed(() => {
        if (!isTokenValid.value || !decodedToken.value) {
            return [];
        }
        return decodedToken.value.roles || [];
    });

    return {
        token,
        userId,
        username,
        error,
        logIn,
        signUp,
        logOut,
        isTokenValid,
        isLoggedIn,
        activeUserPayload,
        userHasRole,
        getUserRoles,
    };
});