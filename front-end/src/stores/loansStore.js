import { ref } from "vue";
import { defineStore } from "pinia";
import client from "@/helpers/client.js";
import { apiWrapper, globalError } from "@/helpers/apiWrapper.js";

export const useLoansStore = defineStore('loans', () => {
    const loans = ref([]);
    const totalLoans = ref(0);
    const totalPages = ref(0);
    const currentPage = ref(0);
    const pageSize = ref(10);
    const error = globalError;
    const topActiveLoans = ref([]);
    const loanTypes = ref([]);
    const createError = globalError;
    const adminLoansPage = ref(null);

    const fetchAllLoans = async (status = null, page = 0, size = 6) => {
        const result = await apiWrapper(async () => {
            const params = new URLSearchParams();
            params.append('page', page.toString());
            params.append('size', size.toString());
            if (status) {
                params.append('status', status);
            }
            const url = `/transactions-service/api/loans/get/user-loans?${params.toString()}`;
            return client.get(url);
        }, 'Failed to fetch loans.');

        if (result.success) {
            const data = result.data || {};
            loans.value = data.content || [];
            totalLoans.value = data.totalElements || 0;
            totalPages.value = data.totalPages || 0;
            currentPage.value = data.number || page;
            pageSize.value = data.size || size;
        } else {
            loans.value = [];
            totalLoans.value = 0;
            totalPages.value = 0;
            currentPage.value = 0;
        }
    };

    const fetchFilteredLoans = async (filters = {}) => {
        const result = await apiWrapper(async () => {
            const params = new URLSearchParams();
            for (const key in filters) {
                const value = filters[key];
                if (value !== undefined && value !== null && value !== '') {
                    params.append(key, value);
                }
            }
            const url = `/transactions-service/api/loans/filter/user-loans?${params.toString()}`;
            return client.get(url);
        }, 'Failed to fetch filtered loans.');

        if (result.success) {
            const data = result.data || {};
            loans.value = data.content || [];
            totalLoans.value = data.totalElements || 0;
            totalPages.value = data.totalPages || 0;
        } else {
            loans.value = [];
            totalLoans.value = 0;
            totalPages.value = 0;
        }
    };

    const fetchLoanTypes = async () => {
        const result = await apiWrapper(async () => {
            return client.get(`/transactions-service/api/loans/get/loan-types`);
        }, 'Failed to fetch loan types.');

        if (result.success) {
            loanTypes.value = result.data;
        } else {
            loanTypes.value = [];
        }
    };

    const fetchTopActiveLoans = async () => {
        const result = await apiWrapper(async () => {
            return client.get(`/transactions-service/api/loans/get/top-active-loans`);
        }, 'Failed to fetch top active loans.');

        if (result.success) {
            topActiveLoans.value = result.data || [];
        } else {
            topActiveLoans.value = [];
        }
    };

    const applyForNewLoan = async (accountId, loanApplicationDetails) => {
        const result = await apiWrapper(async () => {
            const url = `/transactions-service/api/loans/apply?accountId=${accountId}`;
            return client.post(url, loanApplicationDetails);
        }, 'Failed to apply for loan.');

        if (result.success) {
            await fetchAllLoans();
        }
        return result;
    };

    const acceptLoan = async (loanId) => {
        return apiWrapper(async () => {
            return client.put(`/transactions-service/api/loans/${loanId}/accept`);
        }, 'Failed to accept loan.');
    };

    const rejectLoan = async (loanId) => {
        return apiWrapper(async () => {
            return client.put(`/transactions-service/api/loans/${loanId}/reject`);
        }, 'Failed to reject loan.');
    };

    const acceptProposedChanges = async (loanId) => {
        return apiWrapper(async () => {
            return client.put(`/transactions-service/api/loans/${loanId}/accept-changes`);
        }, 'Failed to accept proposed changes.');
    };

    const rejectProposedChanges = async (loanId) => {
        return apiWrapper(async () => {
            return client.put(`/transactions-service/api/loans/${loanId}/reject-changes`);
        }, 'Failed to reject proposed changes.');
    };

    const filterAdminLoans = async (filters = {}, page = 0, size = 6) => {
        const result = await apiWrapper(async () => {
            const params = new URLSearchParams();
            for (const key in filters) {
                const value = filters[key];
                if (value !== undefined && value !== null && value !== '') {
                    params.append(key, value);
                }
            }
            params.append('page', page.toString());
            params.append('size', size.toString());
            const url = `/transactions-service/api/loans/filter/admin-loans?${params.toString()}`;
            return client.get(url);
        }, 'Failed to fetch admin loans.');

        if (result.success) {
            adminLoansPage.value = result.data;
        } else {
            adminLoansPage.value = null;
        }
    };

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
    };
});