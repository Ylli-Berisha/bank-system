<template>
  <div class="page-container">
    <header class="header">
      <h1>User Overview</h1>
      <p>Overview of user details and linked accounts, transactions, and loans</p>
    </header>

    <div v-if="loading" class="loading-section">
      <p>Loading user composition...</p>
    </div>

    <div v-else-if="error" class="error-section">
      <p>{{ error }}</p>
    </div>

    <div v-else class="content-wrapper">
      <section class="card user-info-card">
        <h2>User Info</h2>
        <div class="info-grid">
          <div class="info-item"><strong>Name:</strong> <span>{{ user?.firstName && user?.lastName ? user.firstName + ' ' + user.lastName : '-' }}</span></div>
          <div class="info-item"><strong>Username:</strong> <span>{{ user?.username || '-' }}</span></div>
          <div class="info-item"><strong>Email:</strong> <span>{{ user?.email || '-' }}</span></div>
          <div class="info-item"><strong>Phone:</strong> <span>{{ user?.phoneNumber || '-' }}</span></div>
          <div class="info-item"><strong>Address:</strong> <span>{{ user?.address || '-' }}</span></div>
          <div class="info-item"><strong>Birthdate:</strong> <span>{{ user?.birthDate ? new Date(user.birthDate).toLocaleDateString() : '-' }}</span></div>
          <div class="info-item"><strong>Active:</strong> <span :class="user?.active ? 'status-active' : 'status-inactive'">{{ user?.active ? 'Yes' : 'No' }}</span></div>
          <div class="info-item"><strong>Created At:</strong> <span>{{ user?.createdAt ? new Date(user.createdAt).toLocaleDateString() : '-' }}</span></div>
        </div>
      </section>

      <section class="card accounts-card">
        <h2>Accounts</h2>
        <ul v-if="accounts.length" class="list-cards">
          <li v-for="acc in accounts" :key="acc.id" class="list-item account-item">
            <div class="account-details">
              <div><strong>ID:</strong> {{ acc.id }}</div>
              <div><strong>Type:</strong> {{ acc.type }}</div>
            </div>
            <div class="account-balance">{{ acc.balance.toFixed(2) }} $</div>
            <div class="account-status" :class="`status-${acc.status.toLowerCase()}`">{{ acc.status }}</div>
          </li>
        </ul>
        <p v-else class="no-data">No accounts found.</p>
        <div class="pagination" v-if="accounts.length">
          <button @click="prevAccounts" :disabled="accountsPage === 1">Prev</button>
          <span>Page {{ accountsPage }} of {{ accountsTotalPages }}</span>
          <button @click="nextAccounts" :disabled="accountsPage === accountsTotalPages">Next</button>
        </div>
      </section>

      <section class="card transactions-card">
        <h2>Transactions</h2>
        <ul v-if="transactions.length" class="list-cards">
          <li v-for="tx in transactions" :key="tx.id" class="list-item transaction-item">
            <div class="transaction-header">
              <span class="transaction-type">{{ tx.type }}</span>
              <span class="transaction-amount" :class="tx.type === 'DEPOSIT' ? 'amount-deposit' : 'amount-withdraw'">
                {{ tx.type === 'DEPOSIT' ? '+' : '-' }}{{ tx.amount.toFixed(2) }} {{ tx.currency }}
              </span>
            </div>
            <div class="transaction-details">
              <div class="transaction-info">
                <span><strong>ID:</strong> {{ tx.id }}</span>
                <span><strong>Account:</strong> {{ tx.accountId }}</span>
                <span><strong>Date:</strong> {{ tx.createdAt ? new Date(tx.createdAt).toLocaleDateString() : '-' }}</span>
              </div>
              <p class="transaction-description">{{ tx.details || 'No details provided.' }}</p>
            </div>
            <div class="transaction-footer">
              <span class="transaction-recipient">{{ tx.recipientAccountId ? `To: ${tx.recipientAccountId}` : 'No recipient' }}</span>
              <span class="transaction-status" :class="`status-${tx.status.toLowerCase()}`">{{ tx.status }}</span>
            </div>
          </li>
        </ul>
        <p v-else class="no-data">No transactions found.</p>
        <div class="pagination" v-if="transactions.length">
          <button @click="prevTransactions" :disabled="transactionsPage === 1">Prev</button>
          <span>Page {{ transactionsPage }} of {{ transactionsTotalPages }}</span>
          <button @click="nextTransactions" :disabled="transactionsPage === transactionsTotalPages">Next</button>
        </div>
      </section>

      <section class="card loans-card">
        <h2>Loans</h2>
        <ul v-if="loans.length" class="list-cards">
          <li v-for="loan in loans" :key="loan.id" class="list-item loan-item">
            <div class="loan-header">
              <span class="loan-id">ID: {{ loan.id }}</span>
              <span class="loan-amount">{{ loan.amount.toFixed(2) }} $
                <span class="loan-status" :class="`status-${loan.status.toLowerCase()}`">{{ loan.status }}</span>
              </span>
            </div>
            <div class="loan-details">
              <p><strong>Account:</strong> {{ loan.accountId }}</p>
              <p><strong>Type:</strong> {{ loan.loanType.replace(/_/g, ' ') }}</p>
              <p><strong>Interest Rate:</strong> {{ loan.interestRate }}%</p>
              <p><strong>Monthly Installment:</strong> ${{ loan.monthlyInstallment.toFixed(2) }}</p>
            </div>
            <div class="loan-schedule">
              <span><strong>Due Date:</strong> {{ loan.endDate ? new Date(loan.endDate).toLocaleDateString() : '-' }}</span>
              <span><strong>Next Installment:</strong> {{ loan.nextInstallmentDate ? new Date(loan.nextInstallmentDate).toLocaleDateString() : '-' }}</span>
            </div>
          </li>
        </ul>
        <p v-else class="no-data">No loans found.</p>
        <div class="pagination" v-if="loans.length">
          <button @click="prevLoans" :disabled="loansPage === 1">Prev</button>
          <span>Page {{ loansPage }} of {{ loansTotalPages }}</span>
          <button @click="nextLoans" :disabled="loansPage === loansTotalPages">Next</button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { onMounted, computed, watch } from 'vue'
