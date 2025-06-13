<template>
  <div class="home-container">
    <header class="header">
      <h1>Welcome, {{ username }} 👋</h1>
      <p>Here’s your banking overview</p>
    </header>

    <section class="section">
      <h2>Accounts</h2>
      <div class="card-grid" v-if="accounts.length">
        <div v-for="account in accounts" :key="account.id" class="card">
          <h3>{{ account.type }} Account</h3>
          <p>Balance: ${{ account.balance.toFixed(2) }}</p>
          <p>Account status: {{ account.status.toLowerCase() }}</p>
        </div>
      </div>
      <p v-else>No accounts found.</p>
      <p v-if="accountsError" class="error">{{ accountsError }}</p>
      <router-link to="/accounts" class="view-all">View All Accounts</router-link>
    </section>

    <section class="section">
      <h2>Recent Transactions</h2>
      <ul class="list" v-if="transactions.length">
        <li v-for="tx in transactions" :key="tx.id">
          {{ tx.details || "no details for this transaction" }} — {{"Amount: $"  + tx.amount }} on {{ formatDate(tx.createdAt) }} to "to do later"
        </li>
      </ul>
      <p v-else>No transactions found.</p>
      <p v-if="transactionsError" class="error">{{ transactionsError }}</p>
      <router-link to="/transactions" class="view-all">View All Transactions</router-link>
    </section>

    <section class="section">
      <h2>Loans</h2>
      <div class="card-grid" v-if="loans.length">
        <div v-for="loan in loans" :key="loan.id" class="card">
          <h3>{{ loan.type }} Loan</h3>
          <p>Amount taken: ${{ loan.amount }}</p>
          <p>Due: {{ formatDate(loan.endDate) }}</p>
          <p>Interest rate: {{ loan.interestRate }}%</p>
          <p><b>Due amount: ${{loan.amount + (loan.amount * loan.interestRate / 100)}}</b></p>
        </div>
      </div>
      <p v-else>No loans found.</p>
      <p v-if="loansError" class="error">{{ loansError }}</p>
      <router-link to="/loans" class="view-all">View All Loans</router-link>
    </section>

    <section class="logout-section">
      <button @click="handleLogout" class="logout-button">Logout</button>
    </section>

  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'; // Import useRouter
import { useAuthStore } from '@/stores/authStore'; // Import useAuthStore
import { useAccountsStore } from '@/stores/acccountsStore.js'
import { useTransactionsStore } from '@/stores/transactionsStore.js'
import { useLoansStore } from '@/stores/loansStore.js'

const username = localStorage.getItem('username');

onMounted(() => {
  document.title = "Home"
})

const accountsStore = useAccountsStore()
const transactionsStore = useTransactionsStore()
const loansStore = useLoansStore()
const authStore = useAuthStore(); // Initialize the auth store
const router = useRouter(); // Initialize the router

const { accounts, error: accountsError } = storeToRefs(accountsStore)
const { transactions, error: transactionsError } = storeToRefs(transactionsStore)
const { loans, error: loansError } = storeToRefs(loansStore)

onMounted(() => {
  accountsStore.fetchAccounts()
  transactionsStore.fetchAllTransactions();
  loansStore.fetchAllLoans()
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

const handleLogout = () => {
  authStore.logOut(); // Call the logout method from the store
  router.push('/login'); // Redirect to the login page after logout
};
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

/* Styles for Logout Button */
.logout-section {
  text-align: center;
  margin-top: 3rem;
}

.logout-button {
  background-color: #dc3545;
  color: white;
  padding: 0.8rem 2rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1.1em;
  font-weight: bold;
  transition: background-color 0.2s ease-in-out, transform 0.2s ease-in-out;
}

.logout-button:hover {
  background-color: #c82333;
  transform: translateY(-2px);
}
</style>