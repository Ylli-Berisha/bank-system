import { ref } from 'vue'
import client from '@/helpers/client.js'
import { defineStore } from 'pinia'

export const useAdminAccountsStore = defineStore('adminAccounts', () => {
    const accounts = ref([])
    const error = ref(null)

    const currentPage = ref(0)
    const totalPages = ref(0)
    const totalElements = ref(0)

    const getAllAccounts = async () => {
        error.value = null
        accounts.value = []

        try {
            const url = `/admin-service/api/accounts/get/all`
            const response = await client.get(url)

            if (response.status === 204) {
                accounts.value = []
            } else {
                accounts.value = response.data
            }
        } catch (err) {
            accounts.value = []

            if (err.response && err.response.data && err.response.data.message) {
                error.value = `Failed to fetch accounts: ${err.response.data.message}`
            } else if (err.response && err.response.status) {
                error.value = `Failed to fetch accounts. Server responded with status ${err.response.status}: ${err.response.statusText}`
            } else {
                error.value = 'Failed to fetch accounts due to a network error or unexpected issue.'
            }
        }
    }

    async function fetchFilteredAccounts(filters, page = currentPage.value, size = 12) {
        error.value = null

        try {
            const params = {
                ...filters,
                page,
                size,
            }

            const response = await client.get('/admin-service/api/accounts/filter/admin-accounts', {
                params,
            })

            accounts.value = response.data.content || []
            totalPages.value = response.data.totalPages
            totalElements.value = response.data.totalElements
            currentPage.value = response.data.number
        } catch (err) {
            error.value = 'Failed to load accounts.'
            accounts.value = []
            totalPages.value = 0
            totalElements.value = 0
            console.error(err)
        }
    }

    const freezeAccount = async (accountId) => {
        error.value = null
        try {
            const url = `/admin-service/api/accounts/${accountId}/freeze`
            const response = await client.patch(url)
            return response.status === 200
        } catch (err) {
            if (err.response && err.response.data && err.response.data.message) {
                error.value = `Failed to freeze account: ${err.response.data.message}`
            } else if (err.response && err.response.status) {
                error.value = `Failed to freeze account. Server responded with status ${err.response.status}: ${err.response.statusText}`
            } else {
                error.value = 'Failed to freeze account due to a network error or unexpected issue.'
            }
            throw err
        }
    }

    const unfreezeAccount = async (accountId) => {
        error.value = null
        try {
            const url = `/admin-service/api/accounts/${accountId}/unfreeze`
            const response = await client.patch(url)
            return response.status === 200
        } catch (err) {
            if (err.response && err.response.data && err.response.data.message) {
                error.value = `Failed to unfreeze account: ${err.response.data.message}`
            } else if (err.response && err.response.status) {
                error.value = `Failed to unfreeze account. Server responded with status ${err.response.status}: ${err.response.statusText}`
            } else {
                error.value = 'Failed to unfreeze account due to a network error or unexpected issue.'
            }
            throw err
        }
    }

    const approveAccount = async (accountId) => {
        error.value = null
        try {
            const url = `/admin-service/api/accounts/approve/account/${accountId}`
            const response = await client.patch(url)
            return response.status === 200
        } catch (err) {
            if (err.response && err.response.data && err.response.data.message) {
                error.value = `Failed to approve account: ${err.response.data.message}`
            } else if (err.response && err.response.status) {
                error.value = `Failed to approve account. Server responded with status ${err.response.status}: ${err.response.statusText}`
            } else {
                error.value = 'Failed to approve account due to a network error or unexpected issue.'
            }
            throw err
        }
    }

    const rejectAccount = async (accountId) => {
        error.value = null
        try {
            const url = `/admin-service/api/accounts/reject/account/${accountId}`
            const response = await client.patch(url)
            return response.status === 200
        } catch (err) {
            if (err.response && err.response.data && err.response.data.message) {
                error.value = `Failed to reject account: ${err.response.data.message}`
            } else if (err.response && err.response.status) {
                error.value = `Failed to reject account. Server responded with status ${err.response.status}: ${err.response.statusText}`
            } else {
                error.value = 'Failed to reject account due to a network error or unexpected issue.'
            }
            throw err
        }
    }

    return {
        accounts,
        error,
        currentPage,
        totalPages,
        totalElements,
        getAllAccounts,
        freezeAccount,
        unfreezeAccount,
        approveAccount,
        rejectAccount,
        fetchFilteredAccounts,
    }
})