import { useAdminUserCompositionStore } from '@/stores/admin/adminUserComposition.js'

const props = defineProps({
  userId: {
    type: [String, Number],
    required: true
  }
})

const store = useAdminUserCompositionStore()

const loading = computed(() => store.loading)
const error = computed(() => store.error)
const user = computed(() => store.user || null)

const accounts = computed(() => store.accounts || [])
const transactions = computed(() => store.transactions || [])
const loans = computed(() => store.loans || [])

const accountsPage = computed(() => store.accountsCurrentPage + 1)
const accountsTotalPages = computed(() => store.totalAccountsPages)

const transactionsPage = computed(() => store.transactionsCurrentPage + 1)
const transactionsTotalPages = computed(() => store.totalTransactionsPages)

const loansPage = computed(() => store.loansCurrentPage + 1)
const loansTotalPages = computed(() => store.totalLoansPages)

const prevAccounts = () => {
  if (store.accountsCurrentPage > 0) {
    store.fetchAccountsPage(props.userId, store.accountsCurrentPage - 1, store.accountsPageSize)
  }
}
const nextAccounts = () => {
  if (store.accountsCurrentPage < store.totalAccountsPages - 1) {
    store.fetchAccountsPage(props.userId, store.accountsCurrentPage + 1, store.accountsPageSize)
  }
}

const prevTransactions = () => {
  if (store.transactionsCurrentPage > 0) {
    store.fetchTransactionsPage(props.userId, store.transactionsCurrentPage - 1, store.transactionsPageSize)
  }
}
const nextTransactions = () => {
  if (store.transactionsCurrentPage < store.totalTransactionsPages - 1) {
    store.fetchTransactionsPage(props.userId, store.transactionsCurrentPage + 1, store.transactionsPageSize)
  }
}

