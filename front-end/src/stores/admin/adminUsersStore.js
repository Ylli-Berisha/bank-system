import { ref } from 'vue'
import { defineStore } from 'pinia'
import client from '@/helpers/client.js'
import { apiWrapper, globalError } from '@/helpers/apiWrapper.js'

export const useAdminUsersStore = defineStore('adminUsers', () => {
    const users = ref([])
    const error = globalError

    const currentPage = ref(0)
    const totalPages = ref(0)
    const totalElements = ref(0)

    const lastFilters = ref({})
    const lastAdminId = ref(null)

    const getAllUsers = async (page = 0, size = 6) => {
        const result = await apiWrapper(async () => {
            return client.get('/admin-service/api/users/get/all', {
                params: { page, size },
            })
        }, 'Failed to fetch all users.')

        if (result.success) {
            users.value = result.data.content || []
            totalPages.value = result.data.totalPages
            totalElements.value = result.data.totalElements
            currentPage.value = result.data.number
        } else {
            users.value = []
            totalPages.value = 0
            totalElements.value = 0
            currentPage.value = 0
        }
    }

    const filterUsers = async (
        adminId,
        filters = {},
        page = 0,
        size = 6
    ) => {
        if (!adminId) {
            globalError.value = 'Admin ID is required for filtering users'
            return
        }

        const result = await apiWrapper(async () => {
            lastFilters.value = filters
            lastAdminId.value = adminId

            return client.get('/admin-service/api/users/filter/admin-users', {
                params: { page, size, ...filters },
            })
        }, 'Failed to filter users.')

        if (result.success) {
            users.value = result.data.content || []
            totalPages.value = result.data.totalPages
            totalElements.value = result.data.totalElements
            currentPage.value = result.data.number
        } else {
            users.value = []
            totalPages.value = 0
            totalElements.value = 0
            currentPage.value = 0
        }
    }

    const fetchFilteredPage = async (page = 0, size = 6) => {
        if (!lastAdminId.value) {
            globalError.value = 'Admin ID is required for fetching filtered pages'
            return
        }
        await filterUsers(lastAdminId.value, lastFilters.value, page, size)
    }

    return {
        users,
        error,
        currentPage,
        totalPages,
        totalElements,
        getAllUsers,
        filterUsers,
        fetchFilteredPage,
        lastFilters,
        lastAdminId,
    }
})