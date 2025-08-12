import { ref } from 'vue'
import client from '@/helpers/client.js'
import { defineStore } from 'pinia'

export const useAdminUsersStore = defineStore('adminUsers', () => {
    const users = ref([])
    const error = ref(null)

    const currentPage = ref(0)
    const totalPages = ref(0)
    const totalElements = ref(0)

    const lastFilters = ref({})
    const lastAdminId = ref(null)

    const getAllUsers = async (page = 0, size = 6) => {
        error.value = null
        users.value = []

        try {
            const response = await client.get('/admin-service/api/users/get/all', {
                params: { page, size },
            })

            if (response.status === 204) {
                users.value = []
                totalPages.value = 0
                totalElements.value = 0
                currentPage.value = 0
            } else {
                users.value = response.data.content || []
                totalPages.value = response.data.totalPages
                totalElements.value = response.data.totalElements
                currentPage.value = response.data.number
            }
        } catch (err) {
            users.value = []
            totalPages.value = 0
            totalElements.value = 0
            currentPage.value = 0

            if (err.response && err.response.data && err.response.data.message) {
                error.value = `Failed to fetch users: ${err.response.data.message}`
            } else if (err.response && err.response.status) {
                error.value = `Failed to fetch users. Server responded with status ${err.response.status}: ${err.response.statusText}`
            } else {
                error.value = 'Failed to fetch users due to a network error or unexpected issue.'
            }
            console.error(err)
        }
    }

    const filterUsers = async (
        adminId,
        filters = {},
        page = 0,
        size = 6
    ) => {
        error.value = null
        users.value = []

        if (!adminId) {
            error.value = 'Admin ID is required for filtering users'
            return
        }

        try {
            lastFilters.value = filters
            lastAdminId.value = adminId

            const response = await client.get('/admin-service/api/users/filter/admin-users', {
                params: { page, size, ...filters },
            })

            if (response.status === 204) {
                users.value = []
                totalPages.value = 0
                totalElements.value = 0
                currentPage.value = 0
            } else {
                users.value = response.data.content || []
                totalPages.value = response.data.totalPages
                totalElements.value = response.data.totalElements
                currentPage.value = response.data.number
            }
        } catch (err) {
            users.value = []
            totalPages.value = 0
            totalElements.value = 0
            currentPage.value = 0

            if (err.response && err.response.data && err.response.data.message) {
                error.value = `Failed to filter users: ${err.response.data.message}`
            } else if (err.response && err.response.status) {
                error.value = `Failed to filter users. Server responded with status ${err.response.status}: ${err.response.statusText}`
            } else {
                error.value = 'Failed to filter users due to a network error or unexpected issue.'
            }
            console.error(err)
        }
    }

    const fetchFilteredPage = async (page = 0, size = 6) => {
        if (!lastAdminId.value) {
            error.value = 'Admin ID is required for fetching filtered pages'
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
