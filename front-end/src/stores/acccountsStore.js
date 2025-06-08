import {ref} from "vue";
import client from "@/helpers/client.js";
import {defineStore} from "pinia";

export const useAccountsStore = defineStore('accounts', () => {
    const accounts = ref([]);
    const error = ref(null);

    const fetchAccounts = async () => {
        error.value = null;
        const userId = localStorage.getItem('userId');
        if (!userId) {
            error.value = 'No user ID found please try logging in again';
            return;
        }
        try {
            const response = await client.get(`/accounts-service/api/accounts/get/user-accounts?userId=${userId}`);
            accounts.value = response.data;
            console.log(accounts.value);
        } catch (err) {
            console.error('Failed to fetch accounts:', err);
            error.value = 'Failed to fetch accounts';
        }
    };


    return {
        accounts,
        error,
        fetchAccounts
    };
});