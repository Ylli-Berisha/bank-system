<template>
  <div class="page-container">
    <header class="header">
      <h1>Admin Transactions</h1>
      <p>Manage and audit all platform transactions here.</p>
    </header>

    <section class="section filter-section">
      <h2 class="section-title">Filter Transactions</h2>
      <p class="section-description">Use the fields below to filter transactions across all users.</p>

      <div class="filter-controls-grid">
        <div class="filter-group">
          <label for="startDate">From Date:</label>
          <input type="date" id="startDate" v-model="filters.startDate" class="filter-input"/>
        </div>

        <div class="filter-group">
          <label for="endDate">To Date:</label>
          <input type="date" id="endDate" v-model="filters.endDate" class="filter-input"/>
        </div>

        <div class="filter-group">
          <label for="transactionType">Type:</label>
          <select id="transactionType" v-model="filters.type" class="filter-input">
            <option value="">All Types</option>
            <option v-for="type in transactionTypes" :key="type" :value="type">
              {{ type.toLowerCase() }}
            </option>
          </select>
        </div>

        <div class="filter-group">
          <label for="transactionStatus">Status:</label>
          <select id="transactionStatus" v-model="filters.status" class="filter-input">
            <option value="">All Statuses</option>
            <option v-for="status in transactionStatuses" :key="status" :value="status">
              {{ status.toLowerCase() }}
            </option>
          </select>
        </div>

        <div class="filter-group">
          <label for="minAmount">Min Amount:</label>
          <input type="number" id="minAmount" v-model.number="filters.minAmount" class="filter-input"
                 placeholder="e.g., 50"/>
        </div>

        <div class="filter-group">
          <label for="maxAmount">Max Amount:</label>
          <input type="number" id="maxAmount" v-model.number="filters.maxAmount" class="filter-input"
                 placeholder="e.g., 1000"/>
        </div>

        <div class="filter-group">
          <label for="filterUserId">User ID:</label>
          <input type="text" id="filterUserId" v-model="filters.userId" class="filter-input"
                 placeholder="e.g., usr_78af"/>
        </div>

        <div class="filter-group">
          <label for="filterUsername">Username:</label>
          <input type="text" id="filterUsername" v-model="filters.username" class="filter-input"
                 placeholder="e.g., johndoe"/>
        </div>

        <div class="filter-group">
          <label for="filterEmail">Email:</label>
          <input type="text" id="filterEmail" v-model="filters.email" class="filter-input"
                 placeholder="e.g., admin@example.com"/>
        </div>

        <div class="filter-group filter-group-search">
          <label for="searchQuery">Search:</label>
          <input type="text" id="searchQuery" v-model="filters.query" class="filter-input"
                 placeholder="By description or ID"/>
        </div>

        <div class="filter-buttons">
          <button @click="clearFilters" class="clear-filters-btn">Clear Filters</button>
        </div>
      </div>
    </section>

    <section class="section">
      <h2 class="section-title">All Transactions</h2>
      <p class="section-description">Browse and manage all transactions on the platform.</p>

      <div v-if="error" class="error">{{ error }}</div>

      <div v-else-if="!transactions || transactions.length === 0" class="empty-state-message">
        No transactions found matching your criteria.
      </div>

      <div v-else class="card-grid">
        <div
            v-for="transaction in transactions"
            :key="transaction.id"
            class="card"
            :class="transactionTypeClass(transaction.type)"
        >
          <div class="card-header">
            <span class="transaction-type">
              {{ transaction.type.toLowerCase() }}
            </span>
            <span class="transaction-amount" :class="transactionAmountClass(transaction.type)">
              ${{ transaction.amount ? transaction.amount.toFixed(2) : '0.00' }}
            </span>
          </div>

          <div class="card-body">
            <p><strong>Account id: </strong> {{ transaction.accountId }}</p>
            <p><strong>Date:</strong> {{ formatDate(transaction.createdAt) }}</p>
            <p><strong>Status:</strong>
              <span class="status-tag" :class="transactionStatusClass(transaction.status)">
                {{ transaction.status.toLowerCase() }}
              </span>
            </p>
            <p><strong>Details:</strong> {{ transaction.details || '—' }}</p>
            <p><strong>Recipient:</strong>
              {{ transaction.recipientAccountId ? maskAccount(transaction.recipientAccountId) : 'None' }}</p>

            <button
                v-if="transaction.type === 'TRANSFER' && transaction.status === 'COMPLETED'"
                @click="openRevertModal(transaction.id)"
                class="revert-btn"
            >
              Revert Transaction
            </button>
          </div>
        </div>
      </div>
    </section>

    <section class="pagination-controls" v-if="totalPages > 1">
      <button
          class="pagination-btn"
          :disabled="currentPage === 0"
          @click="goToPreviousPage"
      >
        ◀ Previous
      </button>

      <span><strong>Page {{ currentPage + 1 }} of {{ totalPages }}</strong></span>

      <button
          class="pagination-btn"
          :disabled="currentPage >= totalPages - 1"
          @click="goToNextPage"
      >
        Next ▶
      </button>
    </section>

    <div v-if="isRevertModalOpen" class="modal-overlay" @click.self="closeRevertModal">
      <div class="modal-content">
        <h3>Confirm Revert</h3>
        <p>Are you sure you want to revert this transaction?</p>
        <div class="modal-buttons">
          <button @click="confirmRevert" class="confirm-btn">Yes, Revert</button>
          <button @click="closeRevertModal" class="cancel-btn">Cancel</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive, computed, watch, onMounted} from 'vue';
