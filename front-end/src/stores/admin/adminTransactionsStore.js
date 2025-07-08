import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import client from '@/helpers/client.js';

export const useAdminTransactionsStore = defineStore('adminTransactionsStore', () => {
    const transactions = ref([]);
    const totalPages = ref(0);
    const totalElements = ref(0);
    const currentPage = ref(0);
    const error = ref(null);

    const paginatedTransactions = computed(() => transactions.value);

    async function fetchFilteredTransactions(filters, page = currentPage.value, size = 12) {
        error.value = null;

        try {
            const params = {
                ...filters,
                page,
                size,
            };

            const response = await client.get('/admin-service/api/transactions/filter/transactions', {
                params,
            });

            transactions.value = response.data.content || [];
            totalPages.value = response.data.totalPages;
            totalElements.value = response.data.totalElements;
            currentPage.value = response.data.number;
        } catch (err) {
            error.value = 'Failed to load transactions.';
            transactions.value = [];
            totalPages.value = 0;
            totalElements.value = 0;
            console.error(err);
        }
    }

    async function revertTransaction(transactionId) {
        const tx = transactions.value.find(t => t.id === transactionId);
        if (!tx) {
            error.value = 'Transaction not found';
            throw new Error('Transaction not found');
        }
        if (tx.type !== 'TRANSFER' || tx.status !== 'COMPLETED') {
            error.value = 'Transaction cannot be reverted';
            throw new Error('Transaction cannot be reverted');
        }
        tx.status = 'REVERSED';
        return Promise.resolve();
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