const prevLoans = () => {
  if (store.loansCurrentPage > 0) {
    store.fetchLoansPage(props.userId, store.loansCurrentPage - 1, store.loansPageSize)
  }
}
const nextLoans = () => {
  if (store.loansCurrentPage < store.totalLoansPages - 1) {
    store.fetchLoansPage(props.userId, store.loansCurrentPage + 1, store.loansPageSize)
  }
}

const fetchData = (id) => {
  if (!id) return
  store.getUserComposition(id)
}

onMounted(() => {
  fetchData(props.userId)
})

watch(() => props.userId, (newUserId) => {
  fetchData(newUserId)
})
</script>


<style scoped>
/* Base Styles */
.page-container {
  padding: 2rem;
  max-width: 1100px; /* Limit content width for better readability */
  margin: 0 auto; /* Center the container */
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; /* Modern, readable font */
  color: #333; /* Darker text for better contrast */
  background-color: #f9fbfd; /* Light background for the page */
}

/* Header Styles */
.header {
  text-align: center;
  margin-bottom: 2.5rem;
}

.header h1 {
  font-size: 2.2rem; /* Larger, more prominent heading */
  margin-bottom: 0.5rem;
  color: #2c3e50; /* Darker, more professional color */
  font-weight: 700;
}

.header p {
  font-size: 1.1rem;
  color: #7f8c8d; /* Muted color for description */
}

/* Card Styles */
.card {
  background: white;
  padding: 2rem; /* Increased padding */
  margin-bottom: 2rem; /* Consistent spacing between cards */
  border-radius: 8px; /* Slightly more rounded corners */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); /* More pronounced, softer shadow */
  border: 1px solid #e0e6ed; /* Subtle border for definition */
}

.card h2 {
  font-size: 1.6rem;
  color: #34495e;
  margin-bottom: 1.5rem; /* Space below heading */
  border-bottom: 1px solid #f0f0f0; /* Subtle separator */
  padding-bottom: 0.8rem;
}

/* Loading and Error Sections */
.loading-section, .error-section {
  text-align: center;
  padding: 3rem;
  font-size: 1.2rem;
  color: #7f8c8d;
}

.error-section {
  color: #e74c3c;
  font-weight: 600;
}

/* User Info Grid */
.user-info-card .info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); /* Better responsiveness */
  gap: 1.2rem 2.5rem; /* Adjusted gap */
}

.user-info-card .info-item {
  font-size: 1rem;
  line-height: 1.5;
  color: #555;
}

.user-info-card strong {
  color: #333;
  min-width: 90px; /* Ensure strong text aligns */
  display: inline-block; /* Allows min-width to work */
}

/* Status Styles (Reused across sections) */
.status-active, .status-approved, .status-completed, .status-success {
  color: white;
  background-color: #2ecc71; /* Green for positive status */
  font-weight: 600;
  padding: 0.25rem 0.8rem;
  border-radius: 20px; /* Pill shape */
  text-transform: capitalize;
  font-size: 0.85rem;
  display: inline-block; /* For proper padding and border-radius */
  text-align: center;
}

.status-inactive, .status-pending, .status-processing, .status-progress {
  color: #2c3e50;
  background-color: #f1c40f; /* Yellow for neutral/pending status */
  font-weight: 600;
  padding: 0.25rem 0.8rem;
  border-radius: 20px;
  text-transform: capitalize;
  font-size: 0.85rem;
  display: inline-block;
  text-align: center;
}

.status-closed, .status-rejected, .status-failed, .status-cancelled {
  color: white;
  background-color: #e74c3c; /* Red for negative/closed status */
  font-weight: 600;
  padding: 0.25rem 0.8rem;
  border-radius: 20px;
  text-transform: capitalize;
  font-size: 0.85rem;
  display: inline-block;
  text-align: center;
}

/* List Styles for Accounts, Transactions, Loans */
.list-cards {
  list-style: none;
  padding: 0;
  margin: 0;
}

