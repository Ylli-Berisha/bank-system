import { ref, reactive, watch } from 'vue'
import client from '@/helpers/client.js'
import { defineStore } from 'pinia'

export const useAdminAccountsStore = defineStore('adminAccounts', () => {
    const error = ref(null)

    const filters = reactive({
        accountId: '',
        userId: '',
        username: '',
        email: '',
        loanId: '',
        transactionId: '',
        type: '',
        status: '',
        minBalance: null,
        maxBalance: null,
    })

    const activeAccounts = ref({ content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true })
    const frozenAccounts = ref({ content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true })
    const pendingAccounts = ref({ content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true })
    const closedAccounts = ref({ content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true })

    const allFilteredAccounts = ref({ content: [], pageNumber: 0, pageSize: 12, totalPages: 0, totalElements: 0, first: true, last: true });

    const checkFiltersApplied = (currentFilters) => {
        for (const key in currentFilters) {
            const value = currentFilters[key];
            if (value !== null && value !== undefined) {
                if (typeof value === 'string' && value !== '') return true
                if (typeof value === 'number') return true
            }
        }
        return false
    }

    const freezeAccount = async (accountId) => {
        error.value = null
        try {
            const res = await client.patch(`/admin-service/api/accounts/${accountId}/freeze`)
            await fetchSectionAccounts('ACTIVE', activeAccounts.value.pageNumber)
            await fetchSectionAccounts('FROZEN', frozenAccounts.value.pageNumber)
            if (checkFiltersApplied(filters)) await fetchFilteredAccounts(filters, allFilteredAccounts.value.pageNumber)
            return res.status === 200
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to freeze account.'
            throw err
        }
    }

    const unfreezeAccount = async (accountId) => {
        error.value = null
        try {
            const res = await client.patch(`/admin-service/api/accounts/${accountId}/unfreeze`)
            await fetchSectionAccounts('FROZEN', frozenAccounts.value.pageNumber)
            await fetchSectionAccounts('ACTIVE', activeAccounts.value.pageNumber)
            if (checkFiltersApplied(filters)) await fetchFilteredAccounts(filters, allFilteredAccounts.value.pageNumber)
            return res.status === 200
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to unfreeze account.'
            throw err
        }
    }

    const approveAccount = async (accountId) => {
        error.value = null
        try {
            const res = await client.patch(`/admin-service/api/accounts/approve/account/${accountId}`)
            await fetchSectionAccounts('PENDING_APPROVAL', pendingAccounts.value.pageNumber)
            await fetchSectionAccounts('ACTIVE', activeAccounts.value.pageNumber)
            if (checkFiltersApplied(filters)) await fetchFilteredAccounts(filters, allFilteredAccounts.value.pageNumber)
            return res.status === 200
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to approve account.'
            throw err
        }
    }

    const rejectAccount = async (accountId) => {
        error.value = null
        try {
            const res = await client.patch(`/admin-service/api/accounts/reject/account/${accountId}`)
            await fetchSectionAccounts('PENDING_APPROVAL', pendingAccounts.value.pageNumber)
            await fetchSectionAccounts('CLOSED', closedAccounts.value.pageNumber)
            if (checkFiltersApplied(filters)) await fetchFilteredAccounts(filters, allFilteredAccounts.value.pageNumber)
            return res.status === 200
        } catch (err) {
            error.value = err.response?.data?.message || 'Failed to reject account.'
            throw err
        }
    }

    async function fetchFirstPages() {
        error.value = null
        try {
            const response = await client.get('/api/accounts/composition/get/first-pages')
            const data = response.data

            activeAccounts.value = data.activeAccounts || { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true }
            frozenAccounts.value = data.frozenAccounts || { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true }
            pendingAccounts.value = data.pendingAccounts || { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true }
            closedAccounts.value = data.closedAccounts || { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true }

            if (checkFiltersApplied(filters)) await fetchFilteredAccounts(filters, 0)
            else allFilteredAccounts.value = { content: [], pageNumber: 0, pageSize: 12, totalPages: 0, totalElements: 0, first: true, last: true }

        } catch (err) {
            error.value = err.response?.data?.message || err.message || 'Failed to fetch accounts.'
            console.error(err)
            activeAccounts.value = { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true }
            frozenAccounts.value = { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true }
            pendingAccounts.value = { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true }
            closedAccounts.value = { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true }
            allFilteredAccounts.value = { content: [], pageNumber: 0, pageSize: 12, totalPages: 0, totalElements: 0, first: true, last: true }
        }
    }

    async function fetchSectionAccounts(status, page = 0, size = 6) {
        error.value = null
        try {
            const cleanFilters = {}
            for (const key in filters) {
                const val = filters[key]
                if (key !== 'status' && val !== null && val !== '' && val !== undefined) cleanFilters[key] = val
            }

            const params = { ...cleanFilters, status, page, size }
            const res = await client.get('/accounts-service/api/accounts/get/by-status', { params })
            const pageData = res.data

            switch (status) {
                case 'ACTIVE': activeAccounts.value = pageData; break
                case 'FROZEN': frozenAccounts.value = pageData; break
                case 'PENDING_APPROVAL': pendingAccounts.value = pageData; break
                case 'CLOSED': closedAccounts.value = pageData; break
            }
        } catch (err) {
            error.value = `Failed to fetch accounts for ${status}`
            console.error(err)
            switch (status) {
                case 'ACTIVE': activeAccounts.value = { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true }; break
                case 'FROZEN': frozenAccounts.value = { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true }; break
                case 'PENDING_APPROVAL': pendingAccounts.value = { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true }; break
                case 'CLOSED': closedAccounts.value = { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true }; break
            }
        }
    }

    let filterDebounceTimer = null
    const debouncedFetch = () => {
        if (filterDebounceTimer) clearTimeout(filterDebounceTimer)
        filterDebounceTimer = setTimeout(async () => {
            await fetchSectionAccounts('ACTIVE', 0)
            await fetchSectionAccounts('FROZEN', 0)
            await fetchSectionAccounts('PENDING_APPROVAL', 0)
            await fetchSectionAccounts('CLOSED', 0)

            if (checkFiltersApplied(filters)) await fetchFilteredAccounts(filters, 0)
            else allFilteredAccounts.value = { content: [], pageNumber: 0, pageSize: 12, totalPages: 0, totalElements: 0, first: true, last: true }
        }, 500)
    }

    watch(filters, debouncedFetch, { deep: true })

    function clearFilters() {
        Object.keys(filters).forEach(key => {
            if (typeof filters[key] === 'number') filters[key] = null
            else filters[key] = ''
        })
        debouncedFetch()
    }

    function goToNextPage(status) {
        let section
        switch (status) {
            case 'ACTIVE': section = activeAccounts.value; break
            case 'FROZEN': section = frozenAccounts.value; break
            case 'PENDING_APPROVAL': section = pendingAccounts.value; break
            case 'CLOSED': section = closedAccounts.value; break
            case 'ALL_FILTERED': section = allFilteredAccounts.value; break
            default: return
        }
        if (section.pageNumber < section.totalPages - 1) {
            if (status === 'ALL_FILTERED') fetchFilteredAccounts(filters, section.pageNumber + 1)
            else fetchSectionAccounts(status, section.pageNumber + 1)
        }
    }

    function goToPreviousPage(status) {
        let section
        switch (status) {
            case 'ACTIVE': section = activeAccounts.value; break
            case 'FROZEN': section = frozenAccounts.value; break
            case 'PENDING_APPROVAL': section = pendingAccounts.value; break
            case 'CLOSED': section = closedAccounts.value; break
            case 'ALL_FILTERED': section = allFilteredAccounts.value; break
            default: return
        }
        if (section.pageNumber > 0) {
            if (status === 'ALL_FILTERED') fetchFilteredAccounts(filters, section.pageNumber - 1)
            else fetchSectionAccounts(status, section.pageNumber - 1)
        }
    }

    async function fetchFilteredAccounts(currentFilters, page = 0, size = 12) {
        if (!checkFiltersApplied(currentFilters)) {
            allFilteredAccounts.value = { content: [], pageNumber: 0, pageSize: 12, totalPages: 0, totalElements: 0, first: true, last: true }
            return
        }

        error.value = null
        try {
            const cleanFilters = {}
            for (const key in currentFilters) {
                const val = currentFilters[key]
                if (val !== null && val !== '' && val !== undefined) cleanFilters[key] = val
            }

            const params = { ...cleanFilters, page, size }
            const response = await client.get('/admin-service/api/accounts/filter/admin-accounts', { params })
            allFilteredAccounts.value = response.data || { content: [], pageNumber: 0, pageSize: 12, totalPages: 0, totalElements: 0, first: true, last: true }

        } catch (err) {
            error.value = 'Failed to load all filtered accounts.'
            allFilteredAccounts.value = { content: [], pageNumber: 0, pageSize: 12, totalPages: 0, totalElements: 0, first: true, last: true }
            console.error(err)
        }
    }

    return {
        filters,
        error,
        activeAccounts,
        frozenAccounts,
        pendingAccounts,
        closedAccounts,
        allFilteredAccounts,
        fetchFirstPages,
        fetchSectionAccounts,
        freezeAccount,
        unfreezeAccount,
        approveAccount,
        rejectAccount,
        clearFilters,
        goToNextPage,
        goToPreviousPage,
        fetchFilteredAccounts,
    }
})
