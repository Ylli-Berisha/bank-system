<template>
  <div class="home-container">
    <header class="header">
      <h1>Welcome, {{ username }}! 👋</h1>
      <p>Select a management area to begin:</p>
    </header>

    <section class="section">
      <h2>Admin Tools</h2>
      <div class="card-grid">
        <router-link to="/admin/accounts" class="card admin-card">
          <h3>💳 Account Management</h3>
          <p>Oversee and manage customer bank accounts.</p>
        </router-link>

        <router-link to="/admin/transactions" class="card admin-card">
          <h3>📊 Transaction Management</h3>
          <p>Review all banking transactions.</p>
        </router-link>

        <router-link to="/admin/loans" class="card admin-card">
          <h3>💰 Loan Management</h3>
          <p>Approve, reject, and track loan applications.</p>
        </router-link>

        <router-link to="/admin/audit" class="card admin-card">
          <h3>📈 Audits and Observability </h3>
          <p>Review audits and actions performed by users and accounts.</p>
        </router-link>

      </div>
    </section>

    <section class="logout-section">
      <button @click="handleLogout" class="logout-button">Logout</button>
    </section>

  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';

onMounted(() => {
  document.title = "Admin Dashboard"
})

const authStore = useAuthStore();
const router = useRouter();
const username = localStorage.getItem('username');

const handleLogout = () => {
  authStore.logOut();
  router.push('/login');
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
  text-decoration: none;
  color: #1a2b4c;
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.admin-card {
  flex: 0 0 100%;
}

.admin-card h3 {
  color: #1450a3;
  margin-bottom: 0.5rem;
}

.admin-card p {
  font-size: 0.9em;
  color: #555;
}

.admin-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 55, 123, 0.2);
  cursor: pointer;
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