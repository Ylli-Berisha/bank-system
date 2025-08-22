import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import client from '@/helpers/client.js';
import { apiWrapper, globalError } from '@/helpers/apiWrapper.js';

export const useAdminAuditsStore = defineStore('adminAuditsStore', () => {
    const audits = ref([]);
    const totalPages = ref(0);
    const totalElements = ref(0);
    const currentPage = ref(0);
    const error = globalError;

    const paginatedAudits = computed(() => audits.value);

    async function fetchFilteredAudits(filters, page = currentPage.value, size = 12) {
        const result = await apiWrapper(async () => {
            const params = {
                ...filters,
                page,
                size,
            };
            return client.get('/audit-service/api/audit/filter/admin-audits', { params });
        }, 'Failed to load audits.');

        if (result.success) {
            audits.value = result.data.content || [];
            totalPages.value = result.data.totalPages;
            totalElements.value = result.data.totalElements;
            currentPage.value = result.data.number;
        } else {
            audits.value = [];
            totalPages.value = 0;
            totalElements.value = 0;
            currentPage.value = 0;
        }
    }

    function resetPage() {
        currentPage.value = 0;
    }

    function incrementPage() {
        if (currentPage.value < totalPages.value - 1) {
            currentPage.value++;
        }
    }

    function decrementPage() {
        if (currentPage.value > 0) {
            currentPage.value--;
        }
    }

    return {
        audits,
        totalPages,
        totalElements,
        currentPage,
        error,
        fetchFilteredAudits,
        resetPage,
        incrementPage,
        decrementPage,
        paginatedAudits,
    };
});