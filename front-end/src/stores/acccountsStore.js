import { ref } from "vue";
import client from "@/helpers/client.js";
import { defineStore } from "pinia";

export const useAccountsStore = defineStore('accounts', () => {
    const accounts = ref([]);
    const accountTypes = ref([]);
    const error = ref(null);

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
        error.value = null;
        try {
            const url = `/accounts-service/api/accounts/get/user-accounts?page=${page}&size=${size}`;
            const response = await client.get(url);

            if (response.status === 204 || !response.data || response.data.content?.length === 0) {
                accounts.value = [];
                totalAccounts.value = 0;
                totalPages.value = 0;
                currentPage.value = 0;
            } else {
                accounts.value = response.data.content || [];
                totalAccounts.value = response.data.totalElements || 0;
                totalPages.value = response.data.totalPages || 0;
                currentPage.value = response.data.number;
                pageSize.value = response.data.size;
            }
            console.log('Accounts Store: Fetched accounts data:', accounts.value);
        } catch (err) {
            console.error('Failed to fetch accounts:', err);
            accounts.value = [];
            totalAccounts.value = 0;
            totalPages.value = 0;
            currentPage.value = 0;
            error.value = (err.response?.data?.message)
                ? `Failed to fetch accounts: ${err.response.data.message}`
                : 'Failed to fetch accounts due to an unexpected error.';
        }
    };

    const fetchFilteredAccounts = async (page = currentPage.value, size = pageSize.value) => {
        error.value = null;
        try {
            const params = new URLSearchParams();

            if (filters.value.accountId) params.append('accountId', filters.value.accountId);
            if (filters.value.type) params.append('type', filters.value.type);
            if (filters.value.minBalance !== null && filters.value.minBalance !== '') params.append('minBalance', filters.value.minBalance);
            if (filters.value.maxBalance !== null && filters.value.maxBalance !== '') params.append('maxBalance', filters.value.maxBalance);
            if (filters.value.status) params.append('status', filters.value.status);

            params.append('page', page);
            params.append('size', size);

            const url = `/accounts-service/api/accounts/filter/user-accounts?${params.toString()}`;
            const response = await client.get(url);

            if (response.status === 204 || !response.data || response.data.content?.length === 0) {
                accounts.value = [];
                totalAccounts.value = 0;
                totalPages.value = 0;
                currentPage.value = 0;
            } else {
                accounts.value = response.data.content || [];
                totalAccounts.value = response.data.totalElements || 0;
                totalPages.value = response.data.totalPages || 0;
                currentPage.value = response.data.number;
                pageSize.value = response.data.size;
            }
            console.log('Accounts Store: Filtered accounts data:', accounts.value);
        } catch (err) {
            console.error('Failed to fetch filtered accounts:', err);
            accounts.value = [];
            totalAccounts.value = 0;
            totalPages.value = 0;
            currentPage.value = 0;
            error.value = (err.response?.data?.message)
                ? `Failed to fetch filtered accounts: ${err.response.data.message}`
                : 'Failed to fetch filtered accounts due to an unexpected error.';
        }
    };

    const fetchAccountTypes = async () => {
        error.value = null;
        try {
            const response = await client.get('/accounts-service/api/accounts/get/account-types');
            accountTypes.value = response.data;
            console.log('Accounts Store: Fetched account types:', accountTypes.value);
        } catch (err) {
            console.error('Failed to fetch account types:', err);
            error.value = 'Failed to fetch account types.';
            accountTypes.value = [];
        }
    };

    const applyForNewAccount = async (accountData) => {
        error.value = null;
        const userId = localStorage.getItem('userId');
        if (!userId) {
            error.value = 'No user ID found. Please try logging in again.';
            throw new Error(error.value);
        }

        const safeData = {
            ...accountData,
            status: 'PENDING_APPROVAL',
            userId
        };

        try {
            await client.post(`/accounts-service/api/accounts/apply-for-account`, safeData);
            await fetchAccounts();
            console.log('Accounts Store: Account application successful. Refreshed accounts.');
        } catch (err) {
            console.error('Failed to create account:', err);
            error.value = err.response?.data?.message || 'Failed to create account.';
            throw new Error(error.value);
        }
    };

    const freezeAccount = async (accountId) => {
        error.value = null;
        try {
            await client.patch(`/accounts-service/api/accounts/${accountId}/freeze`);
            await fetchAccounts();
            console.log(`Accounts Store: Account ${accountId} frozen. Refreshed accounts.`);
        } catch (err) {
            console.error('Failed to freeze account:', err);
            error.value = err.response?.data?.message || 'Failed to freeze account.';
            throw new Error(error.value);
        }
    };

    const unfreezeAccount = async (accountId) => {
        error.value = null;
        try {
            await client.patch(`/accounts-service/api/accounts/${accountId}/unfreeze`);
            await fetchAccounts();
            console.log(`Accounts Store: Account ${accountId} un-frozen. Refreshed accounts.`);
        } catch (err) {
            console.error('Failed to unfreeze account:', err);
            error.value = err.response?.data?.message || 'Failed to unfreeze account.';
            throw new Error(error.value);
        }
    };

    const fetchTopAccounts = async () => {
        error.value = null;
        try {
            const response = await client.get(`/accounts-service/api/accounts/get/top-accounts`)
            accounts.value = response.data;
        } catch (err) {
            console.error('Failed to fetch top accounts:', err);
            if (err.response && err.response.data && err.response.data.message) {
                error.value = `Failed to fetch top accounts: ${err.response.data.message}`;
            } else {
                error.value = 'Failed to fetch top accounts due to an unexpected error.';
            }
        }
    }

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
