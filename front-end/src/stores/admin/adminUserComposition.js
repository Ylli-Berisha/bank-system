import { ref } from 'vue'
import { defineStore } from 'pinia'
import client from '@/helpers/client.js'

export const useAdminUserCompositionStore = defineStore('adminUserComposition', () => {
    const user = ref(null)
    const accounts = ref([])
    const transactions = ref([])
    const loans = ref([])
    const error = ref(null)
    const loading = ref(false)

    const getUserComposition = async (userId) => {
        loading.value = true
        error.value = null

        try {
            const res = await client.get('/user/composition/get/user-composition', {
                params: { userId }
            })

            user.value = res.data.user || null
            accounts.value = res.data.accounts?.content || []
            transactions.value = res.data.transactions?.content || []
            loans.value = res.data.loans?.content || []
        } catch (err) {
            console.error(err)
            error.value = 'Failed to load user composition'
        } finally {
            loading.value = false
        }
    }

    return {
        user,
        accounts,
        transactions,
        loans,
        error,
        loading,
        getUserComposition
    }
})