.list-item {
  display: flex;
  align-items: center;
  padding: 1rem 0;
  border-bottom: 1px solid #ecf0f1; /* Lighter border */
  font-size: 0.95rem;
  color: #444;
}

.list-item:last-child {
  border-bottom: none;
}

/* Account Item Specific Styles */
.account-item {
  justify-content: space-between;
  flex-wrap: wrap; /* Allow wrapping on smaller screens */
  gap: 0.5rem;
}

.account-details {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}

.account-balance {
  font-weight: 700;
  font-size: 1.1rem;
  color: #2c3e50;
  margin-left: 1rem; /* Space from details */
  white-space: nowrap; /* Prevent breaking */
}

.account-status {
  min-width: 90px;
}

/* Transaction Item Specific Styles */
.transaction-item {
  flex-direction: column;
  align-items: flex-start;
  padding: 1.2rem 0;
}

.transaction-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.6rem;
  font-weight: 600;
  font-size: 1.05rem;
  color: #222;
}

.transaction-type {
  text-transform: uppercase;
  color: #34495e;
}

.transaction-amount {
  font-weight: 700;
  font-size: 1.1rem;
}

.amount-deposit {
  color: #27ae60; /* Stronger green */
}

.amount-withdraw {
  color: #c0392b; /* Stronger red */
}

.transaction-details {
  width: 100%;
  font-size: 0.9rem;
  color: #555;
  margin-bottom: 0.6rem;
}

.transaction-info {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
  margin-bottom: 0.4rem;
}

.transaction-info span strong {
  color: #333;
}

.transaction-description {
  font-style: italic;
  color: #777;
  margin-top: 0.5rem;
  line-height: 1.4;
}

.transaction-footer {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
  color: #666;
  font-weight: 500;
  padding-top: 0.5rem;
  border-top: 1px dashed #f0f0f0; /* Dashed separator */
}

.transaction-recipient {
  color: #34495e;
  font-weight: 600;
}

/* Loan Item Specific Styles */
.loan-item {
  flex-direction: column;
  align-items: flex-start;
  gap: 0.7rem;
  padding: 1.2rem 0;
}

.loan-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 700;
  font-size: 1.1rem;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.loan-id {
  font-size: 0.95rem;
  color: #777;
}

.loan-amount {
  display: flex;
  align-items: center;
  gap: 0.7rem;
  font-size: 1.2rem;
  color: #27ae60; /* Green for loan amount */
}

.loan-status {
  min-width: 90px;
}

.loan-details p {
  margin: 0.3rem 0;
  font-size: 0.95rem;
  color: #555;
}

.loan-details strong {
  color: #333;
}

.loan-schedule {
  width: 100%;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap; /* Allow wrapping */
  font-size: 0.9rem;
  color: #666;
  margin-top: 0.8rem;
  padding-top: 0.8rem;
  border-top: 1px dashed #f0f0f0;
}

.loan-schedule span {
  flex: 1 1 48%; /* Distribute space */
}

/* No data fallback */
.no-data {
  font-style: italic;
  color: #888;
  text-align: center;
  padding: 1.5rem 0;
  background-color: #fdfdfd;
  border-radius: 5px;
  margin-top: 1rem;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .header h1 {
    font-size: 1.8rem;
  }

  .card {
    padding: 1.5rem;
  }

  .user-info-card .info-grid {
    grid-template-columns: 1fr; /* Stack items on small screens */
    gap: 0.8rem;
  }

  .list-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }

  .account-balance, .account-status {
    width: 100%;
    text-align: left;
    margin-left: 0;
  }

  .transaction-info {
    flex-direction: column;
    gap: 0.5rem;
  }

  .loan-schedule {
    flex-direction: column;
    gap: 0.5rem;
  }
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1rem;
}

.pagination button {
  padding: 0.4rem 0.75rem;
  border: 1px solid #ccc;
  background: #f8f9fa;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: background 0.2s, border-color 0.2s;
}

.pagination button:hover:not(:disabled) {
  background: #e2e6ea;
  border-color: #bbb;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination .active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

</style>