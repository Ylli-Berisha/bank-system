import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '@/components/LoginView.vue';
import HomeView from "@/components/HomeView.vue";
import SignUpView from "@/components/SignUpView.vue";
import {useAuthStore} from "@/stores/authStore.js";

const routes = [
    {
        path: '/',
        name: 'Home',
        component: HomeView,
        meta: { requiresAuth: true }
    },
    {
        path: '/login',
        name: 'Login',
        component: LoginView,
        meta: { requiresAuth: false }
    },
    {
        path: '/sign-up',
        name: 'SignUp',
        component: SignUpView,
        meta: { requiresAuth: false }
    }

];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to, from, next) => {
    const storedToken = localStorage.getItem('token');
    let token = null;
    try {
        if (!storedToken || storedToken === '' || storedToken === 'undefined') {
            console.warn('No valid token found in localStorage');
        }else {
            token = storedToken;
        }
    } catch (error) {
        console.error('Failed to parse token:', error);
    }

    if (to.meta.requiresAuth && (!token || !useAuthStore().isTokenValid())) {
        next('/login');
    } else if ((to.path === '/login' || to.path === '/sign-up') && token) {
        next('/');
    }
    else {
        next();
    }
});

export default router;
