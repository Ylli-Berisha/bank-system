import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { jwtDecode } from 'jwt-decode';
import client from "../helpers/client.js";

export const useAuthStore = defineStore('auth', () => {
    const token = ref(localStorage.getItem('token') || null);
    const userId = ref(localStorage.getItem('userId') || null);
    const username = ref(localStorage.getItem('username') || null);

    const decodedToken = computed(() => {
        console.log("decodedToken computed: running...");
        if (!token.value) {
            console.log("decodedToken computed: token.value is null, returning null.");
            return null;
        }
        try {
            const decoded = jwtDecode(token.value);
            console.log("decodedToken computed: successfully decoded token:", decoded);
            return decoded;
        } catch (e) {
            console.error("decodedToken computed: Error decoding token:", e);
            return null;
        }
    });

    const logIn = async (user) => {
        console.log("logIn: Attempting login...");
        try {
            const response = await client.post('/users-service/api/users/auth/login', user);
            console.log('Login response:', response.data);

            const receivedToken = response.data.accessToken;
            const receivedUserId = response.data.userObj.id;
            const receivedUsername = response.data.userObj.username;

            token.value = receivedToken; // Setting this will automatically update decodedToken computed
            userId.value = receivedUserId;
            username.value = receivedUsername;

            localStorage.setItem('token', receivedToken);
            localStorage.setItem('userId', receivedUserId);
            localStorage.setItem('username', receivedUsername);
            console.log("logIn: Token and user data set in store and localStorage.");

            return true;
        } catch (error) {
            console.error('Login failed:', error);
            throw error;
        }
    };

    const signUp = async (user) => {
        console.log("signUp: Attempting signup..." + JSON.stringify(user, null, 2));
        try {
            const response = await client.post('/users-service/api/users/auth/signup', user);
            console.log('Sign up response:', response.data);

            const receivedToken = response.data.accessToken;
            const receivedUserId = response.data.userObj.id;
            const receivedUsername = response.data.userObj.username;

            token.value = receivedToken; // Setting this will automatically update decodedToken computed
            userId.value = receivedUserId;
            username.value = receivedUsername;

            localStorage.setItem('token', receivedToken);
            localStorage.setItem('userId', receivedUserId);
            localStorage.setItem('username', receivedUsername);
            console.log("signUp: Token and user data set in store and localStorage.");

            return true;
        } catch (error) {
            console.error('Sign up failed:', error);
            throw error;
        }
    };

    const logOut = () => {
        console.log("logOut: Clearing auth data.");
        token.value = null;
        userId.value = null;
        username.value = null;
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        localStorage.removeItem('username');
    };

    const isTokenValid = computed(() => {
        console.log("isTokenValid computed: running...");
        console.log("isTokenValid computed: current token.value:", token.value);
        console.log("isTokenValid computed: current decodedToken.value:", decodedToken.value);

        if (!token.value) {
            console.log("isTokenValid computed: token.value is null/empty. Invalid.");
            return false;
        }
        if (!decodedToken.value) {
            console.log("isTokenValid computed: decodedToken.value is null/empty. Invalid.");
            return false;
        }
        try {
            const now = Date.now() / 1000;
            if (!decodedToken.value.exp) {
                console.warn("isTokenValid computed: JWT has no 'exp' (expiration) claim. Invalid.");
                return false;
            }
            if (decodedToken.value.exp <= now) {
                console.log("isTokenValid computed: Token expired. Expiry:", new Date(decodedToken.value.exp * 1000), "Now:", new Date(now * 1000));
                return false;
            }
            console.log("isTokenValid computed: Token IS VALID. Expiry:", new Date(decodedToken.value.exp * 1000), "Now:", new Date(now * 1000));
            return true;
        } catch (e) {
            console.error("isTokenValid computed: Error during expiration check:", e);
            return false;
        }
    });

    const activeUserPayload = computed(() => {
        console.log("activeUserPayload computed: running.");
        if (!isTokenValid.value) {
            console.log("activeUserPayload computed: Token is not valid, returning null.");
            return null;
        }
        console.log("activeUserPayload computed: Returning decodedToken.value:", decodedToken.value);
        return decodedToken.value;
    });

    const isLoggedIn = computed(() => {
        const status = !!activeUserPayload.value;
        console.log("isLoggedIn computed: status is", status);
        return status;
    });

    const userHasRole = (roleName) => {
        console.log(`userHasRole: Checking for role '${roleName}'.`);
        if (!isTokenValid.value || !decodedToken.value) {
            console.log("userHasRole: Token not valid or not decoded, cannot check role.");
            return false;
        }
        const roles = decodedToken.value.roles || [];
        const has = roles.includes(roleName);
        console.log(`userHasRole: User roles:`, roles, `Has role '${roleName}':`, has);
        return has;
    };

    const getUserRoles = computed(() => {
        console.log("getUserRoles computed: running.");
        if (!isTokenValid.value || !decodedToken.value) {
            console.log("getUserRoles computed: Token not valid or not decoded, returning empty array.");
            return [];
        }
        const roles = decodedToken.value.roles || [];
        console.log("getUserRoles computed: Returning roles:", roles);
        return roles;
    });

    return {
        token,
        userId,
        username,
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