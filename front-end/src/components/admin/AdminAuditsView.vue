<template>
  <div class="page-container">
    <header class="header">
      <h1>Admin Audits</h1>
      <p>View and filter audit logs.</p>
    </header>

    <section class="section filter-section">
      <h2 class="section-title">Filter Audits</h2>
      <p class="section-description">Use filters to find specific audits by user, account, type, or date range.</p>

      <div class="filter-controls-grid">
        <div class="filter-group">
          <label for="userId">User ID:</label>
          <input id="userId" v-model="filters.userId" class="filter-input" placeholder="e.g. 123"/>
        </div>

        <div class="filter-group">
          <label for="accountId">Account ID:</label>
          <input id="accountId" v-model="filters.accountId" class="filter-input" placeholder="e.g. 456"/>
        </div>

        <div class="filter-group">
          <label for="type">Audit Type:</label>
          <select id="type" v-model="filters.type" class="filter-input">
            <option value="">All Types</option>
            <option v-for="type in auditTypes" :key="type" :value="type">{{ formatLabel(type) }}</option>
          </select>
        </div>

        <div class="filter-group">
          <label for="startDate">Start Date:</label>
          <input type="date" id="startDate" v-model="filters.startDate" class="filter-input" />
        </div>

        <div class="filter-group">
          <label for="endDate">End Date:</label>
          <input type="date" id="endDate" v-model="filters.endDate" class="filter-input" />
        </div>

        <div class="filter-group" style="grid-column: span 2;">
          <label for="query">Search (Details or ID):</label>
          <input id="query" v-model="filters.query" class="filter-input" placeholder="Search details or audit ID" />
        </div>

        <div class="filter-buttons">
          <button @click="clearFilters" class="clear-filters-btn">Clear Filters</button>
        </div>
      </div>
    </section>

    <section class="section">
      <h2 class="section-title">Audit Logs</h2>

      <div v-if="audits.length === 0" class="empty-state-message">
        No audits found.
      </div>

      <table v-else class="audit-table">
        <thead>
        <tr>
          <th>User ID</th>
          <th>Account ID</th>
          <th>Type</th>
          <th>Details</th>
          <th>Created At</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="audit in audits" :key="audit.id">
          <td>{{ audit.userId || '-' }}</td>
          <td>{{ audit.accountId || '-' }}</td>
          <td>{{ formatLabel(audit.type) }}</td>
          <td>{{ audit.details || '-' }}</td>
          <td>{{ formatDate(audit.createdAt) }}</td>
        </tr>
        </tbody>
      </table>
    </section>

    <section class="pagination-section">
      <button @click="prevPage" :disabled="currentPage === 0">Previous</button>
      <span>Page {{ currentPage + 1 }} of {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage >= totalPages - 1">Next</button>
    </section>
  </div>
</template>

<script setup>
import { reactive, computed, watch, onMounted } from 'vue';
import { useAdminAuditsStore } from '@/stores/admin/adminAuditsStore.js';

const auditTypes = [
  "FAILED_LOGIN",
  "MONEY_TRANSFERRED",
  "LOAN_APPROVED",
  "LOAN_DECLINED",
  "LOAN_CHANGES_PROPOSED",
  "LOAN_REPAID",
  "LOAN_APPLIED",
  "DEPOSIT_MADE",
  "ACCOUNT_APPLICATION_SUBMITTED",
  "ACCOUNT_APPLICATION_APPROVED",
  "ACCOUNT_APPLICATION_REJECTED",
  "PASSWORD_CHANGED",
  "ACCOUNT_LOCKED",
  "ACCOUNT_UNLOCKED",
  "TRANSACTION_REVERTED",
  "NOT_ENOUGH_FUNDS",
  "LOAN_REJECTED",
  "LOAN_CHANGES_ACCEPTED",
  "LOAN_CHANGES_REJECTED",
  "WITHDRAWAL_MADE",
  "USER_SIGNED_UP",
  "USER_LOGGED_IN"
];


const filters = reactive({
  userId: '',
  accountId: '',
  type: '',
  startDate: '',
  endDate: '',
  query: '',
});

const store = useAdminAuditsStore();

const audits = computed(() => store.paginatedAudits);
const totalPages = computed(() => store.totalPages);
const currentPage = computed(() => store.currentPage);

function clearFilters() {
  filters.userId = '';
  filters.accountId = '';
  filters.type = '';
  filters.startDate = '';
  filters.endDate = '';
  filters.query = '';
  store.resetPage();
}

function fetchAudits() {
  const cleanFilters = {};
  for (const key in filters) {
    if (filters[key] !== '' && filters[key] !== null) {
      cleanFilters[key] = filters[key];
    }
  }
  store.fetchFilteredAudits(cleanFilters, store.currentPage);
}

function formatLabel(value) {
  if (!value) return '';
  return value
      .toString()
      .toLowerCase()
      .replace(/_/g, ' ')
      .replace(/\b\w/g, (c) => c.toUpperCase());
}

function formatDate(isoString) {
  if (!isoString) return '-';
  const date = new Date(isoString);
  return date.toLocaleString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

function nextPage() {
  store.incrementPage();
}

function prevPage() {
  store.decrementPage();
}

onMounted(() => {
  document.title = 'Admin Audits';
  fetchAudits();
});

watch(
    () => [filters, store.currentPage],
    () => fetchAudits(),
    { deep: true }
);
</script>

<style scoped>
/* Page Container and Header */
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

/* Filter Section */
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

/* Empty state message */
.empty-state-message {
  text-align: center;
  font-size: 1.1rem;
  color: #7f8c8d;
  padding: 2rem 0;
  background-color: #f0f4f7;
  border-radius: 8px;
  margin-top: 2rem;
}

/* Table styles */
.audit-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 1rem;
}

.audit-table th,
.audit-table td {
  border: 1px solid #ddd;
  padding: 0.75rem;
  text-align: left;
}

.audit-table th {
  background-color: #f2f2f2;
}

/* Pagination */
.pagination-section {
  margin-top: 1rem;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
}

.pagination-section button {
  padding: 0.5rem 1rem;
  border: none;
  background-color: #1976d2;
  color: white;
  border-radius: 4px;
  cursor: pointer;
}

.pagination-section button:disabled {
  background-color: #90caf9;
  cursor: not-allowed;
}

</style>

