<template>
  <div class="home-container">
    <header class="header">
      <h1>Welcome, Ylli 👋</h1>
      <p>Here’s your banking overview</p>
    </header>

    <!-- Accounts Section -->
    <section class="section">
      <h2>Accounts</h2>
      <div class="card-grid" v-if="accounts.length">
        <div v-for="account in accounts" :key="account.id" class="card">
          <h3>{{ account.type }} Account</h3>
          <p>Balance: ${{ account.balance.toFixed(2) }}</p>
          <p>Account status: {{account.status.toLowerCase()}}</p>
        </div>
      </div>
      <p v-else>No accounts found.</p>
      <p v-if="error" class="error">{{ error }}</p>
      <router-link to="/accounts" class="view-all">View All Accounts</router-link>
    </section>

    <!-- Transactions Section -->
    <section class="section">
      <h2>Recent Transactions</h2>
      <ul class="list">
        <li v-for="tx in transactions" :key="tx.id">
          {{ tx.description }} — ${{ tx.amount }} on {{ tx.date }}
        </li>
      </ul>
      <router-link to="/transactions" class="view-all">View All Transactions</router-link>
    </section>

    <!-- Loans Section -->
    <section class="section">
      <h2>Loans</h2>
      <div class="card-grid">
        <div v-for="loan in loans" :key="loan.id" class="card">
          <h3>{{ loan.type }} Loan</h3>
          <p>Amount: ${{ loan.amount }}</p>
          <p>Due: {{ loan.dueDate }}</p>
        </div>
      </div>
      <router-link to="/loans" class="view-all">View All Loans</router-link>
    </section>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useAccountsStore } from '@/stores/acccountsStore.js'

onMounted(() => {
  document.title = "Home"
})

const accountsStore = useAccountsStore()
const { accounts, error } = storeToRefs(accountsStore)

onMounted(() => {
  accountsStore.fetchAccounts()
})

// Hardcoded sample data for transactions and loans
const transactions = [
  { id: 1, description: 'Paid to John Doe', amount: 50.00, date: '2025-06-07' },
  { id: 2, description: 'Received from ACME Corp', amount: 300.00, date: '2025-06-06' },
  { id: 3, description: 'Paid to Grocery Store', amount: 120.00, date: '2025-06-05' }
]

const loans = [
  { id: 1, type: 'Personal', amount: 10000.00, dueDate: '2025-07-01' },
  { id: 2, type: 'Car', amount: 7500.00, dueDate: '2025-08-15' }
]
</script>




<style scoped>
.home-container {
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

.section {
  margin-bottom: 2.5rem;
}

h2 {
  margin-bottom: 1rem;
  color: #1450a3;
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

.list {
  list-style: none;
  padding-left: 0;
  background-color: #ffffff;
  border-radius: 10px;
  padding: 1rem;
  box-shadow: 0 0 8px rgba(0, 55, 123, 0.1);
}

.list li {
  padding: 0.5rem 0;
  border-bottom: 1px solid #e2e8f0;
}

.list li:last-child {
  border-bottom: none;
}

.view-all {
  display: inline-block;
  margin-top: 1rem;
  color: #1450a3;
  text-decoration: underline;
  font-weight: bold;
}
</style>
