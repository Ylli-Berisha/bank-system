import { ref } from 'vue'
import client from '@/helpers/client.js'
import { defineStore } from 'pinia'

export const useLoansStore = defineStore('loans', () => {
    const loans = ref([])
    const error = ref(null)

    const fetchLoans = async () => {
        error.value = null
        const userId = localStorage.getItem('userId')
        if (!userId) {
            error.value = 'No user ID found. Please try logging in again.'
            return
        }

        try {
            const response = await client.get(`/transactions-service/api/loans/get/user-loans?userId=${userId}`)
            loans.value = response.data
        } catch (err) {
            console.error('Failed to fetch loans:', err)
            error.value = 'Failed to fetch loans.'
        }
    }

    return {
        loans,
        error,
        fetchLoans
    }
})
