<template>
  <div class="home-container">
    <header class="header">
      <h1>Welcome, Admin! 👋</h1>
      <p>Select a management area to begin:</p>
    </header>

    <section class="section">
      <h2>Admin Tools</h2>
      <div class="card-grid">
<!--        <router-link to="/admin/users" class="card admin-card">-->
<!--          <h3>👥 User Management</h3>-->
<!--          <p>View, create, and manage user accounts.</p>-->
<!--        </router-link>-->

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

<!--        <router-link to="/admin/settings" class="card admin-card">-->
<!--          <h3>⚙️ System Settings</h3>-->
<!--          <p>Configure bank-wide parameters and features.</p>-->
<!--        </router-link>-->

      </div>
    </section>

  </div>
</template>

<script setup>
import { onMounted } from 'vue'

onMounted(() => {
  document.title = "Admin Dashboard"
})

// No store imports needed as we're not displaying dynamic data here.
// The respective admin views (e.g., AdminAccountsView) will fetch their own data.
</script>

<style scoped>
/* Reusing the same styles as your homepage for consistency */
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
  gap: 1rem; /* This will now primarily control vertical spacing between cards */
}

/* Base card style from your homepage */
.card {
  background-color: #ffffff;
  padding: 1rem 1.5rem;
  border-radius: 10px;
  box-shadow: 0 0 8px rgba(0, 55, 123, 0.1);
  /* The flex property here is overridden by .admin-card for this specific layout */
  flex: 1 1 250px; /* Original - allows cards to wrap and take up space */
  text-decoration: none; /* Remove underline for router-link */
  color: #1a2b4c; /* Ensure text color is consistent */
  transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out; /* Smooth hover effect */
  display: flex; /* For better alignment of content within the card */
  flex-direction: column;
  justify-content: space-between;
}

/* Specific styling for admin navigation cards */
.admin-card {
  /* NEW: Forces each card to take 100% width, making them stack one per row */
  flex: 0 0 100%;
}

.admin-card h3 {
  color: #1450a3; /* Heading color for admin cards */
  margin-bottom: 0.5rem;
}

.admin-card p {
  font-size: 0.9em;
  color: #555;
}

/* Hover effect for admin cards */
.admin-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 55, 123, 0.2);
  cursor: pointer;
}

/* The .list and .view-all styles are not needed here as they relate to data display,
   but I've kept them in the <style> tag as a block for clarity, they just won't be used. */
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