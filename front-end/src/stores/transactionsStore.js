import { ref } from "vue";
import client from "@/helpers/client.js";
import { defineStore } from "pinia";
import { useAccountsStore } from '@/stores/acccountsStore.js';

export const useTransactionsStore = defineStore('transactions', () => {
    const transactions = ref([]);
    const topTransactions = ref([]);
    const error = ref(null);
    const loading = ref(false);
    const loadingTop = ref(false);

    const totalTransactions = ref(0);
    const totalPages = ref(0);
    const currentPage = ref(0);
    const pageSize = ref(6);

    const fetchTransactions = async (filters = {}) => {
        error.value = null;
        loading.value = true;

        const params = new URLSearchParams();
        params.append('page', currentPage.value.toString());
        params.append('size', pageSize.value.toString());

        for (const key in filters) {
            const value = filters[key];
            if (value !== undefined && value !== null && value !== '') {
                params.append(key, value);
            }
        }

        const url = `/transactions-service/api/transactions/get/user-transactions?${params.toString()}`;

        try {
            const response = await client.get(url);

            if (response.status === 204 || !response.data || response.data.content?.length === 0) {
                transactions.value = [];
                totalTransactions.value = 0;
                totalPages.value = 0;
            } else {
                transactions.value = response.data.content || [];
                totalTransactions.value = response.data.totalElements || 0;
                totalPages.value = response.data.totalPages || 0;
                currentPage.value = response.data.number || currentPage.value;
                pageSize.value = response.data.size || pageSize.value;
            }
        } catch (err) {
            console.error('Failed to fetch transactions (consolidated):', err);

            transactions.value = [];
            totalTransactions.value = 0;
            totalPages.value = 0;
            error.value = (err.response?.data?.message)
                ? `Failed to fetch transactions: ${err.response.data.message}`
                : 'Failed to fetch transactions due to an unexpected error.';
        } finally {
            loading.value = false;
        }
    };

    const fetchTopUserTransactions = async () => {
        error.value = null;
        loadingTop.value = true;

        try {
            const url = `/transactions-service/api/transactions/get/top-user-transactions`;
            const response = await client.get(url);

            if (response.status === 204) {
                topTransactions.value = [];
            } else {
                topTransactions.value = response.data;
            }
        } catch (err) {
            console.error('Failed to fetch top user transactions:', err);
            if (err.response && (err.response.status === 404 || err.response.status === 204)) {
                topTransactions.value = [];
                error.value = 'No recent transactions found.';
            } else {
                error.value = 'Failed to fetch recent transactions. Please try again.';
            }
        } finally {
            loadingTop.value = false;
        }
    };


    const createTransaction = async (transactionData) => {
        error.value = null;
        loading.value = true;

        try {
            const url = '/transactions-service/api/transactions/create-new';
            const response = await client.post(url, transactionData);

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
        topTransactions,
        error,
        loading,
        loadingTop,
        totalTransactions,
        totalPages,
        currentPage,
        pageSize,
        fetchTransactions,
        fetchTopUserTransactions,
        createTransaction,
    };
});