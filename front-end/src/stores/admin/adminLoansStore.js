import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import client from '@/helpers/client.js'

export const useAdminLoansStore = defineStore('adminLoansStore', () => {
    const loans = ref([])
    const totalPages = ref(0)
    const totalElements = ref(0)
    const currentPage = ref(0)
    const error = ref(null)

    const paginatedLoans = computed(() => loans.value)

    async function fetchFilteredLoans(filters, page = currentPage.value, size = 12) {
        error.value = null

        try {
            const params = {
                ...filters,
                page,
                size,
            }

            const response = await client.get('/admin-service/api/loans/filter/loans', {
                params,
            })

            console.log(response)
            loans.value = response.data.content || []
            totalPages.value = response.data.totalPages
            totalElements.value = response.data.totalElements
            currentPage.value = response.data.number
        } catch (err) {
            error.value = 'Failed to load loans.'
            loans.value = []
            totalPages.value = 0
            totalElements.value = 0
            console.error(err)
        }
    }

    function resetPage() {
        currentPage.value = 0
    }

    function incrementPage() {
        if (currentPage.value < totalPages.value - 1) {
            currentPage.value++
        }
    }

    function decrementPage() {
        if (currentPage.value > 0) {
            currentPage.value--
        }
    }

    async function acceptLoan(loanId) {
        error.value = null

        try {
            const response = await client.put(`/admin-service/api/loans/${loanId}/accept`)
            return response.data
        } catch (err) {
            if (err.response && err.response.data && err.response.data.message) {
                error.value = `Failed to accept loan: ${err.response.data.message}`
            } else if (err.response && err.response.status) {
                error.value = `Failed to accept loan. Server responded with status ${err.response.status}: ${err.response.statusText}`
            } else {
                error.value = 'Failed to accept loan due to a network error or unexpected issue.'
            }
            throw err
        }
    }

    async function rejectLoan(loanId) {
        error.value = null

        try {
            const response = await client.put(`/admin-service/api/loans/${loanId}/reject`)
            return response.data
        } catch (err) {
            if (err.response && err.response.data && err.response.data.message) {
                error.value = `Failed to reject loan: ${err.response.data.message}`
            } else if (err.response && err.response.status) {
                error.value = `Failed to reject loan. Server responded with status ${err.response.status}: ${err.response.statusText}`
            } else {
                error.value = 'Failed to reject loan due to a network error or unexpected issue.'
            }
            throw err
        }
    }

    return {
        loans,
        totalPages,
        totalElements,
        currentPage,
        error,
        fetchFilteredLoans,
        resetPage,
        incrementPage,
        decrementPage,
        paginatedLoans,
        acceptLoan,
        rejectLoan,
    }
})
