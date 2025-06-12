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
            <option v-for="type in transactionTypes" :key="type" :value="type">{{ type.toLowerCase() }}</option>
          </select>
        </div>

        <div class="filter-group">
          <label for="transactionStatus">Status:</label>
          <select id="transactionStatus" v-model="filters.status" class="filter-input">
            <option value="">All Statuses</option>
            <option v-for="status in transactionStatuses" :key="status" :value="status">{{
                status.toLowerCase()
              }}
            </option>
          </select>
        </div>

        <div class="filter-group">
          <label for="minAmount">Min Amount:</label>
          <input type="number" id="minAmount" v-model.number="filters.minAmount" class="filter-input"
                 placeholder="e.g., 10.00"/>
        </div>
        <div class="filter-group">
          <label for="maxAmount">Max Amount:</label>
          <input type="number" id="maxAmount" v-model.number="filters.maxAmount" class="filter-input"
                 placeholder="e.g., 500.00"/>
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
      <p class="section-description">Review deposits, withdrawals, and transfers.</p>

      <div v-if="error" class="error">{{ error }}</div>

      <div v-if="transactions.length === 0 && !error" class="empty-state-message">
        No transactions found matching your criteria.
      </div>

      <div v-else class="card-grid">
        <div v-for="transaction in transactions" :key="transaction.id" class="card">
          <div class="card-header">
            <span class="transaction-type" :class="transactionTypeClass(transaction.type)">
              {{ transaction.type.toLowerCase() }}
            </span>
            <span class="transaction-amount" :class="transactionAmountClass(transaction.type)">
              ${{ transaction.amount ? transaction.amount.toFixed(2) : '0.00' }}
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

    <hr class="section-divider">

    <section class="section create-transaction-section">
      <h2 class="section-title">Create New Transaction</h2>
      <p class="section-description">Initiate a new deposit, withdrawal, or transfer.</p>

      <form @submit.prevent="submitTransaction" class="transaction-form">
        <div class="form-grid">
          <div class="form-group">
            <label for="newAccountId">Your Account:</label>
            <select id="newAccountId" v-model="newTransactionForm.accountId" class="form-input" required>
              <option value="" disabled>Select your account</option>
              <option v-for="account in userAccounts" :key="account.id" :value="account.id">
                {{ account.type }} (ID: {{ account.id.substring(0, 8) }}...) - Balance:
                ${{ account.balance ? account.balance.toFixed(2) : '0.00' }}
              </option>
            </select>
            <span v-if="formErrors.accountId" class="error-message">{{ formErrors.accountId }}</span>
          </div>

          <div class="form-group">
            <label for="newType">Transaction Type:</label>
            <select id="newType" v-model="newTransactionForm.type" @change="handleTypeChange" class="form-input"
                    required>
              <option value="" disabled>Select type</option>
              <option v-for="type in transactionTypes" :key="type" :value="type">{{ type.toLowerCase() }}</option>
            </select>
            <span v-if="formErrors.type" class="error-message">{{ formErrors.type }}</span>
          </div>

          <div class="form-group">
            <label for="newAmount">Amount:</label>
            <input type="number" id="newAmount" v-model.number="newTransactionForm.amount" class="form-input" required
                   min="0.01" step="0.01"/>
            <span v-if="formErrors.amount" class="error-message">{{ formErrors.amount }}</span>
          </div>

          <div class="form-group" v-if="newTransactionForm.type === 'TRANSFER'">
            <label for="newRecipientAccountId">Recipient Account ID:</label>
            <input type="text" id="newRecipientAccountId" v-model="newTransactionForm.recipientAccountId"
                   class="form-input" placeholder="Enter recipient account ID"/>
            <span v-if="formErrors.recipientAccountId" class="error-message">{{ formErrors.recipientAccountId }}</span>
          </div>

          <div class="form-group form-group-full">
            <label for="newDetails">Details (optional):</label>
            <textarea id="newDetails" v-model="newTransactionForm.details" class="form-input" maxlength="255"
                      rows="2"></textarea>
            <span v-if="formErrors.details" class="error-message">{{ formErrors.details }}</span>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="submit-btn">
            Create Transaction
          </button>
          <button type="button" @click="resetForm" class="reset-btn">Reset Form</button>
        </div>

        <div v-if="transactionCreationError" class="error-message transaction-error">{{
            transactionCreationError
          }}
        </div>
        <div v-if="transactionCreationSuccess" class="success-message transaction-success">{{
            transactionCreationSuccess
          }}
        </div>
      </form>
    </section>
  </div>
</template>

<script setup>
import {ref, onMounted, watch, reactive} from 'vue';
import {storeToRefs} from 'pinia';
import {useTransactionsStore} from '@/stores/transactionsStore.js';
import {useAuthStore} from '@/stores/authStore.js';
import {useAccountsStore} from '@/stores/acccountsStore.js';

const transactionsStore = useTransactionsStore();
const accountsStore = useAccountsStore();
const {transactions, error} = storeToRefs(transactionsStore); // Removed 'loading' from destructuring

const {accounts: userAccounts} = storeToRefs(accountsStore);


const newTransactionForm = reactive({
  accountId: '',
  type: '',
  amount: null,
  details: '',
  recipientAccountId: '',
});

const formErrors = reactive({});
const transactionCreationError = ref(null);
const transactionCreationSuccess = ref(null);

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
  await fetchUserAccounts();
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
}, {deep: true});

