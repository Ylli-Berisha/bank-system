<template>
  <div class="page-container">
    <header class="header">
      <h1>Account Management</h1>
      <p>Oversee and manage all customer bank accounts</p>
    </header>

    <section class="section filter-section">
      <h2 class="section-title">Filter Accounts</h2>
      <p class="section-description">Use the fields below to filter accounts across all users.</p>
      <div class="filter-controls-grid">
        <div class="filter-group">
          <label for="accountId">Account ID:</label>
          <input type="text" id="accountId" v-model="filters.accountId" class="filter-input" placeholder="e.g., acc_1234"/>
        </div>
        <div class="filter-group">
          <label for="userId">User ID:</label>
          <input type="text" id="userId" v-model="filters.userId" class="filter-input" placeholder="e.g., usr_5678"/>
        </div>
        <div class="filter-group">
          <label for="username">Username:</label>
          <input type="text" id="username" v-model="filters.username" class="filter-input" placeholder="e.g., johndoe"/>
        </div>
        <div class="filter-group">
          <label for="email">Email:</label>
          <input type="text" id="email" v-model="filters.email" class="filter-input" placeholder="e.g., user@example.com"/>
        </div>
        <div class="filter-group">
          <label for="loanId">Loan ID:</label>
          <input type="text" id="loanId" v-model="filters.loanId" class="filter-input" placeholder="e.g., loan_2345"/>
        </div>
        <div class="filter-group">
          <label for="transactionId">Transaction ID:</label>
          <input type="text" id="transactionId" v-model="filters.transactionId" class="filter-input" placeholder="e.g., tx_3456"/>
        </div>
        <div class="filter-group">
          <label for="accountType">Account Type:</label>
          <select id="accountType" v-model="filters.type" class="filter-input">
            <option value="">All Types</option>
            <option v-for="type in accountTypes" :key="type" :value="type">{{ type.toLowerCase() }}</option>
          </select>
        </div>
        <div class="filter-group">
          <label for="accountStatus">Account Status:</label>
          <select id="accountStatus" v-model="filters.status" class="filter-input">
            <option value="">All Statuses</option>
            <option v-for="status in accountStatuses" :key="status" :value="status">{{ status.toLowerCase().replace('_', ' ') }}</option>
          </select>
        </div>
        <div class="filter-group">
          <label for="minBalance">Min Balance:</label>
          <input type="number" id="minBalance" v-model.number="filters.minBalance" class="filter-input" placeholder="e.g., 100.00"/>
        </div>
        <div class="filter-group">
          <label for="maxBalance">Max Balance:</label>
          <input type="number" id="maxBalance" v-model.number="filters.maxBalance" class="filter-input" placeholder="e.g., 10000.00"/>
        </div>
        <div class="filter-buttons">
          <button @click="clearFilters" class="clear-filters-btn">Clear Filters</button>
        </div>
      </div>
    </section>

    <section class="section all-filtered-section" v-if="areFiltersApplied">
      <h2 class="section-title">All Filtered Accounts</h2>
      <div v-if="allFilteredAccountsContent.length" class="card-grid">
        <div v-for="account in allFilteredAccountsContent" :key="account.id" class="card" :class="accountStatusClass(account.status)">
          <h3>{{ account.type.replace('_', ' ') }} Account</h3>
          <p><strong>Customer ID:</strong> {{ account.userId }}</p>
          <p><strong>Balance:</strong> ${{ account.balance.toFixed(2) }}</p>
          <p><strong>Status:</strong> {{ account.status.toLowerCase().replace('_', ' ') }}</p>
          <p><strong>Created:</strong> {{ formatDate(account.createdAt) }}</p>
          <p><strong>Account ID:</strong> {{ account.id }}</p>
          <div class="card-actions">
            <button v-if="account.status === 'ACTIVE'" @click="openFreezeConfirmModal(account.id)" class="freeze-btn">Freeze</button>
            <button v-if="account.status === 'FROZEN'" @click="openUnfreezeConfirmModal(account.id)" class="unfreeze-btn">Unfreeze</button>
            <button v-if="account.status === 'PENDING_APPROVAL'" @click="openApproveConfirmModal(account.id)" class="approve-btn">Approve</button>
            <button v-if="account.status === 'PENDING_APPROVAL'" @click="openRejectConfirmModal(account.id)" class="reject-btn">Reject</button>
          </div>
        </div>
      </div>
      <p v-else class="empty-state-message">No accounts found matching the filters.</p>
      <div v-if="allFilteredTotalPages >= 1" class="pagination-controls">
        <button @click="goToPreviousPage('ALL_FILTERED')" :disabled="allFilteredCurrentPage === 0">Previous</button>
        <span>Page {{ allFilteredCurrentPage + 1 }} of {{ allFilteredTotalPages }}</span>
        <button @click="goToNextPage('ALL_FILTERED')" :disabled="allFilteredCurrentPage + 1 >= allFilteredTotalPages">Next</button>
      </div>
    </section>

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
      <div v-if="activeTotalPages >= 1" class="pagination-controls">
        <button @click="goToPreviousPage('ACTIVE')" :disabled="activeCurrentPage === 0">Previous</button>
        <span>Page {{ activeCurrentPage + 1 }} of {{ activeTotalPages }}</span>
        <button @click="goToNextPage('ACTIVE')" :disabled="activeCurrentPage + 1 >= activeTotalPages">Next</button>
      </div>
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
      <div v-if="frozenTotalPages >= 1" class="pagination-controls">
        <button @click="goToPreviousPage('FROZEN')" :disabled="frozenCurrentPage === 0">Previous</button>
        <span>Page {{ frozenCurrentPage + 1 }} of {{ frozenTotalPages }}</span>
        <button @click="goToNextPage('FROZEN')" :disabled="frozenCurrentPage + 1 >= frozenTotalPages">Next</button>
      </div>
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
            <button @click="openRejectConfirmModal(account.id)" class="reject-btn">Reject</button>
          </div>
        </div>
      </div>
      <p v-else class="empty-state-message">No pending accounts found.</p>
      <div v-if="pendingTotalPages >= 1" class="pagination-controls">
        <button @click="goToPreviousPage('PENDING_APPROVAL')" :disabled="pendingCurrentPage === 0">Previous</button>
        <span>Page {{ pendingCurrentPage + 1 }} of {{ pendingTotalPages }}</span>
        <button @click="goToNextPage('PENDING_APPROVAL')" :disabled="pendingCurrentPage + 1 >= pendingTotalPages">Next</button>
      </div>
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
          <div class="card-actions"></div>
        </div>
      </div>
      <p v-else class="empty-state-message">No closed accounts found.</p>
      <div v-if="closedTotalPages >= 1" class="pagination-controls">
        <button @click="goToPreviousPage('CLOSED')" :disabled="closedCurrentPage === 0">Previous</button>
        <span>Page {{ closedCurrentPage + 1 }} of {{ closedTotalPages }}</span>
        <button @click="goToNextPage('CLOSED')" :disabled="closedCurrentPage + 1 >= closedTotalPages">Next</button>
      </div>
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
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useAdminAccountsStore } from '@/stores/admin/adminAccountsStore.js'
import ConfirmationModal from '@/components/ConfirmModal.vue'

