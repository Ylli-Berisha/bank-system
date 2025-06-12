<template>
  <div class="page-container">
    <header class="header">
      <h1>Your Loans</h1>
      <p>Detailed overview of your loan accounts</p>
    </header>

    <section class="section filter-section">
      <h2 class="section-title">Filter Loans</h2>
      <p class="section-description">Refine your loan view using the controls below.</p>

      <div class="filter-controls-grid">
        <div class="filter-group">
          <label for="loanTypeFilter">Type:</label>
          <select id="loanTypeFilter" v-model="filters.loanType" class="filter-input">
            <option value="">All Types</option>
            <option v-for="type in loanTypes" :key="type" :value="type">{{ formatLoanType(type) }}</option>
          </select>
        </div>

        <div class="filter-group">
          <label for="loanStatusFilter">Status:</label>
          <select id="loanStatusFilter" v-model="filters.status" class="filter-input">
            <option value="">All Statuses</option>
            <option v-for="status in loanStatuses" :key="status" :value="status">{{ formatStatus(status) }}</option>
          </select>
        </div>

        <div class="filter-group">
          <label for="minAmountFilter">Min Amount:</label>
          <input type="number" id="minAmountFilter" v-model.number="filters.minAmount" class="filter-input" placeholder="e.g., 100.00" />
        </div>
        <div class="filter-group">
          <label for="maxAmountFilter">Max Amount:</label>
          <input type="number" id="maxAmountFilter" v-model.number="filters.maxAmount" class="filter-input" placeholder="e.g., 5000.00" />
        </div>

        <div class="filter-group">
          <label for="startDateFilter">Start Date:</label>
          <input type="date" id="startDateFilter" v-model="filters.startDate" class="filter-input" />
        </div>
        <div class="filter-group">
          <label for="endDateFilter">End Date:</label>
          <input type="date" id="endDateFilter" v-model="filters.endDate" class="filter-input" />
        </div>

        <div class="filter-group filter-group-search">
          <label for="searchQueryFilter">Search:</label>
          <input type="text" id="searchQueryFilter" v-model="filters.query" class="filter-input" placeholder="By ID or details" />
        </div>

        <div class="filter-buttons">
          <button @click="clearFilters" class="clear-filters-btn">Clear Filters</button>
        </div>
      </div>
    </section>

    <section class="section">
      <h2 class="section-title">All Loans Overview</h2>
      <p v-if="loansError" class="error">{{ loansError }}</p>

      <div v-if="loans.length === 0 && !loansError" class="empty-state-message">
        No loans found matching your criteria.
      </div>

      <div v-else class="card-grid">
        <div v-for="loan in loans" :key="loan.id" :class="['card', loanStatusClass(loan.status)]">
          <h3>{{ formatLoanType(loan.loanType) }} Loan</h3>
          <p><strong>Amount:</strong> ${{ loan.amount.toFixed(2) }}</p>
          <p><strong>Interest Rate:</strong> {{ loan.interestRate.toFixed(2) }}%</p>
          <p><strong>Term:</strong> {{ loan.termInMonths }} Months</p>
          <p><strong>Status:</strong> {{ formatStatus(loan.status) }}</p>
          <p v-if="loan.startDate"><strong>Start Date:</strong> {{ formatDate(loan.startDate) }}</p>
          <p v-if="loan.endDate"><strong>End Date:</strong> {{ formatDate(loan.endDate) }}</p>
          <p v-if="loan.createdAt"><strong>Applied On:</strong> {{ formatDate(loan.createdAt) }}</p>
          <p v-if="loan.paidOffDate"><strong>Paid Off Date:</strong> {{ formatDate(loan.paidOffDate) }}</p>
          <p><strong>Loan ID:</strong> {{ loan.id }}</p>
        </div>
      </div>
    </section>

    <section class="create-loan-section">
      <h2 class="section-title">New Loan Application</h2>
      <p class="section-description">Ready to apply for a new loan? Find options and apply here.</p>

      <button @click="showForm = !showForm" :class="['toggle-form-btn', { 'is-open': showForm }]">
        <span v-if="!showForm">Apply For New Loan</span>
        <span v-else>Cancel Application</span>
        <svg v-if="!showForm" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-plus-circle"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="16"></line><line x1="8" y1="12" x2="16" y2="12"></line></svg>
        <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-x-circle"><circle cx="12" cy="12" r="10"></circle><line x1="15" y1="9" x2="9" y2="15"></line><line x1="9" y1="9" x2="15" y2="15"></line></svg>
      </button>

      <Transition name="fade-slide">
        <form v-if="showForm" @submit.prevent="openNewLoanConfirmModal" class="create-form-card">
          <h3>New Loan Details</h3>

          <div class="form-group">
            <label for="loanType">Loan Type:</label>
            <select id="loanType" v-model="newLoan.loanType" required class="form-input">
              <option value="" disabled>Select a loan type</option>
              <option v-for="type in loanTypes" :key="type" :value="type">
                {{ formatLoanType(type) }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="sourceAccount">Source Account:</label>
            <select id="sourceAccount" v-model="selectedSourceAccountId" required class="form-input">
              <option value="" disabled>Select an account</option>
              <option v-for="account in activeAccountsForSelection" :key="account.id" :value="account.id">
                {{ formatAccountDisplay(account) }}
              </option>
            </select>
            <p v-if="!activeAccountsForSelection.length" class="text-sm text-gray-500 mt-1">
              You need at least one active account to apply for a loan.
            </p>
          </div>

          <div class="form-group">
            <label for="amount">Loan Amount:</label>
            <input
                id="amount"
                type="number"
                v-model.number="newLoan.amount"
                min="0.01"
                step="0.01"
                required
                class="form-input"
                placeholder="e.g., 1000.00"
            />
          </div>

          <div class="form-group">
            <label for="interestRate">Interest Rate (%):</label>
            <input
                id="interestRate"
                type="number"
                v-model.number="newLoan.interestRate"
                min="0.01"
                step="0.01"
                required
                class="form-input"
                placeholder="e.g., 5.50"
            />
          </div>

          <div class="form-group">
            <label for="termInMonths">Loan Term (Months):</label>
            <input
                id="termInMonths"
                type="number"
                v-model.number="newLoan.termInMonths"
                min="1"
                required
                class="form-input"
                placeholder="e.g., 60"
            />
          </div>

          <div class="status-box">
            <strong>Status:</strong> Pending Approval
          </div>

          <button type="submit" class="submit-application-btn">
            Submit Loan Application
            <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-arrow-right"><line x1="5" y1="12" x2="19" y2="12"></line><polyline points="12 5 19 12 12 19"></polyline></svg>
          </button>
          <p v-if="createError" class="error">{{ createError }}</p>
        </form>
      </Transition>
    </section>

    <ConfirmationModal
        :is-open="showConfirmationModal"
        :title="confirmationModalTitle"
        :confirm="handleConfirmationModalConfirm"
        :cancel="handleConfirmationModalCancel"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, reactive } from 'vue'
