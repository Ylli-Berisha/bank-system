<template>
  <div class="page-container">
    <header class="header">
      <h1>Account Management</h1>
      <p>Oversee and manage all customer bank accounts</p>
    </header>

    <section class="section active-section">
      <h2 class="section-title">Active Accounts</h2>
      <div v-if="activeAccounts.length" class="card-grid">
        <div v-for="account in activeAccounts" :key="account.id" class="card" :class="accountStatusClass(account.status)">
          <h3>{{ account.type.replace('_', ' ') }} Account</h3>
          <p><strong>Customer ID:</strong> {{ account.userId }}</p>
          <p><strong>Balance:</strong> ${{ account.balance.toFixed(2) }}</p>
          <p><strong>Status:</strong> {{ account.status.toLowerCase().replace('_', ' ') }}</p>
          <p><strong>Created:</strong> {{ formatDate(account.createdAt) }}</p>
          <p><strong>Account ID:</strong> {{ account.id }}</p>
          <div class="card-actions">
            <button @click="openFreezeConfirmModal(account.id)" class="freeze-btn">Freeze</button>
          </div>
        </div>
      </div>
      <p v-else class="empty-state-message">No active accounts found.</p>
    </section>

    <section class="section frozen-section">
      <h2 class="section-title">Frozen Accounts</h2>
      <div v-if="frozenAccounts.length" class="card-grid">
        <div v-for="account in frozenAccounts" :key="account.id" class="card" :class="accountStatusClass(account.status)">
          <h3>{{ account.type.replace('_', ' ') }} Account</h3>
          <p><strong>Customer ID:</strong> {{ account.userId }}</p>
          <p><strong>Balance:</strong> ${{ account.balance.toFixed(2) }}</p>
          <p><strong>Status:</strong> {{ account.status.toLowerCase().replace('_', ' ') }}</p>
          <p><strong>Created:</strong> {{ formatDate(account.createdAt) }}</p>
          <p><strong>Account ID:</strong> {{ account.id }}</p>
          <div class="card-actions">
            <button @click="openUnfreezeConfirmModal(account.id)" class="unfreeze-btn">Unfreeze</button>
          </div>
        </div>
      </div>
      <p v-else class="empty-state-message">No frozen accounts found.</p>
    </section>

    <section class="section pending-section">
      <h2 class="section-title">Pending Accounts</h2>
      <div v-if="pendingAccounts.length" class="card-grid">
        <div v-for="account in pendingAccounts" :key="account.id" class="card" :class="accountStatusClass(account.status)">
          <h3>{{ account.type.replace('_', ' ') }} Account</h3>
          <p><strong>Customer ID:</strong> {{ account.userId }}</p>
          <p><strong>Balance:</strong> ${{ account.balance.toFixed(2) }}</p>
          <p><strong>Status:</strong> {{ account.status.toLowerCase().replace('_', ' ') }}</p>
          <p><strong>Created:</strong> {{ formatDate(account.createdAt) }}</p>
          <p><strong>Account ID:</strong> {{ account.id }}</p>
          <div class="card-actions">
            <button @click="openApproveConfirmModal(account.id)" class="approve-btn">Approve</button>
          </div>
        </div>
      </div>
      <p v-else class="empty-state-message">No pending accounts found.</p>
    </section>

    <section class="section closed-section">
      <h2 class="section-title">Closed Accounts</h2>
      <div v-if="closedAccounts.length" class="card-grid">
        <div v-for="account in closedAccounts" :key="account.id" class="card" :class="accountStatusClass(account.status)">
          <h3>{{ account.type.replace('_', ' ') }} Account</h3>
          <p><strong>Customer ID:</strong> {{ account.userId }}</p>
          <p><strong>Balance:</strong> ${{ account.balance.toFixed(2) }}</p>
          <p><strong>Status:</strong> {{ account.status.toLowerCase().replace('_', ' ') }}</p>
          <p><strong>Created:</strong> {{ formatDate(account.createdAt) }}</p>
          <p><strong>Account ID:</strong> {{ account.id }}</p>
          <div class="card-actions">
          </div>
        </div>
      </div>
      <p v-else class="empty-state-message">No closed accounts found.</p>
    </section>

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
import { useAdminAccountsStore } from '@/stores/admin/adminAccountsStore.js'
import ConfirmationModal from '@/components/ConfirmModal.vue';

const adminAccountsStore = useAdminAccountsStore()
const {
  accounts,
  error: accountsError,
} = storeToRefs(adminAccountsStore)

const showConfirmationModal = ref(false)
const confirmationModalTitle = ref('')
const currentActionType = ref(null)
const currentAccountId = ref(null)

// Computed properties to filter accounts by status
const activeAccounts = computed(() =>
    (accounts.value ?? []).filter(acc => acc.status === 'ACTIVE')
)

const frozenAccounts = computed(() =>
    (accounts.value ?? []).filter(acc => acc.status === 'FROZEN')
)

const pendingAccounts = computed(() =>
    (accounts.value ?? []).filter(acc => acc.status === 'PENDING_APPROVAL')
)

const closedAccounts = computed(() =>
    (accounts.value ?? []).filter(acc => acc.status === 'CLOSED')
)