const adminAccountsStore = useAdminAccountsStore()
const { error: accountsError } = storeToRefs(adminAccountsStore)
const filters = adminAccountsStore.filters

const accountTypes = ['SAVINGS', 'CHECKING', 'FIXED_DEPOSIT', 'JOINT', 'BUSINESS']
const accountStatuses = ['ACTIVE', 'INACTIVE', 'SUSPENDED', 'CLOSED', 'FROZEN', 'PENDING_APPROVAL', 'REJECTED']
const showConfirmationModal = ref(false)
const confirmationModalTitle = ref('')
const currentActionType = ref(null)
const currentAccountId = ref(null)

const activeAccounts = computed(() => adminAccountsStore.activeAccounts.content ?? [])
const frozenAccounts = computed(() => adminAccountsStore.frozenAccounts.content ?? [])
const pendingAccounts = computed(() => adminAccountsStore.pendingAccounts.content ?? [])
const closedAccounts = computed(() => adminAccountsStore.closedAccounts.content ?? [])

const activeTotalPages = computed(() => adminAccountsStore.activeAccounts.totalPages ?? 0)
const frozenTotalPages = computed(() => adminAccountsStore.frozenAccounts.totalPages ?? 0)
const pendingTotalPages = computed(() => adminAccountsStore.pendingAccounts.totalPages ?? 0)
const closedTotalPages = computed(() => adminAccountsStore.closedAccounts.totalPages ?? 0)

const activeCurrentPage = computed(() => adminAccountsStore.activeAccounts.pageNumber ?? 0)
const frozenCurrentPage = computed(() => adminAccountsStore.frozenAccounts.pageNumber ?? 0)
const pendingCurrentPage = computed(() => adminAccountsStore.pendingAccounts.pageNumber ?? 0)
const closedCurrentPage = computed(() => adminAccountsStore.closedAccounts.pageNumber ?? 0)

const allFilteredAccountsContent = computed(() => adminAccountsStore.allFilteredAccounts.content ?? [])
const allFilteredTotalPages = computed(() => adminAccountsStore.allFilteredAccounts.totalPages ?? 0)
const allFilteredCurrentPage = computed(() => adminAccountsStore.allFilteredAccounts.pageNumber ?? 0)

const areFiltersApplied = computed(() => {
  for (const key in filters) {
    const value = filters[key];
    if (value !== null && value !== undefined) {
      if (typeof value === 'string' && value !== '') return true;
      if (typeof value === 'number') return true;
    }
  }
  return false;
});

onMounted(async () => {
  document.title = 'Admin Accounts'
  await adminAccountsStore.fetchFirstPages()
})

watch(filters, adminAccountsStore.debouncedFetch, { deep: true });

