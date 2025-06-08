import { ref } from "vue";
import client from "@/helpers/client.js";
import { defineStore } from "pinia";

export const useTransactionsStore = defineStore('transactions', () => {
    const transactions = ref([]);
    const error = ref(null);

    const fetchTransactions = async () => {
        error.value = null;
        const userId = localStorage.getItem('userId');
        if (!userId) {
            error.value = 'No user ID found, please try logging in again';
            return;
        }
        try {
            const response = await client.get(`/transactions-service/api/transactions/get/user-transactions?userId=${userId}`);
            transactions.value = response.data;
            console.log(transactions.value);
        } catch (err) {
            console.error('Failed to fetch transactions:', err);
            error.value = 'Failed to fetch transactions';
        }
    };

    return {
        transactions,
        error,
        fetchTransactions,
    };
});
