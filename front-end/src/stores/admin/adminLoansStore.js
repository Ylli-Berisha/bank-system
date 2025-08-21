import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import client from '@/helpers/client.js'

export const useAdminLoansStore = defineStore('adminLoansStore', () => {
    const loans = ref([])
    const totalPages = ref(0)
    const totalElements = ref(0)
    const currentPage = ref(0)
    const pendingLoans = ref([])
    const pendingTotalPages = ref(0)
    const pendingTotalElements = ref(0)
    const pendingCurrentPage = ref(0)
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

    async function fetchPendingLoans(page = pendingCurrentPage.value, size = 12) {
        error.value = null
        const status = 'PENDING'

        try {
            const response = await client.get('/admin-service/api/loans/filter/loans', {
                params: { status, page, size },
            })

            if (response.status === 204) {
                pendingLoans.value = []
                pendingTotalPages.value = 0
                pendingTotalElements.value = 0
                pendingCurrentPage.value = 0
            } else {
                pendingLoans.value = response.data.content || []
                pendingTotalPages.value = response.data.totalPages
                pendingTotalElements.value = response.data.totalElements
                pendingCurrentPage.value = response.data.number
            }
        } catch (err) {
            pendingLoans.value = []
            pendingTotalPages.value = 0
            pendingTotalElements.value = 0
            pendingCurrentPage.value = 0

            if (err.response && err.response.data && err.response.data.message) {
                error.value = `Failed to fetch pending loans: ${err.response.data.message}`
            } else if (err.response && err.response.status) {
                error.value = `Failed to fetch pending loans. Server responded with status ${err.response.status}: ${err.response.statusText}`
            } else {
                error.value = 'Failed to fetch pending loans due to a network error or unexpected issue.'
            }
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

    async function proposeChanges(loanId, proposedAmount, proposedInterestRate, proposedTermInMonths) {
        error.value = null

        try {
            const payload = {
                proposedAmount,
                proposedInterestRate,
                proposedTermInMonths,
            }

            const response = await client.put(`/admin-service/api/loans/${loanId}/propose-changes`, payload)
            return response.data
        } catch (err) {
            if (err.response && err.response.data && err.response.data.message) {
                error.value = `Failed to propose changes: ${err.response.data.message}`
            } else if (err.response && err.response.status) {
                error.value = `Failed to propose changes. Server responded with status ${err.response.status}: ${err.response.statusText}`
            } else {
                error.value = 'Failed to propose changes due to a network error or unexpected issue.'
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
        proposeChanges,
        pendingLoans,
        pendingTotalPages,
        pendingTotalElements,
        pendingCurrentPage,
        fetchPendingLoans,
            }
})