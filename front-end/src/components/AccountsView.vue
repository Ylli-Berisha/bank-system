<template>
  <div class="page-container"> <header class="header">
    <h1>Your Accounts</h1>
    <p>Detailed overview of your banking accounts</p>
  </header>

    <section class="create-account-section">
      <h2 class="section-title">New Account Application</h2>
      <p class="section-description">Ready to expand your financial portfolio? Apply for a new account here.</p>

      <button @click="showForm = !showForm" :class="['toggle-form-btn', { 'is-open': showForm }]">
        <span v-if="!showForm">Apply For New Account</span>
        <span v-else>Cancel Application</span>
        <svg v-if="!showForm" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-plus-circle"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="16"></line><line x1="8" y1="12" x2="16" y2="12"></line></svg>
        <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-x-circle"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>
      </button>

      <Transition name="fade-slide">
        <form v-if="showForm" @submit.prevent="openNewAccountConfirmModal" class="create-form-card">
          <h3>New Account Details</h3>

          <div class="form-group">
            <label for="accountType">Account Type:</label>
            <select id="accountType" v-model="newAccount.type" required class="form-input">
              <option value="" disabled>Select an account type</option>
              <option v-for="type in accountTypes" :key="type" :value="type">
                {{ type.toString().replace('_', ' ') }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="initialBalance">Initial Balance:</label>
            <input
                id="initialBalance"
                type="number"
                v-model.number="newAccount.balance"
                min="0"
                required
                class="form-input"
                placeholder="e.g., 500.00"
            />
          </div>

          <div class="status-box">
            <strong>Status:</strong> Pending Approval
          </div>

          <button type="submit" class="submit-application-btn">
            Apply For New Account
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-arrow-right"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
          </button>
          <p v-if="createError" class="error">{{ createError }}</p>
        </form>
      </Transition>
    </section>

    <section class="section">
      <h2 class="section-title">Active Accounts</h2>
      <div v-if="activeAccounts.length" class="card-grid">
        <div v-for="account in activeAccounts" :key="account.id" class="card">
          <h3>{{ account.type }} Account</h3>
          <p><strong>Balance:</strong> ${{ account.balance.toFixed(2) }}</p>
          <p><strong>Status:</strong> {{ account.status.toLowerCase() }}</p>
          <p><strong>Created:</strong> {{ formatDate(account.createdAt) }}</p>
          <p><strong>Account id:</strong> {{ account.id }}</p>
          <button @click="openFreezeConfirmModal(account.id)" class="freeze-btn">Freeze</button>
        </div>
      </div>
      <p v-else class="empty-state-message">No active accounts found.</p> </section>

    <section class="section frozen-section">
      <h2 class="section-title">Frozen Accounts</h2>
      <div v-if="frozenAccounts.length" class="card-grid">
        <div v-for="account in frozenAccounts" :key="account.id" class="card frozen">
          <h3>{{ account.type }} Account</h3>
          <p><strong>Balance:</strong> ${{ account.balance.toFixed(2) }}</p>
          <p><strong>Status:</strong> {{ account.status.toLowerCase() }}</p>
          <p><strong>Created:</strong> {{ formatDate(account.createdAt) }}</p>
          <p><strong>Account id:</strong> {{ account.id }}</p>
          <button @click="openUnfreezeConfirmModal(account.id)" class="unfreeze-btn">Unfreeze</button>
        </div>
      </div>
      <p v-else class="empty-state-message">No frozen accounts found.</p> </section>

    <section class="section pending-section">
      <h2 class="section-title">Pending Accounts</h2>
      <div v-if="pendingAccounts.length" class="card-grid">
        <div v-for="account in pendingAccounts" :key="account.id" class="card pending">
          <h3>{{ account.type }} Account</h3>
          <p><strong>Balance:</strong> ${{ account.balance.toFixed(2) }}</p>
          <p><strong>Status:</strong> {{ account.status.toLowerCase() }}</p>
          <p><strong>Created:</strong> {{ formatDate(account.createdAt) }}</p>
          <p><strong>Account id:</strong> {{ account.id }}</p>
        </div>
      </div>
      <p v-else class="empty-state-message">No pending accounts found.</p> </section>

    <p v-if="accountsError" class="error">{{ accountsError }}</p>

    <ConfirmationModal
        :is-open="showConfirmationModal"
        :title="confirmationModalTitle"
        :confirm="handleConfirmationModalConfirm"
        :cancel="handleConfirmationModalCancel"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useAccountsStore } from '@/stores/acccountsStore.js'
