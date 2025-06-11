import { ref } from "vue";
import client from "@/helpers/client.js";
import { defineStore } from "pinia";
import { useAccountsStore } from '@/stores/acccountsStore.js';

export const useTransactionsStore = defineStore('transactions', () => {
    const transactions = ref([]);
    const error = ref(null);
    const loading = ref(false);

    const fetchAllTransactions = async () => {
        error.value = null;
        loading.value = true;

        const userId = localStorage.getItem('userId');
        if (!userId) {
            error.value = 'No user ID found, please try logging in again';
            loading.value = false;
            return;
        }

        try {
            const url = `/transactions-service/api/transactions/get/user-transactions?userId=${userId}`;
            const response = await client.get(url);

            if (response.status === 204) {
                transactions.value = [];
            } else {
                transactions.value = response.data;
            }
            console.log("Fetched all transactions:", transactions.value);

        } catch (err) {
            console.error('Failed to fetch all transactions:', err);
            if (err.response && (err.response.status === 404 || err.response.status === 204)) {
                transactions.value = [];
                error.value = 'No transactions found.';
            } else {
                error.value = 'Failed to fetch all transactions. Please try again.';
            }
        } finally {
            loading.value = false;
        }
    };

    const fetchFilteredTransactions = async (filters = {}) => {
        error.value = null;
        loading.value = true;

        const userId = localStorage.getItem('userId');
        if (!userId) {
            error.value = 'No user ID found, please try logging in again';
            loading.value = false;
            return;
        }

        try {
            const params = new URLSearchParams();
            params.append('userId', userId);

            for (const key in filters) {
                const value = filters[key];
                if (value !== undefined && value !== null && value !== '') {
                    params.append(key, value);
                }
            }

            const url = `/transactions-service/api/transactions/filter/user-transactions?${params.toString()}`;
            const response = await client.get(url);

            if (response.status === 204) {
                transactions.value = [];
            } else {
                transactions.value = response.data;
            }
            console.log("Fetched filtered transactions:", transactions.value);

        } catch (err) {
            console.error('Failed to fetch filtered transactions:', err);
            if (err.response && (err.response.status === 404 || err.response.status === 204)) {
                transactions.value = [];
                error.value = 'No transactions found matching your criteria.';
            } else {
                error.value = 'Failed to fetch filtered transactions. Please try again.';
            }
        } finally {
            loading.value = false;
        }
    };

    const createTransaction = async (transactionData) => {
        error.value = null;
        loading.value = true;

        try {
            const url = '/transactions-service/api/transactions/create-new';
            const response = await client.post(url, transactionData);

            transactions.value.unshift(response.data);

            console.log("Transaction created successfully:", response.data);

            const accountsStore = useAccountsStore();
            await accountsStore.fetchAccounts();

            return response.data;
        } catch (err) {
            console.error('Failed to create transaction:', err);
            if (err.response && err.response.data && err.response.data.message) {
                error.value = err.response.data.message;
            } else if (err.response && err.response.status) {
                error.value = `Error ${err.response.status}: ${err.response.statusText}`;
            } else {
                error.value = 'Failed to create transaction. Please try again.';
            }
            throw err;
        } finally {
            loading.value = false;
        }
    };

    return {
        transactions,
        error,
        loading,
        fetchAllTransactions,
        fetchFilteredTransactions,
        createTransaction,
    };
});