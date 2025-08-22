import { ref } from "vue";
import { defineStore } from "pinia";
import client from "@/helpers/client.js";
import { apiWrapper, globalError } from "@/helpers/apiWrapper.js";

export const useAccountsStore = defineStore('accounts', () => {
    const accounts = ref([]);
    const accountTypes = ref([]);
    const error = globalError;

    const totalAccounts = ref(0);
    const totalPages = ref(0);
    const currentPage = ref(0);
    const pageSize = ref(6);

    const filters = ref({
        accountId: '',
        type: '',
        minBalance: null,
        maxBalance: null,
        status: ''
    });

    const fetchAccounts = async (page = currentPage.value, size = pageSize.value) => {
        const result = await apiWrapper(async () => {
            const url = `/accounts-service/api/accounts/get/user-accounts?page=${page}&size=${size}`;
            return client.get(url);
        }, 'Failed to fetch accounts.');

        if (result.success) {
            const data = result.data || {};
            accounts.value = data.content || [];
            totalAccounts.value = data.totalElements || 0;
            totalPages.value = data.totalPages || 0;
            currentPage.value = data.number;
            pageSize.value = data.size;
        } else {
            accounts.value = [];
            totalAccounts.value = 0;
            totalPages.value = 0;
            currentPage.value = 0;
        }
    };

    const fetchFilteredAccounts = async (page = currentPage.value, size = pageSize.value) => {
        const result = await apiWrapper(async () => {
            const params = new URLSearchParams();
            if (filters.value.accountId) params.append('accountId', filters.value.accountId);
            if (filters.value.type) params.append('type', filters.value.type);
            if (filters.value.minBalance !== null && filters.value.minBalance !== '') params.append('minBalance', filters.value.minBalance);
            if (filters.value.maxBalance !== null && filters.value.maxBalance !== '') params.append('maxBalance', filters.value.maxBalance);
            if (filters.value.status) params.append('status', filters.value.status);
            params.append('page', page);
            params.append('size', size);
            const url = `/accounts-service/api/accounts/filter/user-accounts?${params.toString()}`;
            return client.get(url);
        }, 'Failed to fetch filtered accounts.');

        if (result.success) {
            const data = result.data || {};
            accounts.value = data.content || [];
            totalAccounts.value = data.totalElements || 0;
            totalPages.value = data.totalPages || 0;
            currentPage.value = data.number;
            pageSize.value = data.size;
        } else {
            accounts.value = [];
            totalAccounts.value = 0;
            totalPages.value = 0;
            currentPage.value = 0;
        }
    };

    const fetchAccountTypes = async () => {
        const result = await apiWrapper(async () => {
            return client.get('/accounts-service/api/accounts/get/account-types');
        }, 'Failed to fetch account types.');

        if (result.success) {
            accountTypes.value = result.data || [];
        } else {
            accountTypes.value = [];
        }
    };

    const applyForNewAccount = async (accountData) => {
        const userId = localStorage.getItem('userId');
        if (!userId) {
            globalError.value = 'No user ID found. Please try logging in again.';
            return { success: false, error: globalError.value };
        }

        const safeData = {
            ...accountData,
            status: 'PENDING_APPROVAL',
            userId
        };

        const result = await apiWrapper(async () => {
            return client.post(`/accounts-service/api/accounts/apply-for-account`, safeData);
        }, 'Failed to create account.');

        if (result.success) {
            await fetchAccounts();
        }
        return result;
    };

    const freezeAccount = async (accountId) => {
        const result = await apiWrapper(async () => {
            return client.patch(`/accounts-service/api/accounts/${accountId}/freeze`);
        }, 'Failed to freeze account.');

        if (result.success) {
            await fetchAccounts();
        }
        return result;
    };

    const unfreezeAccount = async (accountId) => {
        const result = await apiWrapper(async () => {
            return client.patch(`/accounts-service/api/accounts/${accountId}/unfreeze`);
        }, 'Failed to unfreeze account.');

        if (result.success) {
            await fetchAccounts();
        }
        return result;
    };

    const fetchTopAccounts = async () => {
        const result = await apiWrapper(async () => {
            return client.get(`/accounts-service/api/accounts/get/top-accounts`);
        }, 'Failed to fetch top accounts.');

        if (result.success) {
            accounts.value = result.data;
        } else {
            accounts.value = [];
        }
    };

    return {
        accounts,
        accountTypes,
        error,
        totalAccounts,
        totalPages,
        currentPage,
        pageSize,
        filters,
        fetchAccounts,
        fetchFilteredAccounts,
        fetchAccountTypes,
        applyForNewAccount,
        freezeAccount,
        unfreezeAccount,
        fetchTopAccounts,
    };
});