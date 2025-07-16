<template>
  <div class="page-container">
    <header class="header">
      <h1>Admin Loans</h1>
      <p>Review and manage all user loan applications.</p>
    </header>

    <section class="section filter-section">
      <h2 class="section-title">Filter Loans</h2>
      <p class="section-description">Use filters to find specific loans by type, user, or status.</p>

      <div class="filter-controls-grid">
        <div class="filter-group">
          <label for="loanType">Loan Type:</label>
          <select id="loanType" v-model="filters.type" class="filter-input">
            <option value="">All Types</option>
            <option v-for="type in loanTypes" :key="type" :value="type">
              {{ formatLabel(type) }}
            </option>
          </select>
        </div>

        <div class="filter-group">
          <label for="loanStatus">Status:</label>
          <select id="loanStatus" v-model="filters.status" class="filter-input">
            <option value="">All Statuses</option>
            <option v-for="status in loanStatuses" :key="status" :value="status">
              {{ formatLabel(status) }}
            </option>
          </select>
        </div>

        <div class="filter-group">
          <label for="userId">User ID:</label>
          <input id="userId" v-model="filters.userId" class="filter-input" placeholder="e.g. usr_123"/>
        </div>

        <div class="filter-group">
          <label for="username">Username:</label>
          <input id="username" v-model="filters.username" class="filter-input" placeholder="e.g. johndoe"/>
        </div>

        <div class="filter-group">
          <label for="email">Email:</label>
          <input id="email" v-model="filters.email" class="filter-input" placeholder="e.g. user@example.com"/>
        </div>

        <div class="filter-group">
          <label for="startDate">Start Date:</label>
          <input type="date" id="startDate" v-model="filters.startDate" class="filter-input"/>
        </div>

        <div class="filter-group">
          <label for="endDate">End Date:</label>
          <input type="date" id="endDate" v-model="filters.endDate" class="filter-input"/>
        </div>

        <div class="filter-group">
          <label for="minAmount">Min Amount:</label>
          <input type="number" id="minAmount" v-model.number="filters.minAmount" min="0" step="0.01"
                 class="filter-input" placeholder="0.00"/>
        </div>

        <div class="filter-group">
          <label for="maxAmount">Max Amount:</label>
          <input type="number" id="maxAmount" v-model.number="filters.maxAmount" min="0" step="0.01"
                 class="filter-input" placeholder="0.00"/>
        </div>

        <div class="filter-buttons">
          <button @click="clearFilters" class="clear-filters-btn">Clear Filters</button>
        </div>
      </div>
    </section>

    <section class="section">
      <h2 class="section-title">Pending Loans</h2>

      <div v-if="pendingLoans.length === 0" class="empty-state-message">
        No pending loans found.
      </div>

      <div v-else class="card-grid">
        <div
            v-for="loan in pendingLoans"
            :key="loan.id"
            class="card type-pending"
        >
          <div class="card-header">
            <span class="loan-type">{{ formatLabel(loan.loanType) }}</span>
            <span class="loan-amount">${{ loan.amount.toFixed(2) }}</span>
          </div>

          <div class="card-body">
            <p><strong>Account:</strong> {{ loan.accountId }}</p>
            <p><strong>Interest Rate:</strong> {{ loan.interestRate }}%</p>
            <p><strong>Duration:</strong> {{ loan.termInMonths }} months</p>
            <p><strong>Status:</strong>
              <span class="status-tag status-pending">{{ formatLabel(loan.status) }}</span>
            </p>

            <div class="action-buttons">
              <button class="btn-accept">Accept</button>
              <button class="btn-reject">Reject</button>
              <button class="btn-propose">Propose Changes</button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="section">
      <h2 class="section-title">Filtered Loans</h2>

      <div v-if="filteredLoans.length === 0" class="empty-state-message">
        No filtered loans found.
      </div>

      <div v-else class="card-grid">
        <div
            v-for="loan in filteredLoans"
            :key="loan.id"
            class="card"
        >
          <div class="card-header">
            <span class="loan-type">{{ formatLabel(loan.type) }}</span>
            <span class="loan-amount">${{ loan.amount.toFixed(2) }}</span>
          </div>

          <div class="card-body">
            <p><strong>Account:</strong> {{ loan.accountId }}</p>
            <p><strong>Interest Rate:</strong> {{ loan.interestRate }}%</p>
            <p><strong>Duration:</strong> {{ loan.termInMonths }} months</p>
            <p><strong>Status:</strong>
              <span class="status-tag" :class="'status-' + loan.status.toLowerCase()">
                {{ formatLabel(loan.status) }}
              </span>
            </p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { reactive, watch, onMounted, computed } from 'vue'