import ConfirmationModal from '@/components/ConfirmModal.vue';

const accountsStore = useAccountsStore()
const {
  accounts,
  error: accountsError,
  accountTypes
} = storeToRefs(accountsStore)

const showForm = ref(false)
const createError = ref('')

const newAccount = ref({
  type: '',
  status: 'PENDING_APPROVAL',
  balance: 0,
  userId: localStorage.getItem('userId') || ''
})

const activeAccounts = computed(() =>
    accounts.value.filter(acc => acc.status === 'ACTIVE')
)

const frozenAccounts = computed(() =>
    accounts.value.filter(acc => acc.status === 'FROZEN')
)

const pendingAccounts = computed(() =>
    accounts.value.filter(acc => acc.status === 'PENDING_APPROVAL')
)

const showConfirmationModal = ref(false)
const confirmationModalTitle = ref('')
const currentActionType = ref(null)
const currentAccountId = ref(null)

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

function openNewAccountConfirmModal() {
  confirmationModalTitle.value = `Are you sure you want to apply for a new ${newAccount.value.type} account with initial balance $${newAccount.value.balance.toFixed(2)}?`
  currentActionType.value = 'apply'
  showConfirmationModal.value = true
}

function openFreezeConfirmModal(accountId) {
  confirmationModalTitle.value = 'Are you sure you want to freeze this account?'
  currentActionType.value = 'freeze'
  currentAccountId.value = accountId
  showConfirmationModal.value = true
}

function openUnfreezeConfirmModal(accountId) {
  confirmationModalTitle.value = 'Are you sure you want to unfreeze this account?'
  currentActionType.value = 'unfreeze'
  currentAccountId.value = accountId
  showConfirmationModal.value = true
}

async function handleConfirmationModalConfirm() {
  try {
    if (currentActionType.value === 'apply') {
      await submitApplication();
    } else if (currentActionType.value === 'freeze' && currentAccountId.value) {
      await freeze(currentAccountId.value);
    } else if (currentActionType.value === 'unfreeze' && currentAccountId.value) {
      await unfreeze(currentAccountId.value);
    }
  } catch (err) {
    console.error("Error during modal action:", err);
  } finally {
    resetConfirmationModalState();
    await accountsStore.fetchAccounts();
  }
}

function handleConfirmationModalCancel() {
  resetConfirmationModalState();
}

function resetConfirmationModalState() {
  showConfirmationModal.value = false;
  confirmationModalTitle.value = '';
  currentActionType.value = null;
  currentAccountId.value = null;
}

async function submitApplication() {
  createError.value = ''
  try {
    newAccount.value.userId = localStorage.getItem('userId')
    await accountsStore.applyForNewAccount({ ...newAccount.value })

    newAccount.value = {
      type: accountTypes.value[0] || '',
      status: 'PENDING_APPROVAL',
      balance: 0,
      userId: localStorage.getItem('userId') || ''
    }

    showForm.value = false
  } catch (err) {
    createError.value = err.message || 'Failed to create account.'
    throw err;
  }
}

async function freeze(accountId) {
  try {
    await accountsStore.freezeAccount(accountId)
  } catch (err) {
    console.error("Error freezing account:", err)
    throw err;
  }
}

async function unfreeze(accountId) {
  try {
    await accountsStore.unfreezeAccount(accountId)
  } catch (err) {
    console.error("Error unfreezing account:", err)
    throw err;
  }
}
</script>

<style scoped>
/* Only component-specific styles remain here */

/* Create New Account Section Styles */
.create-account-section {
  background-color: #e3f2fd;
  padding: 2.5rem;
  border-radius: 12px;
  margin-bottom: 3rem;
  text-align: center;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.08);
  position: relative;
}

