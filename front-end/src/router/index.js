import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '@/components/LoginView.vue';
import HomeView from "@/components/HomeView.vue";
import SignUpView from "@/components/SignUpView.vue";
import AccountsView from "@/components/AccountsView.vue";
import TransactionsView from "@/components/TransactionsView.vue";
import LoansView from "@/components/LoansView.vue";

// Import the auth store
import { useAuthStore } from "@/stores/authStore.js";
import AdminHomeView from "@/components/AdminHomeView.vue";
import AdminAccountsView from "@/components/AdminAccountsView.vue";

const ROLE_USER = 'ROLE_USER';
const ROLE_ADMIN = 'ROLE_ADMIN';

const routes = [
    {
        path: '/',
        name: 'Home',
        component: HomeView,
        meta: { requiresAuth: true, requiredRole: ROLE_USER } // Added requiredRole
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
    },
    {
        path: '/accounts',
        name: 'Accounts',
        component: AccountsView,
        meta: { requiresAuth: true, requiredRole: ROLE_USER } // Added requiredRole
    },
    {
        path: '/transactions',
        name: 'Transactions',
        component: TransactionsView,
        meta: { requiresAuth: true, requiredRole: ROLE_USER } // Added requiredRole
    },
    {
        path: '/loans',
        name: 'Loans',
        component: LoansView,
        meta: {requiresAuth: true, requiredRole: ROLE_USER} // Added requiredRole
    },
    {
        path: '/admin',
        name: 'Admin',
        component: AdminHomeView,
        meta: { requiresAuth: true, requiredRole: ROLE_ADMIN }
    },
    {
        path: '/admin/accounts',
        name: 'AdminAccounts',
        component: AdminAccountsView,
        meta: { requiresAuth: true, requiredRole: ROLE_ADMIN }
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach(async (to, from, next) => { // Made async to await store actions if needed
    const authStore = useAuthStore();

    const isAuthenticated = authStore.isTokenValid;
    console.log(`Navigating to ${to.path}. Authenticated: ${isAuthenticated}`);
    if (to.meta.requiresAuth && !isAuthenticated) {
        console.warn(`Attempted access to ${to.path} without authentication. Redirecting to login.`);
        next('/login');
        return;
    }

    if ((to.path === '/login' || to.path === '/sign-up') && isAuthenticated) {
        console.log(`Authenticated user tried to access ${to.path}. Redirecting to home.`);
        next('/');
        return;
    }

    if (to.meta.requiredRole) {
        if (!authStore.userHasRole(to.meta.requiredRole)) {
            console.warn(`User does not have required role '${to.meta.requiredRole}' for ${to.path}. Redirecting.`);
            next('/');
            return;
        }
    }
    next();
});

export default router;