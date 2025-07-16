import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import client from '@/helpers/client.js'

export const useAdminLoansStore = defineStore('adminLoansStore', () => {
    const loans = ref([])
    const totalPages = ref(0)
    const totalElements = ref(0)
    const currentPage = ref(0)
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
    }
})