import { storeToRefs } from 'pinia'
import { useLoansStore } from '@/stores/loansStore.js'
import { useAccountsStore } from '@/stores/acccountsStore.js'
import ConfirmationModal from '@/components/ConfirmModal.vue';

const loansStore = useLoansStore()
const accountsStore = useAccountsStore()

const {
  loans,
  error: loansError,
  loanTypes,
  createError,
  // applyForNewLoan // applyForNewLoan is a method, not a ref, so it's not from storeToRefs
} = storeToRefs(loansStore) // Removed applyForNewLoan from storeToRefs


const {
  accounts
} = storeToRefs(accountsStore)

const showForm = ref(false)
const selectedSourceAccountId = ref('')

const newLoan = ref({
  loanType: '',
  amount: 0.00,
  interestRate: 0.00,
  termInMonths: 1,
})

const showConfirmationModal = ref(false)
const confirmationModalTitle = ref('')
const currentActionType = ref(null)

// Define loan statuses for filter dropdown
const loanStatuses = ['ACTIVE', 'PENDING', 'PAID_OFF', 'REJECTED', 'DEFAULTED'];

// Reactive object for filters, similar to transactions view
const filters = reactive({
  startDate: '',
  endDate: '',
  loanType: '', // Use loanType instead of type for clarity with loans
  status: '',
  minAmount: null,
  maxAmount: null,
  query: ''
});

