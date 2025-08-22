import { ref } from 'vue'
import { defineStore } from 'pinia'
import client from '@/helpers/client.js'
import { apiWrapper, globalError } from '@/helpers/apiWrapper.js'

export const useAdminUserCompositionStore = defineStore('adminUserComposition', () => {
    const user = ref(null)
    const error = globalError
    const loading = ref(false)

    const accounts = ref([])
    const totalAccounts = ref(0)
    const totalAccountsPages = ref(0)
    const accountsCurrentPage = ref(0)
    const accountsPageSize = ref(6)

    const transactions = ref([])
    const totalTransactions = ref(0)
    const totalTransactionsPages = ref(0)
    const transactionsCurrentPage = ref(0)
    const transactionsPageSize = ref(6)

    const loans = ref([])
    const totalLoans = ref(0)
    const totalLoansPages = ref(0)
    const loansCurrentPage = ref(0)
    const loansPageSize = ref(6)

    const getUserComposition = async (userId) => {
        loading.value = true
        const result = await apiWrapper(async () => {
            return client.get('/user/composition/get/user-composition', {
                params: { userId }
            })
        }, 'Failed to load user composition')

        if (result.success) {
            const data = result.data || {}
            user.value = data.user || null

            accounts.value = data.accounts?.content || []
            totalAccounts.value = data.accounts?.totalElements || 0
            totalAccountsPages.value = data.accounts?.totalPages || 0
            accountsCurrentPage.value = data.accounts?.number || 0
            accountsPageSize.value = data.accounts?.size || 6

            transactions.value = data.transactions?.content || []
            totalTransactions.value = data.transactions?.totalElements || 0
            totalTransactionsPages.value = data.transactions?.totalPages || 0
            transactionsCurrentPage.value = data.transactions?.number || 0
            transactionsPageSize.value = data.transactions?.size || 6

            loans.value = data.loans?.content || []
            totalLoans.value = data.loans?.totalElements || 0
            totalLoansPages.value = data.loans?.totalPages || 0
            loansCurrentPage.value = data.loans?.number || 0
            loansPageSize.value = data.loans?.size || 6
        } else {
            user.value = null
            accounts.value = []
            totalAccounts.value = 0
            totalAccountsPages.value = 0
            transactions.value = []
            totalTransactions.value = 0
            totalTransactionsPages.value = 0
            loans.value = []
            totalLoans.value = 0
            totalLoansPages.value = 0
        }
        loading.value = false
    }

    const fetchAccountsPage = async (userId, page = accountsCurrentPage.value, size = accountsPageSize.value) => {
        const result = await apiWrapper(async () => {
            return client.get('/user/composition/get/accounts-page', {
                params: { userId, page, size }
            })
        }, 'Failed to fetch accounts.')

        if (result.success) {
            const data = result.data || {}
            accounts.value = data.content || []
            totalAccounts.value = data.totalElements || 0
            totalAccountsPages.value = data.totalPages || 0
            accountsCurrentPage.value = data.number
            accountsPageSize.value = data.size
        } else {
            accounts.value = []
            totalAccounts.value = 0
            totalAccountsPages.value = 0
            accountsCurrentPage.value = 0
        }
    }

    const fetchTransactionsPage = async (userId, page = transactionsCurrentPage.value, size = transactionsPageSize.value) => {
        const result = await apiWrapper(async () => {
            return client.get('/user/composition/get/transactions-page', {
                params: { userId, page, size }
            })
        }, 'Failed to fetch transactions.')

        if (result.success) {
            const data = result.data || {}
            transactions.value = data.content || []
            totalTransactions.value = data.totalElements || 0
            totalTransactionsPages.value = data.totalPages || 0
            transactionsCurrentPage.value = data.number
            transactionsPageSize.value = data.size
        } else {
            transactions.value = []
            totalTransactions.value = 0
            totalTransactionsPages.value = 0
            transactionsCurrentPage.value = 0
        }
    }

    const fetchLoansPage = async (userId, page = loansCurrentPage.value, size = loansPageSize.value) => {
        const result = await apiWrapper(async () => {
            return client.get('/user/composition/get/loans-page', {
                params: { userId, page, size }
            })
        }, 'Failed to fetch loans.')

        if (result.success) {
            const data = result.data || {}
            loans.value = data.content || []
            totalLoans.value = data.totalElements || 0
            totalLoansPages.value = data.totalPages || 0
            loansCurrentPage.value = data.number
            loansPageSize.value = data.size
        } else {
            loans.value = []
            totalLoans.value = 0
            totalLoansPages.value = 0
            loansCurrentPage.value = 0
        }
    }

    return {
        user,
        error,
        loading,

        accounts,
        totalAccounts,
        totalAccountsPages,
        accountsCurrentPage,
        accountsPageSize,

        transactions,
        totalTransactions,
        totalTransactionsPages,
        transactionsCurrentPage,
        transactionsPageSize,

        loans,
        totalLoans,
        totalLoansPages,
        loansCurrentPage,
        loansPageSize,

        getUserComposition,
        fetchAccountsPage,
        fetchTransactionsPage,
        fetchLoansPage
    }
})