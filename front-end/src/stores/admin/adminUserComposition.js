import { ref } from 'vue'
import { defineStore } from 'pinia'
import client from '@/helpers/client.js'

export const useAdminUserCompositionStore = defineStore('adminUserComposition', () => {
    const user = ref(null)
    const error = ref(null)
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
        error.value = null

        try {
            const res = await client.get('/user/composition/get/user-composition', {
                params: { userId }
            })

            user.value = res.data.user || null

            accounts.value = res.data.accounts?.content || []
            totalAccounts.value = res.data.accounts?.totalElements || 0
            totalAccountsPages.value = res.data.accounts?.totalPages || 0
            accountsCurrentPage.value = res.data.accounts?.number || 0
            accountsPageSize.value = res.data.accounts?.size || 6

            transactions.value = res.data.transactions?.content || []
            totalTransactions.value = res.data.transactions?.totalElements || 0
            totalTransactionsPages.value = res.data.transactions?.totalPages || 0
            transactionsCurrentPage.value = res.data.transactions?.number || 0
            transactionsPageSize.value = res.data.transactions?.size || 6

            loans.value = res.data.loans?.content || []
            totalLoans.value = res.data.loans?.totalElements || 0
            totalLoansPages.value = res.data.loans?.totalPages || 0
            loansCurrentPage.value = res.data.loans?.number || 0
            loansPageSize.value = res.data.loans?.size || 6
        } catch (err) {
            console.error(err)
            error.value = 'Failed to load user composition'
        } finally {
            loading.value = false
        }
    }

    const fetchAccountsPage = async (userId, page = accountsCurrentPage.value, size = accountsPageSize.value) => {
        try {
            const res = await client.get('/user/composition/get/accounts-page', {
                params: { userId, page, size }
            })
            accounts.value = res.data.content || []
            totalAccounts.value = res.data.totalElements || 0
            totalAccountsPages.value = res.data.totalPages || 0
            accountsCurrentPage.value = res.data.number
            accountsPageSize.value = res.data.size
        } catch (err) {
            console.error('Failed to fetch accounts:', err)
            accounts.value = []
            totalAccounts.value = 0
            totalAccountsPages.value = 0
            accountsCurrentPage.value = 0
        }
    }

    const fetchTransactionsPage = async (userId, page = transactionsCurrentPage.value, size = transactionsPageSize.value) => {
        try {
            const res = await client.get('/user/composition/get/transactions-page', {
                params: { userId, page, size }
            })
            transactions.value = res.data.content || []
            totalTransactions.value = res.data.totalElements || 0
            totalTransactionsPages.value = res.data.totalPages || 0
            transactionsCurrentPage.value = res.data.number
            transactionsPageSize.value = res.data.size
        } catch (err) {
            console.error('Failed to fetch transactions:', err)
            transactions.value = []
            totalTransactions.value = 0
            totalTransactionsPages.value = 0
            transactionsCurrentPage.value = 0
        }
    }

    const fetchLoansPage = async (userId, page = loansCurrentPage.value, size = loansPageSize.value) => {
        try {
            const res = await client.get('/user/composition/get/loans-page', {
                params: { userId, page, size }
            })
            loans.value = res.data.content || []
            totalLoans.value = res.data.totalElements || 0
            totalLoansPages.value = res.data.totalPages || 0
            loansCurrentPage.value = res.data.number
            loansPageSize.value = res.data.size
        } catch (err) {
            console.error('Failed to fetch loans:', err)
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
