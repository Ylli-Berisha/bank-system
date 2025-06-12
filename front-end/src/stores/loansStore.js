import { ref } from 'vue'
import client from '@/helpers/client.js'
import { defineStore } from 'pinia'

export const useLoansStore = defineStore('loans', () => {
    const loans = ref([])
    const error = ref(null)
    const loanTypes = ref([])
    const createError = ref(null)

    const fetchAllLoans = async (status = null) => {
        error.value = null

        let url = `/transactions-service/api/loans/get/user-loans`;
        const params = new URLSearchParams();

        if (status) {
            params.append('status', status);
        }

        if (params.toString()) {
            url += `?${params.toString()}`;
        }

        try {
            const response = await client.get(url)
            loans.value = response.data
        } catch (err) {
            console.error('Failed to fetch loans:', err)
            if (err.response && err.response.data && err.response.data.message) {
                error.value = `Failed to fetch loans: ${err.response.data.message}`
            } else {
                error.value = 'Failed to fetch loans due to an unexpected error.'
            }
        }
    }

    const fetchFilteredLoans = async (filters = {}) => {
        error.value = null
        loans.value = []
        try {
            const params = new URLSearchParams()

            for (const key in filters) {
                const value = filters[key]
                if (value !== undefined && value !== null && value !== '') {
                    params.append(key, value)
                }
            }


            const url = `/transactions-service/api/loans/filter/user-loans?${params.toString()}`
            const response = await client.get(url)

            if (response.status === 204) {
                loans.value = []
            } else {
                loans.value = response.data
            }
            console.log("Fetched filtered loans:", loans.value)

        } catch (err) {
            console.error('Failed to fetch filtered loans:', err)
            if (err.response && (err.response.status === 404 || err.response.status === 204)) {
                loans.value = []
                error.value = 'No loans found matching your criteria.'
            } else {
                error.value = 'Failed to fetch filtered loans. Please try again.'
            }
        }
    }

    const fetchLoanTypes = async () => {
        error.value = null
        try {
            const response = await client.get(`/transactions-service/api/loans/get/loan-types`)
            loanTypes.value = response.data
        } catch (err) {
            console.error('Failed to fetch loan types:', err)
            if (err.response && err.response.data && err.response.data.message) {
                error.value = `Failed to fetch loan types: ${err.response.data.message}`
            } else {
                error.value = 'Failed to fetch loan types due to an unexpected error.'
            }
        }
    }

    const applyForNewLoan = async (accountId, loanApplicationDetails) => {
        createError.value = null
        try {
            const url = `/transactions-service/api/loans/apply?accountId=${accountId}`;
            const response = await client.post(url, loanApplicationDetails);
            await fetchAllLoans();
            return response.data;
        } catch (err) {
            console.error('Failed to apply for new loan:', err);
            if (err.response && err.response.data && err.response.data.message) {
                createError.value = err.response.data.message;
            } else {
                createError.value = 'Failed to apply for loan due to an unexpected error.';
            }
            throw err;
        }
    }

    return {
        loans,
        error,
        loanTypes,
        createError,
        fetchAllLoans,
        fetchFilteredLoans,
        fetchLoanTypes,
        applyForNewLoan
    }
})