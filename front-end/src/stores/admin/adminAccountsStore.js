import { reactive, watch } from 'vue'
import { defineStore } from 'pinia'
import client from '@/helpers/client.js'
import { apiWrapper, globalError } from '@/helpers/apiWrapper.js'

export const useAdminAccountsStore = defineStore('adminAccounts', () => {
    const error = globalError;

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

    const activeAccounts = reactive({ content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true })
    const frozenAccounts = reactive({ content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true })
    const pendingAccounts = reactive({ content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true })
    const closedAccounts = reactive({ content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true })
    const allFilteredAccounts = reactive({ content: [], pageNumber: 0, pageSize: 12, totalPages: 0, totalElements: 0, first: true, last: true });

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
        const result = await apiWrapper(async () => {
            return client.patch(`/admin-service/api/accounts/${accountId}/freeze`)
        }, 'Failed to freeze the account.');

        if (result.success) {
            await fetchSectionAccounts('ACTIVE', activeAccounts.pageNumber)
            await fetchSectionAccounts('FROZEN', frozenAccounts.pageNumber)
            if (checkFiltersApplied(filters)) await fetchFilteredAccounts(filters, allFilteredAccounts.pageNumber)
        }

        return result.success;
    };

    const unfreezeAccount = async (accountId) => {
        const result = await apiWrapper(async () => {
            return client.patch(`/admin-service/api/accounts/${accountId}/unfreeze`);
        }, 'Failed to unfreeze the account.');

        if (result.success) {
            await fetchSectionAccounts('FROZEN', frozenAccounts.pageNumber)
            await fetchSectionAccounts('ACTIVE', activeAccounts.pageNumber)
            if (checkFiltersApplied(filters)) await fetchFilteredAccounts(filters, allFilteredAccounts.pageNumber)
        }

        return result.success;
    };

    const approveAccount = async (accountId) => {
        const result = await apiWrapper(async () => {
            return client.patch(`/admin-service/api/accounts/approve/account/${accountId}`);
        }, 'Failed to approve the account.');

        if (result.success) {
            await fetchSectionAccounts('PENDING_APPROVAL', pendingAccounts.pageNumber)
            await fetchSectionAccounts('ACTIVE', activeAccounts.pageNumber)
            if (checkFiltersApplied(filters)) await fetchFilteredAccounts(filters, allFilteredAccounts.pageNumber)
        }

        return result.success;
    };

    const rejectAccount = async (accountId) => {
        const result = await apiWrapper(async () => {
            return client.patch(`/admin-service/api/accounts/reject/account/${accountId}`);
        }, 'Failed to reject the account.');

        if (result.success) {
            await fetchSectionAccounts('PENDING_APPROVAL', pendingAccounts.pageNumber)
            await fetchSectionAccounts('CLOSED', closedAccounts.pageNumber)
            if (checkFiltersApplied(filters)) await fetchFilteredAccounts(filters, allFilteredAccounts.pageNumber)
        }

        return result.success;
    };

    async function fetchFirstPages() {
        const result = await apiWrapper(async () => {
            return client.get('/api/accounts/composition/get/first-pages')
        }, 'Failed to fetch initial account data.');

        if (result.success) {
            const data = result.data;
            Object.assign(activeAccounts, data.activeAccounts || { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true });
            Object.assign(frozenAccounts, data.frozenAccounts || { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true });
            Object.assign(pendingAccounts, data.pendingAccounts || { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true });
            Object.assign(closedAccounts, data.closedAccounts || { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true });

            if (checkFiltersApplied(filters)) await fetchFilteredAccounts(filters, 0)
            else Object.assign(allFilteredAccounts, { content: [], pageNumber: 0, pageSize: 12, totalPages: 0, totalElements: 0, first: true, last: true })
        } else {
            Object.assign(activeAccounts, { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true });
            Object.assign(frozenAccounts, { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true });
            Object.assign(pendingAccounts, { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true });
            Object.assign(closedAccounts, { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true });
            Object.assign(allFilteredAccounts, { content: [], pageNumber: 0, pageSize: 12, totalPages: 0, totalElements: 0, first: true, last: true });
        }
    }

    async function fetchSectionAccounts(status, page = 0, size = 6) {
        const cleanFilters = {};
        for (const key in filters) {
            const val = filters[key];
            if (key !== 'status' && val !== null && val !== '' && val !== undefined) cleanFilters[key] = val;
        }

        const params = { ...cleanFilters, status, page, size };

        const result = await apiWrapper(async () => {
            return client.get('/accounts-service/api/accounts/get/by-status', { params });
        }, `Failed to fetch accounts for status: ${status}.`);

        const pageData = result.data || { content: [], pageNumber: 0, pageSize: 6, totalPages: 0, totalElements: 0, first: true, last: true };

        switch (status) {
            case 'ACTIVE': Object.assign(activeAccounts, pageData); break;
            case 'FROZEN': Object.assign(frozenAccounts, pageData); break;
            case 'PENDING_APPROVAL': Object.assign(pendingAccounts, pageData); break;
            case 'CLOSED': Object.assign(closedAccounts, pageData); break;
        }
    }

    let filterDebounceTimer = null;
    const debouncedFetch = () => {
        if (filterDebounceTimer) clearTimeout(filterDebounceTimer);
        filterDebounceTimer = setTimeout(async () => {
            await fetchSectionAccounts('ACTIVE', 0);
            await fetchSectionAccounts('FROZEN', 0);
            await fetchSectionAccounts('PENDING_APPROVAL', 0);
            await fetchSectionAccounts('CLOSED', 0);

            if (checkFiltersApplied(filters)) await fetchFilteredAccounts(filters, 0);
            else Object.assign(allFilteredAccounts, { content: [], pageNumber: 0, pageSize: 12, totalPages: 0, totalElements: 0, first: true, last: true });
        }, 500);
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
        let section;
        switch (status) {
            case 'ACTIVE': section = activeAccounts; break;
            case 'FROZEN': section = frozenAccounts; break;
            case 'PENDING_APPROVAL': section = pendingAccounts; break;
            case 'CLOSED': section = closedAccounts; break;
            case 'ALL_FILTERED': section = allFilteredAccounts; break;
            default: return;
        }
        if (section.pageNumber < section.totalPages - 1) {
            if (status === 'ALL_FILTERED') fetchFilteredAccounts(filters, section.pageNumber + 1);
            else fetchSectionAccounts(status, section.pageNumber + 1);
        }
    }

    function goToPreviousPage(status) {
        let section;
        switch (status) {
            case 'ACTIVE': section = activeAccounts; break;
            case 'FROZEN': section = frozenAccounts; break;
            case 'PENDING_APPROVAL': section = pendingAccounts; break;
            case 'CLOSED': section = closedAccounts; break;
            case 'ALL_FILTERED': section = allFilteredAccounts; break;
            default: return;
        }
        if (section.pageNumber > 0) {
            if (status === 'ALL_FILTERED') fetchFilteredAccounts(filters, section.pageNumber - 1);
            else fetchSectionAccounts(status, section.pageNumber - 1);
        }
    }

    async function fetchFilteredAccounts(currentFilters, page = 0, size = 12) {
        if (!checkFiltersApplied(currentFilters)) {
            Object.assign(allFilteredAccounts, { content: [], pageNumber: 0, pageSize: 12, totalPages: 0, totalElements: 0, first: true, last: true });
            return;
        }

        const cleanFilters = {};
        for (const key in currentFilters) {
            const val = currentFilters[key];
            if (val !== null && val !== '' && val !== undefined) cleanFilters[key] = val;
        }

        const params = { ...cleanFilters, page, size };

        const result = await apiWrapper(async () => {
            return client.get('/admin-service/api/accounts/filter/admin-accounts', { params });
        }, 'Failed to load filtered accounts.');

        if (result.success) {
            Object.assign(allFilteredAccounts, result.data || { content: [], pageNumber: 0, pageSize: 12, totalPages: 0, totalElements: 0, first: true, last: true });
        } else {
            Object.assign(allFilteredAccounts, { content: [], pageNumber: 0, pageSize: 12, totalPages: 0, totalElements: 0, first: true, last: true });
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