function clearFilters() { adminAccountsStore.clearFilters() }
function formatDate(isoString) { if (!isoString) return ''; return new Date(isoString).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' }) }

function openFreezeConfirmModal(accountId) { confirmationModalTitle.value = 'Are you sure you want to freeze this account?'; currentActionType.value = 'freeze'; currentAccountId.value = accountId; showConfirmationModal.value = true }
function openUnfreezeConfirmModal(accountId) { confirmationModalTitle.value = 'Are you sure you want to unfreeze this account?'; currentActionType.value = 'unfreeze'; currentAccountId.value = accountId; showConfirmationModal.value = true }
function openApproveConfirmModal(accountId) { confirmationModalTitle.value = 'Are you sure you want to approve this account?'; currentActionType.value = 'approve'; currentAccountId.value = accountId; showConfirmationModal.value = true }
function openRejectConfirmModal(accountId) { confirmationModalTitle.value = 'Are you sure you want to reject this account?'; currentActionType.value = 'reject'; currentAccountId.value = accountId; showConfirmationModal.value = true }

async function handleConfirmationModalConfirm() {
  try {
    if (currentActionType.value === 'freeze' && currentAccountId.value) await adminAccountsStore.freezeAccount(currentAccountId.value)
    else if (currentActionType.value === 'unfreeze' && currentAccountId.value) await adminAccountsStore.unfreezeAccount(currentAccountId.value)
    else if (currentActionType.value === 'approve' && currentAccountId.value) await adminAccountsStore.approveAccount(currentAccountId.value)
    else if (currentActionType.value === 'reject' && currentAccountId.value) await adminAccountsStore.rejectAccount(currentAccountId.value)
  } catch (err) { console.error(err); accountsError.value = `Action failed: ${err.message || 'Unknown error'}` }
  finally { resetConfirmationModalState() }
}

function handleConfirmationModalCancel() { resetConfirmationModalState() }
function resetConfirmationModalState() { showConfirmationModal.value = false; confirmationModalTitle.value = ''; currentActionType.value = null; currentAccountId.value = null }
function goToNextPage(status) { adminAccountsStore.goToNextPage(status) }
function goToPreviousPage(status) { adminAccountsStore.goToPreviousPage(status) }

function accountStatusClass(status) {
  switch (status) {
    case 'ACTIVE': return 'active-account'
    case 'FROZEN': return 'frozen-account'
    case 'PENDING_APPROVAL': return 'pending-account'
    case 'CLOSED': return 'closed-account'
    default: return ''
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
.reject-btn {
  background-color: #e74c3c; /* Red */
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

.reject-btn:hover {
  background-color: #c0392b;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.15);
}


/* Filter section */
.filter-controls-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-top: 1.5rem;
}

.filter-group {
  display: flex;
  flex-direction: column;
}

.filter-group label {
  font-size: 0.9rem;
  color: #546e7a;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.filter-input {
  padding: 0.8rem 1rem;
  border: 1px solid #cfd8dc;
  border-radius: 8px;
  font-size: 1rem;
  color: #455a64;
  background-color: #ffffff;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  width: 100%;
  box-sizing: border-box;
}

.filter-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.2);
}

.filter-input::placeholder {
  color: #90a4ae;
}

.filter-buttons {
  grid-column: 1 / -1;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 1rem;
}

.clear-filters-btn {
  background-color: #95a5a6;
  color: #ffffff;
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(149, 165, 166, 0.2);
}

.clear-filters-btn:hover {
  background-color: #7f8c8d;
  transform: translateY(-1px);
  box-shadow: 0 6px 15px rgba(149, 165, 166, 0.3);
}

/* Pagination Controls */
.pagination-controls {
  display: flex;
  justify-content: center; /* Center the pagination controls */
  align-items: center;
  gap: 10px; /* Space between buttons and text */
  margin-top: 20px; /* Space above the pagination controls */
  padding: 10px 0;
  /* Optional: Add a border or background if you want them to stand out more */
  /* border-top: 1px solid #eee; */
  /* background-color: #f9f9f9; */
}

.pagination-controls button {
  padding: 8px 15px;
  border: 1px solid #007bff; /* A nice blue border */
  border-radius: 5px; /* Slightly rounded corners */
  background-color: #007bff; /* Blue background */
  color: white; /* White text */
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease, border-color 0.3s ease, opacity 0.3s ease;
  min-width: 80px; /* Ensure buttons have a consistent width */
}

.pagination-controls button:hover:not(:disabled) {
  background-color: #0056b3; /* Darker blue on hover */
  border-color: #0056b3;
}

.pagination-controls button:disabled {
  background-color: #e0e0e0; /* Gray background for disabled buttons */
  border-color: #ccc;
  color: #a0a0a0; /* Lighter text for disabled buttons */
  cursor: not-allowed; /* Indicate not clickable */
  opacity: 0.6; /* Slightly transparent */
}

.pagination-controls span {
  font-size: 1rem;
  color: #555; /* Darker gray for page info text */
  font-weight: 500;
}

</style>