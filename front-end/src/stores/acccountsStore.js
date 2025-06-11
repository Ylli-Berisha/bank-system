import { ref } from "vue";
import client from "@/helpers/client.js";
import { defineStore } from "pinia";

export const useAccountsStore = defineStore('accounts', () => {
    const accounts = ref([]);
    const accountTypes = ref([]);
    const error = ref(null);

    const fetchAccounts = async () => {
        error.value = null;
        const userId = localStorage.getItem('userId');
        if (!userId) {
            error.value = 'No user ID found. Please try logging in again.';
            return;
        }
        try {
            const response = await client.get(`/accounts-service/api/accounts/get/user-accounts?userId=${userId}`);
            accounts.value = response.data;
            // --- ADD THIS CONSOLE LOG ---
            console.log('Accounts Store: Fetched accounts data:', accounts.value);
            // --------------------------
        } catch (err) {
            console.error('Failed to fetch accounts:', err);
            error.value = 'Failed to fetch accounts.';
        }
    };

    const fetchAccountTypes = async () => {
        error.value = null;
        try {
            const response = await client.get('/accounts-service/api/accounts/get/account-types');
            accountTypes.value = response.data;
            console.log('Accounts Store: Fetched account types:', accountTypes.value); // Added log
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
            await fetchAccounts(); // Added to refresh accounts after creating a new one
            console.log('Accounts Store: Account application successful. Refreshed accounts.'); // Added log
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
            console.log(`Accounts Store: Account ${accountId} frozen. Refreshed accounts.`); // Added log
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
            console.log(`Accounts Store: Account ${accountId} un-frozen. Refreshed accounts.`); // Added log
        } catch (err) {
            console.error('Failed to unfreeze account:', err);
            error.value = err.response?.data?.message || 'Failed to unfreeze account.';
            throw new Error(error.value);
        }
    };


    return {
        accounts,
        accountTypes,
        error,
        fetchAccounts,
        fetchAccountTypes,
        applyForNewAccount,
        freezeAccount,
        unfreezeAccount,
    };
});