import {useAdminTransactionsStore} from '@/stores/admin/adminTransactionsStore.js';

const transactionsStore = useAdminTransactionsStore();

const filters = reactive({
  startDate: '',
  endDate: '',
  type: '',
  status: '',
  minAmount: null,
  maxAmount: null,
  userId: '',
  username: '',
  email: '',
  query: '',
});

const transactionTypes = ['DEPOSIT', 'WITHDRAWAL', 'TRANSFER'];
const transactionStatuses = ['PENDING', 'COMPLETED', 'FAILED', 'REVERSED', 'CANCELLED'];

const transactions = computed(() => transactionsStore.paginatedTransactions);
const currentPage = computed(() => transactionsStore.currentPage);
const totalPages = computed(() => transactionsStore.totalPages);
const error = computed(() => transactionsStore.error);

let searchDebounceTimer = null;

onMounted(() => {
  fetchTransactions();
});

watch(filters, (newVal, oldVal) => {
  if (newVal.query !== oldVal.query) {
    if (searchDebounceTimer) clearTimeout(searchDebounceTimer);
    searchDebounceTimer = setTimeout(() => {
      transactionsStore.resetPage();
      fetchTransactions();
    }, 500);
  } else {
    transactionsStore.resetPage();
    fetchTransactions();
  }
}, {deep: true});

watch(currentPage, () => {
  fetchTransactions();
});

async function fetchTransactions() {
  try {
    const cleanFilters = {};
    for (const key in filters) {
      const val = filters[key];
      if (val !== '' && val !== null && val !== undefined) {
        cleanFilters[key] = val;
      }
    }
    await transactionsStore.fetchFilteredTransactions(cleanFilters, transactionsStore.currentPage);
  } catch (e) {
    console.error(e);
  }
}


const isRevertModalOpen = ref(false);
const selectedTransactionId = ref(null);

function openRevertModal(transactionId) {
  selectedTransactionId.value = transactionId;
  isRevertModalOpen.value = true;
}

function closeRevertModal() {
  isRevertModalOpen.value = false;
  selectedTransactionId.value = null;
}

async function confirmRevert() {
  if (!selectedTransactionId.value) return;

  try {
    await transactionsStore.revertTransaction(selectedTransactionId.value);
    alert('Transaction reverted successfully.');
    closeRevertModal();
    await fetchTransactions();
  } catch (e) {
    alert('Failed to revert transaction.');
    console.error(e);
  }
}


function goToNextPage() {
  if (transactionsStore.currentPage < transactionsStore.totalPages - 1) {
    transactionsStore.incrementPage();
  }
}

function goToPreviousPage() {
  if (transactionsStore.currentPage > 0) {
    transactionsStore.decrementPage();
  }
}

function formatDate(isoString) {
  if (!isoString) return '';
  const date = new Date(isoString);
  return date.toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
}

function maskAccount(accountId) {
  return accountId ? '****' + accountId.slice(-4) : 'None';
}

function transactionTypeClass(type) {
  if (type === 'DEPOSIT') return 'type-deposit';
  if (type === 'WITHDRAWAL') return 'type-withdrawal';
  if (type === 'TRANSFER') return 'type-transfer';
  return '';
}

function transactionAmountClass(type) {
  if (type === 'DEPOSIT') return 'text-green';
  if (type === 'WITHDRAWAL') return 'text-red';
  if (type === 'TRANSFER') return 'text-blue';
  return '';
}

function transactionStatusClass(status) {
  if (status === 'COMPLETED') return 'status-completed';
  if (status === 'PENDING') return 'status-pending';
  if (status === 'FAILED') return 'status-failed';
  if (status === 'REVERSED') return 'status-reversed';
  if (status === 'CANCELLED') return 'status-cancelled';
  return '';
}

