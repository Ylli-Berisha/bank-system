<template>
  <div class="accounts-container">
    <header class="header">
      <h1>Your Accounts</h1>
      <p>Detailed overview of your banking accounts</p>
    </header>

    <!-- Create New Account Button -->
    <div class="create-section">
      <button @click="showForm = !showForm" class="create-btn">
        {{ showForm ? 'Cancel' : 'Apply For New Account' }}
      </button>

      <form v-if="showForm" @submit.prevent="handleApplication" class="create-form">
        <label>
          Account Type:
          <select v-model="newAccount.type" required>
            <option v-for="type in accountTypes" :key="type" :value="type">
              {{ type.toString().replace('_', ' ') }}
            </option>
          </select>
        </label>

        <label>
          Initial Balance:
          <input type="number" v-model.number="newAccount.balance" min="0" required />
        </label>

        <p class="status-info">
          <strong>Status:</strong> Pending Approval
        </p>

        <button type="submit" class="submit-btn">Apply For New Account</button>
        <p v-if="createError" class="error">{{ createError }}</p>
      </form>
    </div>

    <section class="section">
      <h2>Active Accounts</h2>
      <div v-if="activeAccounts.length" class="card-grid">
        <div v-for="account in activeAccounts" :key="account.id" class="card">
          <h3>{{ account.type }} Account</h3>
          <p><strong>Balance:</strong> ${{ account.balance.toFixed(2) }}</p>
          <p><strong>Status:</strong> {{ account.status.toLowerCase() }}</p>
          <p><strong>Created:</strong> {{ formatDate(account.createdAt) }}</p>
          <p><strong>Account id:</strong> {{ account.id }}</p>
        </div>
      </div>
      <p v-else>No active accounts found.</p>
    </section>

    <section class="section pending-section">
      <h2>Pending Accounts</h2>
      <div v-if="pendingAccounts.length" class="card-grid">
        <div v-for="account in pendingAccounts" :key="account.id" class="card pending">
          <h3>{{ account.type }} Account</h3>
          <p><strong>Balance:</strong> ${{ account.balance.toFixed(2) }}</p>
          <p><strong>Status:</strong> {{ account.status.toLowerCase() }}</p>
          <p><strong>Created:</strong> {{ formatDate(account.createdAt) }}</p>
          <p><strong>Account id:</strong> {{ account.id }}</p>
        </div>
      </div>
      <p v-else>No pending accounts found.</p>
    </section>


    <p v-if="accountsError" class="error">{{ accountsError }}</p>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useAccountsStore } from '@/stores/acccountsStore.js'

const accountsStore = useAccountsStore()
const {
  accounts,
  error: accountsError,
  accountTypes
} = storeToRefs(accountsStore)

const showForm = ref(false)
const createError = ref('')

// Default new account data with hardcoded status
const newAccount = ref({
  type: '',
  status: 'PENDING_APPROVAL',
  balance: 0,
  userId: localStorage.getItem('userId') || ''
})

// Computed: only accounts with status "ACTIVE"
const activeAccounts = computed(() =>
    accounts.value.filter(acc => acc.status === 'ACTIVE')
)

// Computed: only accounts with status "PENDING_APPROVAL"
const pendingAccounts = computed(() =>
    accounts.value.filter(acc => acc.status === 'PENDING_APPROVAL')
)

onMounted(async () => {
  document.title = "Accounts"
  await accountsStore.fetchAccounts()
  await accountsStore.fetchAccountTypes()

  if (accountTypes.value.length > 0) {
    newAccount.value.type = accountTypes.value[0]
  }
})

function formatDate(isoString) {
  if (!isoString) return ''
  const date = new Date(isoString)
  return date.toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

async function handleApplication() {
  createError.value = ''
  try {
    newAccount.value.userId = localStorage.getItem('userId')
    await accountsStore.applyForNewAccount({ ...newAccount.value })
    await accountsStore.fetchAccounts()

    // Reset the form
    newAccount.value = {
      type: accountTypes.value[0] || '',
      status: 'PENDING_APPROVAL',
      balance: 0,
      userId: localStorage.getItem('userId') || ''
    }

    showForm.value = false
  } catch (err) {
    createError.value = err.message || 'Failed to create account.'
  }
}
</script>




<style scoped>
.accounts-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem;
  font-family: 'Segoe UI', sans-serif;
  background-color: #f5f9ff;
  color: #1a2b4c;
}

.header {
  text-align: center;
  margin-bottom: 2rem;
}

.create-section {
  margin-bottom: 2rem;
}

.create-btn,
.submit-btn {
  background-color: #1450a3;
  color: white;
  padding: 0.6rem 1.2rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.create-btn:hover,
.submit-btn:hover {
  background-color: #0f3a7a;
}

.create-form {
  margin-top: 1rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

label {
  display: flex;
  flex-direction: column;
  font-weight: bold;
}

input,
select {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 5px;
}

.card-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.card {
  background-color: #ffffff;
  padding: 1rem 1.5rem;
  border-radius: 10px;
  box-shadow: 0 0 8px rgba(0, 55, 123, 0.1);
  flex: 1 1 250px;
}

.error {
  color: red;
  margin-top: 0.5rem;
}
</style>


<style scoped>
.accounts-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem;
  font-family: 'Segoe UI', sans-serif;
  background-color: #f0f4fa;
  color: #1a2b4c;
}

.header {
  text-align: center;
  margin-bottom: 2rem;
}

.section {
  margin-top: 2rem;
}

.card-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.card {
  background-color: #ffffff;
  padding: 1.2rem;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 55, 123, 0.1);
  flex: 1 1 260px;
}

.error {
  color: #d9534f;
  margin-top: 0.5rem;
}

.create-section {
  margin-bottom: 2rem;
  text-align: center;
}

.create-btn {
  background-color: #1450a3;
  color: #ffffff;
  padding: 0.6rem 1.2rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  margin-bottom: 1rem;
}

.create-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  max-width: 400px;
  margin: 0 auto;
  background-color: #ffffff;
  padding: 1.5rem;
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 55, 123, 0.1);
}

.create-form label {
  display: flex;
  flex-direction: column;
  text-align: left;
}

.submit-btn {
  background-color: #28a745;
  color: #ffffff;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}
.pending-section .card.pending {
  background-color: #fff5e6;
  border: 1px solid #ffa726;
}

</style>