onMounted(async () => {
  document.title = "Admin Accounts"
  await adminAccountsStore.getAllAccounts()
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

// New method to open confirmation modal for approving an account
function openApproveConfirmModal(accountId) {
  confirmationModalTitle.value = 'Are you sure you want to approve this account?'
  currentActionType.value = 'approve'
  currentAccountId.value = accountId
  showConfirmationModal.value = true
}

async function handleConfirmationModalConfirm() {
  try {
    if (currentActionType.value === 'freeze' && currentAccountId.value) {
      await freeze(currentAccountId.value);
    } else if (currentActionType.value === 'unfreeze' && currentAccountId.value) {
      await unfreeze(currentAccountId.value);
    } else if (currentActionType.value === 'approve' && currentAccountId.value) {
      await approve(currentAccountId.value); // Call the new approve method
    }
  } catch (err) {
    console.error("Error during modal action:", err);
    // Optionally, set an error message here to display in the UI
    accountsError.value = `Action failed: ${err.message || 'Unknown error'}`;
  } finally {
    resetConfirmationModalState();
    await adminAccountsStore.getAllAccounts(); // Re-fetch to update all sections after any action
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

async function freeze(accountId) {
  try {
    await adminAccountsStore.freezeAccount(accountId)
  } catch (err) {
    console.error("Error freezing account:", err)
    throw err;
  }
}

async function unfreeze(accountId) {
  try {
    await adminAccountsStore.unfreezeAccount(accountId)
  } catch (err) {
    console.error("Error unfreezing account:", err)
    throw err;
  }
}

// New method to call the approveAccount action in the store
async function approve(accountId) {
  try {
    await adminAccountsStore.approveAccount(accountId)
  } catch (err) {
    console.error("Error approving account:", err)
    throw err;
  }
}

function accountStatusClass(status) {
  switch (status) {
    case 'ACTIVE':
      return 'active-account';
    case 'FROZEN':
      return 'frozen-account';
    case 'PENDING_APPROVAL':
      return 'pending-account';
    case 'CLOSED':
      return 'closed-account';
    default:
      return '';
  }
}
</script>

<style scoped>
/* Only component-specific styles remain here */

/* General Page Container and Header */
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2.5rem 1.5rem;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
}

.header {
  text-align: center;
  margin-bottom: 3.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #e0e0e0;
}

.header h1 {
  font-size: 3rem;
  color: #1a2b4c;
  margin-bottom: 0.5rem;
  font-weight: 700;
}

.header p {
  font-size: 1.2rem;
  color: #607d8b;
  max-width: 700px;
  margin: 0 auto;
}

/* Common Section Styling */
.section {
  margin-bottom: 3rem;
  padding: 2rem;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.05);
}

.section-title {
  font-size: 2rem;
  color: #1a2b4c;
  margin-bottom: 1.5rem;
  text-align: center;
  position: relative;
  padding-bottom: 1rem;
}

.section-title::after {
  content: '';
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  bottom: 0;
  width: 60px;
  height: 3px;
  background-color: #3498db;
  border-radius: 2px;
}

/* Card Grid */
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.card {
  background-color: #f8fafd;
  padding: 1.8rem;
  border-radius: 10px;
  border-left: 6px solid; /* Will be dynamically set by status class */
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  display: flex;
  flex-direction: column;
}

.card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.card h3 {
  font-size: 1.4rem;
  color: #1a2b4c;
  margin-bottom: 1rem;
  border-bottom: 1px solid #e0e0e0;
  padding-bottom: 0.8rem;
}

.card p {
  font-size: 1rem;
  color: #455a64;
  margin-bottom: 0.6rem;
}

.card p strong {
  color: #1a2b4c;
}

.card-actions {
  margin-top: auto; /* Pushes buttons to the bottom */
  padding-top: 1rem;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 0.8rem;
}

.empty-state-message {
  text-align: center;
  font-size: 1.1rem;
  color: #7f8c8d;
  padding: 2rem 0;
  background-color: #f0f4f7;
  border-radius: 8px;
  margin-top: 2rem;
}

.error {
  color: #e74c3c;
  text-align: center;
  font-size: 1.1rem;
  margin-top: 2rem;
  padding: 1rem;
  background-color: #fdeded;
  border: 1px solid #e74c3c;
  border-radius: 8px;
}

/* Account card specific styles based on status */
.card.active-account {
  border-left-color: #2d8cf0; /* Blue */
  background-color: #eaf2fb;
}
.card.frozen-account {
  border-left-color: #95a5a6; /* Grey */
  background-color: #f7f9fb;
}
.card.pending-account {
  border-left-color: #f1c40f; /* Yellow */
  background-color: #fffde7;
}
.card.closed-account {
  border-left-color: #e74c3c; /* Red */
  background-color: #fdeded;
}

/* Freeze/Unfreeze buttons */
.freeze-btn,
.unfreeze-btn {
  background-color: #f39c12;
  border: none;
  padding: 12px 20px;
  color: white;
  cursor: pointer;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  min-width: 120px;
  transition: background-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.freeze-btn:hover {
  background-color: #e67e22;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.15);
}

.unfreeze-btn {
  background-color: #3498db;
}

.unfreeze-btn:hover {
  background-color: #2980b9;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.15);
}

/* New Approve Button Style */
.approve-btn {
  background-color: #28a745; /* Green */
  border: none;
  padding: 12px 20px;
  color: white;
  cursor: pointer;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  min-width: 120px;
  transition: background-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.approve-btn:hover {
  background-color: #218838;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.15);
}
</style>