function clearFilters() {
  Object.assign(filters, {
    startDate: '',
    endDate: '',
    type: '',
    status: '',
    minAmount: null,
    maxAmount: null,
    userId: '',
    username: '',
    email: '',
    query: '',
  });
  transactionsStore.resetPage();
}
</script>

<style scoped>
/* Container */
.page-container {
  padding: 2.5rem;
  max-width: 1200px;
  margin: 0 auto;
  font-family: 'Inter', sans-serif;
  background-color: #f0f2f5;
  color: #334e68;
}

/* Section styling */
.section {
  background-color: #ffffff;
  padding: 2.5rem;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  margin-bottom: 2.5rem;
}

.section-title {
  font-size: 2rem;
  color: #263238;
  margin-bottom: 0.75rem;
  font-weight: 600;
  text-align: center;
}

.section-description {
  font-size: 1rem;
  color: #78909c;
  text-align: center;
  margin-bottom: 2rem;
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

/* Card Grid */
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

/* Transaction Cards */
.card {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.06);
  padding: 1.8rem;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  border-left: 5px solid #cfd8dc;
}

.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 1rem;
  margin-bottom: 1rem;
  border-bottom: 1px solid #eceff1;
}

.transaction-type {
  font-size: 1rem;
  font-weight: 600;
  padding: 0.4rem 0.8rem;
  border-radius: 6px;
  text-transform: capitalize;
  display: inline-block;
}

.type-deposit {
  background-color: #e8f5e9;
  color: #2e7d32;
  border-left-color: #2e7d32;
}

.type-withdrawal {
  background-color: #ffebee;
  color: #c62828;
  border-left-color: #c62828;
}

.type-transfer {
  background-color: #e1f5fe;
  color: #0277bd;
  border-left-color: #0277bd;
}

.transaction-amount {
  font-size: 1.4rem;
  font-weight: 700;
}

.card-body p {
  font-size: 0.95rem;
  line-height: 1.6;
  color: #546e7a;
  margin-bottom: 0.5rem;
}

.card-body strong {
  color: #334e68;
}

.status-tag {
  padding: 0.3rem 0.7rem;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: capitalize;
  display: inline-block;
  margin-left: 0.5rem;
}

.status-completed {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-pending {
  background-color: #fffde7;
  color: #fbc02d;
}

.status-failed {
  background-color: #ffebee;
  color: #d32f2f;
}

.status-reversed {
  background-color: #f3e5f5;
  color: #8e24aa;
}

.status-cancelled {
  background-color: #e0e0e0;
  color: #616161;
}

/* Text color helpers */
.text-green {
  color: #2e7d32;
}

.text-red {
  color: #c62828;
}

.text-blue {
  color: #0277bd;
}

/* Revert Button */
.revert-btn {
  margin-top: 1rem;
  padding: 0.6rem 1.2rem;
  background-color: #c62828;
  color: white;
  font-weight: 600;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  display: inline-block;
  user-select: none;
}

.revert-btn:hover {
  background-color: #b71c1c;
}

/* Error styling */
.error {
  color: #d32f2f;
  font-weight: 600;
  text-align: center;
  margin-bottom: 1rem;
}

/* Empty state */
.empty-state-message {
  text-align: center;
  color: #78909c;
  font-size: 1.1rem;
  padding: 2rem;
  background-color: #f7f9fb;
  border-radius: 8px;
  margin-top: 1.5rem;
  border: 1px solid #cfd8dc;
}

/* Pagination */
.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
  gap: 0.5rem;
}

.pagination-btn {
  background-color: #eceff1;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s ease;
  user-select: none;
}

.pagination-btn:hover:not(:disabled) {
  background-color: #b0bec5;
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: default;
}

.pagination-btn.active {
  background-color: #3498db;
  color: white;
}

.pagination-btn.active:hover {
  background-color: #2980b9;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 1.8rem 2rem;
  border-radius: 10px;
  max-width: 400px;
  width: 90%;
  text-align: center;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);
}

.modal-buttons {
  margin-top: 1.5rem;
  display: flex;
  justify-content: space-around;
}

.confirm-btn {
  background-color: #d32f2f;
  color: white;
  border: none;
  padding: 0.6rem 1.4rem;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.3s ease;
}

.confirm-btn:hover {
  background-color: #b71c1c;
}

.cancel-btn {
  background-color: #78909c;
  color: white;
  border: none;
  padding: 0.6rem 1.4rem;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.3s ease;
}

.cancel-btn:hover {
  background-color: #546e7a;
}
</style>
