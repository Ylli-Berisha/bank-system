import { ref } from 'vue'
import client from '@/helpers/client.js'
import { defineStore } from 'pinia'

export const useLoansStore = defineStore('loans', () => {
    const loans = ref([])
    const totalLoans = ref(0)
    const totalPages = ref(0)
    const currentPage = ref(0)
    const pageSize = ref(10)
    const error = ref(null)
    const topActiveLoans = ref([])
    const loanTypes = ref([])
    const createError = ref(null)
    const adminLoansPage = ref(null)

    const fetchAllLoans = async (status = null, page = 0, size = 6) => {
        error.value = null

        const params = new URLSearchParams()
        params.append('page', page.toString())
        params.append('size', size.toString())

        if (status) {
            params.append('status', status)
        }

        const url = `/transactions-service/api/loans/get/user-loans?${params.toString()}`

        try {
            const response = await client.get(url)

            loans.value = response.data.content || []
            totalLoans.value = response.data.totalElements || 0
            totalPages.value = response.data.totalPages || 0
            currentPage.value = response.data.number || page
            pageSize.value = response.data.size || size

        } catch (err) {
            loans.value = []
            totalLoans.value = 0
            totalPages.value = 0
            error.value = (err.response?.data?.message)
                ? `Failed to fetch loans: ${err.response.data.message}`
                : 'Failed to fetch loans due to an unexpected error.'
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
                loans.value = response.data.content || []
                totalLoans.value = response.data.totalElements || 0
                totalPages.value = response.data.totalPages || 0
            }
        } catch (err) {
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
            error.value = (err.response?.data?.message)
                ? `Failed to fetch loan types: ${err.response.data.message}`
                : 'Failed to fetch loan types due to an unexpected error.'
        }
    }

    const fetchTopActiveLoans = async () => {
        error.value = null
        try {
            const url = `/transactions-service/api/loans/get/top-active-loans`
            const response = await client.get(url)
            if (response.status === 204) {
                topActiveLoans.value = []
            } else {
                topActiveLoans.value = response.data
            }
        } catch (err) {
            if (err.response && (err.response.status === 404 || err.response.status === 204)) {
                topActiveLoans.value = []
                error.value = 'No active loans found.'
            } else {
                error.value = 'Failed to fetch active loans. Please try again.'
            }
        }
    }

    const applyForNewLoan = async (accountId, loanApplicationDetails) => {
        createError.value = null
        try {
            const url = `/transactions-service/api/loans/apply?accountId=${accountId}`
            const response = await client.post(url, loanApplicationDetails)
            await fetchAllLoans()
            return response.data
        } catch (err) {
            createError.value = (err.response?.data?.message) || 'Failed to apply for loan due to an unexpected error.'
            throw err
        }
    }

    const acceptLoan = async (loanId) => {
        try {
            const response = await client.put(`/transactions-service/api/loans/${loanId}/accept`)
            return response.data
        } catch (err) {
            throw err
        }
    }

    const rejectLoan = async (loanId) => {
        try {
            const response = await client.put(`/transactions-service/api/loans/${loanId}/reject`)
            return response.data
        } catch (err) {
            throw err
        }
    }

    const acceptProposedChanges = async (loanId) => {
        try {
            const response = await client.put(`/transactions-service/api/loans/${loanId}/accept-changes`)
            return response.data
        } catch (err) {
            throw err
        }
    }

    const rejectProposedChanges = async (loanId) => {
        try {
            const response = await client.put(`/transactions-service/api/loans/${loanId}/reject-changes`)
            return response.data
        } catch (err) {
            throw err
        }
    }

    const filterAdminLoans = async (filters = {}, page = 0, size = 6) => {
        error.value = null
        try {
            const params = new URLSearchParams()
            for (const key in filters) {
                const value = filters[key]
                if (value !== undefined && value !== null && value !== '') {
                    params.append(key, value)
                }
            }
            params.append('page', page.toString())
            params.append('size', size.toString())
            const url = `/transactions-service/api/loans/filter/admin-loans?${params.toString()}`
            const response = await client.get(url)
            if (response.status === 204) {
                adminLoansPage.value = null
            } else {
                adminLoansPage.value = response.data
            }
        } catch (err) {
            if (err.response && (err.response.status === 404 || err.response.status === 204)) {
                adminLoansPage.value = null
                error.value = 'No loans found matching your criteria.'
            } else {
                error.value = 'Failed to fetch admin filtered loans. Please try again.'
            }
        }
    }

    return {
        loans,
        totalLoans,
        totalPages,
        currentPage,
        pageSize,
        error,
        topActiveLoans,
        loanTypes,
        createError,
        adminLoansPage,
        fetchAllLoans,
        fetchFilteredLoans,
        fetchLoanTypes,
        fetchTopActiveLoans,
        applyForNewLoan,
        acceptLoan,
        rejectLoan,
        acceptProposedChanges,
        rejectProposedChanges,
        filterAdminLoans
    }
})
