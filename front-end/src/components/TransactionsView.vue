<template>
  <div class="page-container">
    <header class="header">
      <h1>Your Transactions</h1>
      <p>A detailed history of all your financial movements</p>
    </header>

    <section class="section filter-section">
      <h2 class="section-title">Filter Transactions</h2>
      <p class="section-description">Refine your transaction view using the controls below.</p>

      <div class="filter-controls-grid">
        <div class="filter-group">
          <label for="startDate">From Date:</label>
          <input type="date" id="startDate" v-model="filters.startDate" class="filter-input" />
        </div>
        <div class="filter-group">
          <label for="endDate">To Date:</label>
          <input type="date" id="endDate" v-model="filters.endDate" class="filter-input" />
        </div>

        <div class="filter-group">
          <label for="transactionType">Type:</label>
          <select id="transactionType" v-model="filters.type" class="filter-input">
            <option value="">All Types</option>
            <option v-for="type in transactionTypes" :key="type" :value="type">{{ type.toLowerCase() }}</option>
          </select>
        </div>

        <div class="filter-group">
          <label for="transactionStatus">Status:</label>
          <select id="transactionStatus" v-model="filters.status" class="filter-input">
            <option value="">All Statuses</option>
            <option v-for="status in transactionStatuses" :key="status" :value="status">{{ status.toLowerCase() }}</option>
          </select>
        </div>

        <div class="filter-group">
          <label for="minAmount">Min Amount:</label>
          <input type="number" id="minAmount" v-model.number="filters.minAmount" class="filter-input" placeholder="e.g., 10.00" />
        </div>
        <div class="filter-group">
          <label for="maxAmount">Max Amount:</label>
          <input type="number" id="maxAmount" v-model.number="filters.maxAmount" class="filter-input" placeholder="e.g., 500.00" />
        </div>

        <div class="filter-group filter-group-search">
          <label for="searchQuery">Search:</label>
          <input type="text" id="searchQuery" v-model="filters.query" class="filter-input" placeholder="By description or ID" />
        </div>

        <div class="filter-buttons">
          <button @click="clearFilters" class="clear-filters-btn">Clear Filters</button>
        </div>
      </div>
    </section>

    <section class="section">
      <h2 class="section-title">All Transactions</h2>
      <p class="section-description">Review deposits, withdrawals, and transfers.</p>

      <div v-if="loading" class="loading-message">
        <div class="spinner"></div>
        Fetching transactions...
      </div>

      <div v-if="error" class="error">{{ error }}</div>

      <div v-if="!loading && transactions.length === 0" class="empty-state-message">
        No transactions found matching your criteria.
      </div>

      <div v-else-if="!loading" class="card-grid">
        <div v-for="transaction in transactions" :key="transaction.id" class="card">
          <div class="card-header">
            <span class="transaction-type" :class="transactionTypeClass(transaction.type)">
              {{ transaction.type.toLowerCase() }}
            </span>
            <span class="transaction-amount" :class="transactionAmountClass(transaction.type)">
              ${{ transaction.amount.toFixed(2) }}
            </span>
          </div>

          <div class="card-body">
            <p><strong>Date:</strong> {{ formatDate(transaction.createdAt) }}</p>
            <p><strong>Status:</strong>
              <span
                  class="status-tag"
                  :class="transactionStatusClass(transaction.status)"
              >
                {{ transaction.status.toLowerCase() }}
              </span>
            </p>
            <p><strong>Details:</strong> {{ transaction.details || '—' }}</p>
            <p><strong>Recipient:</strong> {{ maskAccount(transaction.recipientAccountId) }}</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { storeToRefs } from 'pinia';
import { useTransactionsStore } from '@/stores/transactionsStore.js';

const transactionsStore = useTransactionsStore();
const { transactions, error, loading } = storeToRefs(transactionsStore);

const filters = ref({
  startDate: '',
  endDate: '',
  type: '',
  status: '',
  minAmount: null,
  maxAmount: null,
  query: ''
});

const transactionTypes = ['DEPOSIT', 'WITHDRAWAL', 'TRANSFER'];
const transactionStatuses = ['PENDING', 'COMPLETED', 'FAILED', 'REVERSED', 'CANCELLED'];

let searchDebounceTimer = null;

onMounted(async () => {
  document.title = "Transactions";
  await applyFilters();
});

watch(filters, (newFilters, oldFilters) => {
  if (newFilters.query !== oldFilters.query) {
    if (searchDebounceTimer) {
      clearTimeout(searchDebounceTimer);
    }
    searchDebounceTimer = setTimeout(() => {
      applyFilters();
    }, 500);
  } else {
    applyFilters();
  }
}, { deep: true });

async function applyFilters() {
  const currentFilters = filters.value;

  const hasActiveFilter = Object.values(currentFilters).some(value => {
    if (typeof value === 'number') {
      return value !== null && !isNaN(value);
    }
    if (typeof value === 'string') {
      return value.trim() !== '';
    }
    return false;
  });

  if (hasActiveFilter) {
    const cleanedFilters = {
      startDate: currentFilters.startDate || undefined,
      endDate: currentFilters.endDate || undefined,
      type: currentFilters.type || undefined,
      status: currentFilters.status || undefined,
      minAmount: currentFilters.minAmount !== null ? currentFilters.minAmount : undefined,
      maxAmount: currentFilters.maxAmount !== null ? currentFilters.maxAmount : undefined,
      query: currentFilters.query || undefined
    };
    await transactionsStore.fetchFilteredTransactions(cleanedFilters);
  } else {
    await transactionsStore.fetchAllTransactions();
  }
}

function clearFilters() {
  filters.value = {
    startDate: '',
    endDate: '',
    type: '',
    status: '',
    minAmount: null,
    maxAmount: null,
    query: ''
  };
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
  return accountId ? '****' + accountId.slice(-4) : '—';
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
</script>
<style scoped>
.filter-section {
  background-color: #f7f9fb;
  padding: 2rem;
  border-radius: 12px;
  margin-bottom: 2.5rem;
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.05);
}

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

.filter-group-search {
  grid-column: span 2;
  min-width: 300px;
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

.loading-message {
  text-align: center;
  color: #607d8b;
  font-size: 1.1rem;
  padding: 2rem;
  background-color: #e3f2fd;
  border-radius: 8px;
  margin-top: 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1rem;
  border: 1px solid #90caf9;
  font-weight: 500;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-left-color: #3498db;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.card-grid {
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.card {
  border-left: 5px solid #cfd8dc;
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
}
.type-withdrawal {
  background-color: #ffebee;
  color: #c62828;
}
.type-transfer {
  background-color: #e1f5fe;
  color: #0277bd;
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
</style>