import { useAdminLoansStore } from '@/stores/admin/adminLoansStore.js'

const filters = reactive({
  type: '',
  status: '',
  userId: '',
  username: '',
  email: '',
  startDate: '',
  endDate: '',
  minAmount: null,
  maxAmount: null,
})

const loanTypes = ['personal_loan', 'auto_loan', 'mortgage', 'student_loan']
const loanStatuses = ['pending', 'approved', 'rejected', 'active', 'repaid', 'overdue', 'defaulted', 'cancelled']

const adminLoansStore = useAdminLoansStore()

const pendingLoans = computed(() =>
    adminLoansStore.loans.filter(loan => loan.status === 'PENDING')
)

const filteredLoans = computed(() =>
    adminLoansStore.paginatedLoans.filter(loan => loan.status !== 'PENDING')
)

function clearFilters() {
  filters.type = ''
  filters.status = ''
  filters.userId = ''
  filters.username = ''
  filters.email = ''
  filters.startDate = ''
  filters.endDate = ''
  filters.minAmount = null
  filters.maxAmount = null
  adminLoansStore.resetPage()
}

function formatLabel(value) {
  if (!value) return ''
  return value
      .replace(/_/g, ' ')
      .replace(/\b\w/g, c => c.toUpperCase())
}

function fetchLoans() {
  const cleanFilters = {}
  Object.entries(filters).forEach(([key, val]) => {
    if (val !== '' && val !== null && val !== undefined) {
      cleanFilters[key] = val
    }
  })
  adminLoansStore.fetchFilteredLoans(cleanFilters, adminLoansStore.currentPage)
}

onMounted(() => {
  document.title = "Admin Loans"
  fetchLoans()
})

watch(
    () => [filters, adminLoansStore.currentPage],
    () => {
      fetchLoans()
    },
    { deep: true }
)

function nextPage() {
  adminLoansStore.incrementPage()
}

function prevPage() {
  adminLoansStore.decrementPage()
}
</script>


<style scoped>
.page-container {
  padding: 2.5rem;
  max-width: 1200px;
  margin: 0 auto;
  font-family: 'Inter', sans-serif;
  background-color: #f0f2f5;
  color: #334e68;
}

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

.loan-type {
  font-size: 1rem;
  font-weight: 600;
  padding: 0.4rem 0.8rem;
  border-radius: 6px;
  text-transform: capitalize;
  display: inline-block;
}

.type-pending {
  border-left-color: #fbc02d;
}

.type-approved {
  border-left-color: #2e7d32;
}

.type-rejected {
  border-left-color: #c62828;
}

.type-active {
  border-left-color: #0277bd;
}

.type-repaid {
  border-left-color: #388e3c;
}

.type-overdue {
  border-left-color: #f57c00;
}

.type-defaulted {
  border-left-color: #b71c1c;
}

.type-cancelled {
  border-left-color: #616161;
}

.loan-amount {
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

.status-pending {
  background-color: transparent;
  color: #fbc02d;
}

.status-approved {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.status-rejected {
  background-color: #ffebee;
  color: #d32f2f;
}

.status-active {
  background-color: #e1f5fe;
  color: #0277bd;
}

.status-repaid {
  background-color: #c8e6c9;
  color: #388e3c;
}

.status-overdue {
  background-color: #fff3e0;
  color: #f57c00;
}

.status-defaulted {
  background-color: #fbe9e7;
  color: #b71c1c;
}

.status-cancelled {
  background-color: #e0e0e0;
  color: #616161;
}

.action-buttons {
  margin-top: 1rem;
  display: flex;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.btn-accept,
.btn-reject,
.btn-propose {
  padding: 0.6rem 1.2rem;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  border: none;
  user-select: none;
  transition: background-color 0.3s ease;
}

.btn-accept {
  background-color: #2e7d32;
  color: white;
}

.btn-accept:hover {
  background-color: #276c2c;
}

.btn-reject {
  background-color: #c62828;
  color: white;
}

.btn-reject:hover {
  background-color: #b71c1c;
}

.btn-propose {
  background-color: #fbc02d;
  color: #4a4a4a;
}

.btn-propose:hover {
  background-color: #d4af1f;
}

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
</style>


