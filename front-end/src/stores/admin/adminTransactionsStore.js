import {defineStore} from 'pinia';
import {ref, computed} from 'vue';
import client from '@/helpers/client.js';
import {apiWrapper, globalError} from '@/helpers/apiWrapper.js';

export const useAdminTransactionsStore = defineStore('adminTransactionsStore', () => {
    const transactions = ref([]);
    const totalPages = ref(0);
    const totalElements = ref(0);
    const currentPage = ref(0);
    const error = globalError;

    const paginatedTransactions = computed(() => transactions.value);

    async function fetchFilteredTransactions(filters, page = currentPage.value, size = 12) {
        const result = await apiWrapper(async () => {
            const params = {
                ...filters,
                page,
                size,
            };
            return client.get('/admin-service/api/transactions/filter/transactions', {params});
        }, 'Failed to load transactions.');

        if (result.success) {
            transactions.value = result.data.content || [];
            totalPages.value = result.data.totalPages;
            totalElements.value = result.data.totalElements;
            currentPage.value = result.data.number;
        } else {
            transactions.value = [];
            totalPages.value = 0;
            totalElements.value = 0;
        }
    }

    async function revertTransaction(transactionId) {
        const result = await apiWrapper(async () => {
            const response = await client.put('/admin-service/api/transactions/revert', null, {
                params: {transactionId},
            });
            return response;
        }, 'Failed to revert transaction.');

        if (result.success) {
            const revertedTransaction = result.data;
            const index = transactions.value.findIndex(t => t.id === revertedTransaction.id);
            if (index !== -1) {
                transactions.value[index] = revertedTransaction;
            }
            return revertedTransaction;
        } else {
            return null;
        }
    }

    function resetPage() {
        currentPage.value = 0;
    }

    function incrementPage() {
        if (currentPage.value < totalPages.value - 1) {
            currentPage.value++;
        }
    }

    function decrementPage() {
        if (currentPage.value > 0) {
            currentPage.value--;
        }
    }

    return {
        transactions,
        totalPages,
        totalElements,
        currentPage,
        error,
        fetchFilteredTransactions,
        revertTransaction,
        resetPage,
        incrementPage,
        decrementPage,
        paginatedTransactions,
    };
});