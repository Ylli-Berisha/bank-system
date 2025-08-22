import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import client from '@/helpers/client.js'
import { apiWrapper, globalError } from '@/helpers/apiWrapper.js'

export const useAdminLoansStore = defineStore('adminLoansStore', () => {
    const loans = ref([])
    const totalPages = ref(0)
    const totalElements = ref(0)
    const currentPage = ref(0)
    const pendingLoans = ref([])
    const pendingTotalPages = ref(0)
    const pendingTotalElements = ref(0)
    const pendingCurrentPage = ref(0)
    const error = globalError

    const paginatedLoans = computed(() => loans.value)

    async function fetchFilteredLoans(filters, page = currentPage.value, size = 12) {
        const result = await apiWrapper(async () => {
            const params = {
                ...filters,
                page,
                size,
            }
            return client.get('/admin-service/api/loans/filter/loans', { params })
        }, 'Failed to load loans.')

        if (result.success) {
            loans.value = result.data.content || []
            totalPages.value = result.data.totalPages
            totalElements.value = result.data.totalElements
            currentPage.value = result.data.number
        } else {
            loans.value = []
            totalPages.value = 0
            totalElements.value = 0
        }
    }

    async function fetchPendingLoans(page = pendingCurrentPage.value, size = 12) {
        const status = 'PENDING'

        const result = await apiWrapper(async () => {
            return client.get('/admin-service/api/loans/filter/loans', {
                params: { status, page, size },
            })
        }, 'Failed to fetch pending loans.')

        if (result.success) {
            const responseData = result.data
            if (!responseData || responseData.content.length === 0) {
                pendingLoans.value = []
                pendingTotalPages.value = 0
                pendingTotalElements.value = 0
                pendingCurrentPage.value = 0
            } else {
                pendingLoans.value = responseData.content || []
                pendingTotalPages.value = responseData.totalPages
                pendingTotalElements.value = responseData.totalElements
                pendingCurrentPage.value = responseData.number
            }
        } else {
            pendingLoans.value = []
            pendingTotalPages.value = 0
            pendingTotalElements.value = 0
            pendingCurrentPage.value = 0
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
        const result = await apiWrapper(async () => {
            return client.put(`/admin-service/api/loans/${loanId}/accept`)
        }, 'Failed to accept loan.')

        return result.success
    }

    async function rejectLoan(loanId) {
        const result = await apiWrapper(async () => {
            return client.put(`/admin-service/api/loans/${loanId}/reject`)
        }, 'Failed to reject loan.')

        return result.success
    }

    async function proposeChanges(loanId, proposedAmount, proposedInterestRate, proposedTermInMonths) {
        const payload = {
            proposedAmount,
            proposedInterestRate,
            proposedTermInMonths,
        }

        const result = await apiWrapper(async () => {
            return client.put(`/admin-service/api/loans/${loanId}/propose-changes`, payload)
        }, 'Failed to propose changes.')

        return result.success
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