let searchDebounceTimer = null;

// Re-using activeAccountsForSelection as it's useful for the new loan form
const activeAccountsForSelection = computed(() =>
    accounts.value.filter(acc => acc.status === 'ACTIVE')
);

onMounted(async () => {
  document.title = "Loans"
  await accountsStore.fetchAccounts() // Fetch accounts first
  await loansStore.fetchLoanTypes() // Fetch loan types

  // Apply filters initially to load loans based on default state or any initial URL params if present
  await applyFilters();

  if (loanTypes.value.length > 0) {
    newLoan.value.loanType = loanTypes.value[0]
  }

  if (activeAccountsForSelection.value.length > 0) {
    selectedSourceAccountId.value = activeAccountsForSelection.value[0].id;
  }
})

// Watch filters for changes and apply debouncing for query
watch(filters, (newFilters, oldFilters) => {
  if (newFilters.query !== oldFilters.query) {
    if (searchDebounceTimer) {
      clearTimeout(searchDebounceTimer);
    }
    searchDebounceTimer = setTimeout(() => {
      applyFilters();
    }, 500); // 500ms debounce
  } else {
    // If other filters change, apply filters immediately
    applyFilters();
  }
}, { deep: true }); // Deep watch is needed for reactive objects like filters.value

// Function to apply filters
async function applyFilters() {
  const currentFilters = filters; // filters is already reactive

  const hasActiveFilter = Object.values(currentFilters).some(value => {
    if (typeof value === 'number') {
      return value !== null && !isNaN(value);
    }
    if (typeof value === 'string') {
      return value.trim() !== '';
    }
    return false;
  });

  // Create a clean payload for the API call
  const cleanedFilters = {
    loanType: currentFilters.loanType || undefined,
    status: currentFilters.status || undefined,
    startDate: currentFilters.startDate || undefined,
    endDate: currentFilters.endDate || undefined,
    minAmount: currentFilters.minAmount !== null ? currentFilters.minAmount : undefined,
    maxAmount: currentFilters.maxAmount !== null ? currentFilters.maxAmount : undefined,
    query: currentFilters.query || undefined
  };

  if (hasActiveFilter) {
    await loansStore.fetchFilteredLoans(cleanedFilters);
  } else {
    await loansStore.fetchAllLoans();
  }
}

// Function to clear all filters
function clearFilters() {
  Object.assign(filters, {
    startDate: '',
    endDate: '',
    loanType: '',
    status: '',
    minAmount: null,
    maxAmount: null,
    query: ''
  });
  // applyFilters() will be called automatically by the watch effect
}


