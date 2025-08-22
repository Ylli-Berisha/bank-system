import { ref } from "vue";
import client from "@/helpers/client.js";
import { defineStore } from "pinia";
import { useAccountsStore } from '@/stores/acccountsStore.js';
import { apiWrapper, globalError } from '@/helpers/apiWrapper.js';

export const useTransactionsStore = defineStore('transactions', () => {
    const transactions = ref([]);
    const topTransactions = ref([]);
    const error = globalError;
    const loading = ref(false);
    const loadingTop = ref(false);

    const totalTransactions = ref(0);
    const totalPages = ref(0);
    const currentPage = ref(0);
    const pageSize = ref(6);

    const fetchTransactions = async (filters = {}) => {
        loading.value = true;
        const result = await apiWrapper(async () => {
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
            return client.get(url);
        }, 'Failed to fetch transactions.');

        if (result.success) {
            const data = result.data || {};
            transactions.value = data.content || [];
            totalTransactions.value = data.totalElements || 0;
            totalPages.value = data.totalPages || 0;
            currentPage.value = data.number || currentPage.value;
            pageSize.value = data.size || pageSize.value;
        } else {
            transactions.value = [];
            totalTransactions.value = 0;
            totalPages.value = 0;
        }
        loading.value = false;
    };

    const fetchTopUserTransactions = async () => {
        loadingTop.value = true;
        const result = await apiWrapper(async () => {
            const url = `/transactions-service/api/transactions/get/top-user-transactions`;
            return client.get(url);
        }, 'Failed to fetch recent transactions.');

        if (result.success) {
            topTransactions.value = result.data || [];
        } else {
            topTransactions.value = [];
        }
        loadingTop.value = false;
    };


    const createTransaction = async (transactionData) => {
        loading.value = true;
        const result = await apiWrapper(async () => {
            const url = '/transactions-service/api/transactions/create-new';
            const response = await client.post(url, transactionData);
            return response;
        }, 'Failed to create transaction.');

        if (result.success) {
            const accountsStore = useAccountsStore();
            await accountsStore.fetchAccounts();
        }
        loading.value = false;
        return result;
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