async function fetchUserAccounts() {
  await accountsStore.fetchAccounts();
  if (userAccounts.value.length > 0) {
    newTransactionForm.accountId = userAccounts.value[0].id;
  }
}

function validateForm() {
  formErrors.accountId = '';
  formErrors.type = '';
  formErrors.amount = '';
  formErrors.recipientAccountId = '';
  formErrors.details = '';
  transactionCreationError.value = null;
  transactionCreationSuccess.value = null;

  let isValid = true;

  if (!newTransactionForm.accountId) {
    formErrors.accountId = 'Your account is required.';
    isValid = false;
  }
  if (!newTransactionForm.type) {
    formErrors.type = 'Transaction type is required.';
    isValid = false;
  }
  if (newTransactionForm.amount === null || newTransactionForm.amount <= 0) {
    formErrors.amount = 'Amount must be positive.';
    isValid = false;
  }
  if (newTransactionForm.type === 'TRANSFER' && !newTransactionForm.recipientAccountId) {
    formErrors.recipientAccountId = 'Recipient account ID is required for transfers.';
    isValid = false;
  }
  if (newTransactionForm.details && newTransactionForm.details.length > 255) {
    formErrors.details = 'Details cannot exceed 255 characters.';
    isValid = false;
  }

  return isValid;
}

async function submitTransaction() {
  if (!validateForm()) {
    return;
  }

  try {
    const payload = {
      accountId: newTransactionForm.accountId,
      type: newTransactionForm.type,
      amount: newTransactionForm.amount,
      details: newTransactionForm.details || null,
      recipientAccountId: newTransactionForm.type === 'TRANSFER' ? (newTransactionForm.recipientAccountId || null) : null,
    };

    await transactionsStore.createTransaction(payload);
    transactionCreationSuccess.value = 'Transaction created successfully!';
    resetForm();
    await applyFilters();
  } catch (err) {
    transactionCreationError.value = transactionsStore.error;
    console.error('Error submitting new transaction:', err);
  }
}

function handleTypeChange() {
  if (newTransactionForm.type !== 'TRANSFER') {
    newTransactionForm.recipientAccountId = '';
  }
  if (formErrors.recipientAccountId && newTransactionForm.type !== 'TRANSFER') {
    formErrors.recipientAccountId = '';
  }
}

function resetForm() {
  Object.assign(newTransactionForm, {
    accountId: userAccounts.value.length > 0 ? userAccounts.value[0].id : '',
    type: '',
    amount: null,
    details: '',
    recipientAccountId: '',
  });
  Object.keys(formErrors).forEach(key => formErrors[key] = '');
  transactionCreationError.value = null;
  transactionCreationSuccess.value = null;
}

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
/* Base Layout & Typography */
.page-container {
  padding: 2.5rem;
  max-width: 1200px;
  margin: 0 auto;
  font-family: 'Inter', sans-serif;
  background-color: #f0f2f5;
  color: #334e68;
}

.header {
  text-align: center;
  margin-bottom: 3rem;
}

.header h1 {
  font-size: 2.8rem;
  color: #263238;
  margin-bottom: 0.5rem;
  font-weight: 700;
}

.header p {
  font-size: 1.1rem;
  color: #607d8b;
}

/* Common Section Styling */
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

/* --- Transaction Creation Form Styles --- */
.create-transaction-section {
  padding: 2.5rem;
}

.transaction-form {
  margin-top: 1.5rem;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-size: 0.9rem;
  color: #546e7a;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.form-input {
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

.form-input:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.2);
}

.form-input::placeholder {
  color: #90a4ae;
}

.form-group-full {
  grid-column: 1 / -1;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.submit-btn,
.reset-btn {
  padding: 0.9rem 2rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.submit-btn {
  background-color: #28a745;
  color: #ffffff;
}

.submit-btn:hover {
  background-color: #218838;
  transform: translateY(-1px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.15);
}

.submit-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.reset-btn {
  background-color: #6c757d;
  color: #ffffff;
}

.reset-btn:hover {
  background-color: #5a6268;
  transform: translateY(-1px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.15);
}

.reset-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.error-message {
  color: #d32f2f;
  background-color: #ffebee;
  padding: 0.8rem 1.2rem;
  border-radius: 8px;
  border: 1px solid #d32f2f;
  margin-top: 0.5rem;
  font-size: 0.95rem;
  text-align: left;
}

.transaction-error, .transaction-success {
  margin-top: 1.5rem;
  text-align: center;
}

.success-message {
  color: #2e7d32;
  background-color: #e8f5e9;
  padding: 0.8rem 1.2rem;
  border-radius: 8px;
  border: 1px solid #2e7d32;
  font-size: 0.95rem;
}

/* Horizontal Rule for Section Separation */
.section-divider {
  border: 0;
  height: 1px;
  background-image: linear-gradient(to right, rgba(0, 0, 0, 0), rgba(189, 189, 189, 0.75), rgba(0, 0, 0, 0));
  margin: 3.5rem 0;
}

/* --- Filter Section Styles (Existing) --- */
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

/* --- Loading and Empty State Messages --- */
/* Removed .loading-message and .spinner styles as they are no longer used */

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

/* --- Transaction Card Grid Styles (Existing) --- */
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

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

.card.type-deposit {
  border-left-color: #2e7d32;
}

.card.type-withdrawal {
  border-left-color: #c62828;
}

.card.type-transfer {
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
</style>