function formatDate(isoString) {
  if (!isoString) return ''
  const date = new Date(isoString)
  return date.toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

function formatLoanType(type) {
  return type ? type.toString().replace(/_/g, ' ').replace(/\b\w/g, char => char.toUpperCase()) : '';
}

function formatStatus(status) {
  return status ? status.toString().replace(/_/g, ' ').replace(/\b\w/g, char => char.toUpperCase()) : '';
}

function formatAccountDisplay(account) {
  if (!account) return '';
  return `${account.type.replace('_', ' ')} (ID: ${account.id.substring(0, 8)}..., Balance: $${account.balance.toFixed(2)})`;
}

function openNewLoanConfirmModal() {
  if (!newLoan.value.loanType || newLoan.value.amount <= 0 || newLoan.value.interestRate <= 0 || newLoan.value.termInMonths < 1 || !selectedSourceAccountId.value) {
    createError.value = 'Please fill in all loan details and select a source account correctly.';
    return;
  }

  const selectedAccount = activeAccountsForSelection.value.find(acc => acc.id === selectedSourceAccountId.value);
  const accountDisplay = selectedAccount ? formatAccountDisplay(selectedAccount) : 'selected account';

  confirmationModalTitle.value = `Are you sure you want to apply for a ${formatLoanType(newLoan.value.loanType)} loan of $${newLoan.value.amount.toFixed(2)} with an interest rate of ${newLoan.value.interestRate.toFixed(2)}% over ${newLoan.value.termInMonths} months, linked to your ${accountDisplay}?`
  currentActionType.value = 'applyLoan'
  showConfirmationModal.value = true
}

async function handleConfirmationModalConfirm() {
  try {
    if (currentActionType.value === 'applyLoan') {
      await submitLoanApplication();
    }
  } catch (err) {
    console.error("Error during modal action:", err);
  } finally {
    resetConfirmationModalState();
    // After confirmation, re-fetch loans to update the list, applyFilters will do this
    await applyFilters();
  }
}

function handleConfirmationModalCancel() {
  resetConfirmationModalState();
}

function resetConfirmationModalState() {
  showConfirmationModal.value = false;
  confirmationModalTitle.value = '';
  currentActionType.value = null;
}

async function submitLoanApplication() {
  createError.value = null
  try {
    const loanPayload = { ...newLoan.value };

    await loansStore.applyForNewLoan(selectedSourceAccountId.value, loanPayload);

    newLoan.value = {
      loanType: loanTypes.value[0] || '',
      amount: 0.00,
      interestRate: 0.00,
      termInMonths: 1,
    };
    selectedSourceAccountId.value = activeAccountsForSelection.value[0]?.id || '';

    showForm.value = false;
  } catch (err) {
    throw err;
  }
}

// Function to dynamically apply status-based classes to loan cards
function loanStatusClass(status) {
  switch (status) {
    case 'ACTIVE':
      return 'active-loan';
    case 'PENDING':
      return 'pending-loan';
    case 'PAID_OFF':
      return 'paid-off-loan';
    case 'REJECTED':
      return 'rejected-loan';
    case 'DEFAULTED':
      return 'defaulted-loan';
    default:
      return '';
  }
}
</script>

<style scoped>
/* Base Layout & Typography (from original, mostly unchanged) */
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

.section-description {
  text-align: center;
  color: #555;
  margin-bottom: 2rem;
  font-size: 1.1rem;
}

/* --- Filter Section Styles (Copied from TransactionsView) --- */
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
  grid-column: span 2; /* Allows search input to span two columns */
  min-width: 300px;
}

.filter-buttons {
  grid-column: 1 / -1; /* Buttons span all columns */
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

/* --- Loan Card Grid Styles (Updated) --- */
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
  border-left: 6px solid #2d8cf0; /* Default border color */
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
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

.empty-state-message {
  text-align: center;
  font-size: 1.1rem;
  color: #7f8c8d;
  padding: 2rem 0;
  background-color: #f0f4f7;
  border-radius: 8px;
  margin-top: 2rem;
  border: 1px solid #cfd8dc; /* Added border for consistency */
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

/* Specific Loan Status Card Styles (new for dynamic coloring) */
.card.active-loan {
  border-left-color: #3498db; /* Blue */
  background-color: #eaf2fb;
}

.card.pending-loan {
  border-left-color: #f1c40f; /* Yellow */
  background-color: #fffde7;
}

.card.paid-off-loan {
  border-left-color: #2ecc71; /* Green */
  background-color: #eafaf1;
}

.card.rejected-loan {
  border-left-color: #e74c3c; /* Red */
  background-color: #fdeded;
}

.card.defaulted-loan {
  border-left-color: #555; /* Darker for defaulted */
  background-color: #e0e0e0;
}


/* --- Create Loan Section (Original) --- */
.create-loan-section {
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
  max-height: 700px;
  padding-top: 2rem;
  padding-bottom: 2rem;
  margin-top: 2rem;
  margin-bottom: 0;
}

/* Original loan status sections are removed in template,
   but classes remain for dynamic styling on cards */
.pending-section .card.pending { /* This rule is now effectively dead if you removed the separate sections */
  background-color: #fffde7;
  border-left-color: #f1c40f;
  box-shadow: 0 5px 15px rgba(241, 196, 15, 0.1);
}

.paid-off-section .card.paid-off { /* This rule is now effectively dead */
  background-color: #eafaf1;
  border-left-color: #2ecc71;
  box-shadow: 0 5px 15px rgba(46, 204, 113, 0.1);
}

.active-loan { /* This rule is now handled by .card.active-loan above */
  border-left-color: #3498db;
}
</style>