.toggle-form-btn {
  background-color: #3498db;
  color: #ffffff;
  padding: 0.8rem 1.8rem;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  margin-top: 1.5rem;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  gap: 0.7rem;
  box-shadow: 0 5px 15px rgba(52, 152, 219, 0.2);
}

.toggle-form-btn svg {
  stroke: currentColor;
  transition: transform 0.3s ease;
}

.toggle-form-btn.is-open {
  background-color: #e74c3c;
  box-shadow: 0 5px 15px rgba(231, 76, 60, 0.2);
}

.toggle-form-btn:hover {
  background-color: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(52, 152, 219, 0.3);
}

.toggle-form-btn.is-open:hover {
  background-color: #c0392b;
  box-shadow: 0 8px 20px rgba(231, 76, 60, 0.3);
}

/* Form Styles */
.create-form-card {
  background-color: #ffffff;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  margin: 2rem auto 0;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  border-top: 5px solid #3498db;
  z-index: 10;
  position: relative;
}

.create-form-card h3 {
  font-size: 1.5rem;
  color: #1a2b4c;
  margin-bottom: 1rem;
  border-bottom: 1px solid #eceff1;
  padding-bottom: 0.8rem;
}

.form-group {
  text-align: left;
}

.form-group label {
  display: block;
  margin-bottom: 0.6rem;
  font-weight: 600;
  color: #334e68;
  font-size: 0.95rem;
}

.form-input {
  width: 100%;
  padding: 0.9rem 1.2rem;
  border: 1px solid #cfd8dc;
  border-radius: 8px;
  font-size: 1rem;
  color: #455a64;
  background-color: #fdfefe;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.2);
}

.form-input::placeholder {
  color: #90a4ae;
}

.status-box {
  background-color: #e0f2f7;
  border-left: 4px solid #0097a7;
  padding: 0.8rem 1.2rem;
  border-radius: 8px;
  font-size: 0.95rem;
  color: #006064;
  margin-top: 1rem;
  text-align: left;
}

.submit-application-btn {
  background-color: #27ae60;
  color: #ffffff;
  padding: 0.9rem 2.2rem;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.7rem;
  box-shadow: 0 5px 15px rgba(39, 174, 96, 0.2);
  margin-top: 1rem;
}

.submit-application-btn svg {
  stroke: currentColor;
}

.submit-application-btn:hover {
  background-color: #229a54;
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(39, 174, 96, 0.3);
}

/* Transition for form appearance */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.4s ease;
  overflow: hidden;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(20px);
  max-height: 0;
  padding-top: 0;
  padding-bottom: 0;
  margin-top: 0;
  margin-bottom: 0;
}

.fade-slide-enter-to,
.fade-slide-leave-from {
  opacity: 1;
  transform: translateY(0);
  max-height: 500px;
  padding-top: 2rem;
  padding-bottom: 2rem;
  margin-top: 2rem;
  margin-bottom: 0;
}

/* Account card specific styles */
.card {
  border-left-color: #2d8cf0; /* Default border color for active accounts */
}
.pending-section .card.pending {
  background-color: #fffde7;
  border-left-color: #f1c40f;
  box-shadow: 0 5px 15px rgba(241, 196, 15, 0.1);
}

.frozen-section .card.frozen {
  background-color: #f7f9fb;
  border-left-color: #95a5a6;
  box-shadow: 0 5px 15px rgba(149, 165, 166, 0.1);
}

.freeze-btn {
  background-color: #f39c12;
  border: none;
  padding: 8px 12px;
  color: white;
  cursor: pointer;
  border-radius: 6px;
  margin-top: 12px;
  font-size: 0.9rem;
  transition: background-color 0.2s ease, transform 0.2s ease;
}

.freeze-btn:hover {
  background-color: #e67e22;
  transform: translateY(-1px);
}

.unfreeze-btn {
  background-color: #3498db;
  border: none;
  padding: 8px 12px;
  color: white;
  cursor: pointer;
  border-radius: 6px;
  margin-top: 12px;
  font-size: 0.9rem;
  transition: background-color 0.2s ease, transform 0.2s ease;
}

.unfreeze-btn:hover {
  background-color: #2980b9;
  transform: translateY(-1px);
}
</style>