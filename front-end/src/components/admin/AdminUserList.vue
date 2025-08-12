<template>
  <div class="page-container">
    <header class="header">
      <h1>Users</h1>
      <p>Browse and manage registered users.</p>
    </header>

    <section class="section filter-section">
      <h2 class="section-title">Filter Users</h2>
      <p class="section-description">Use the fields below to filter users.</p>

      <div class="filter-controls-grid">
        <div class="filter-group">
          <label for="userId">User ID:</label>
          <input
              type="text"
              id="userId"
              v-model="filters.userId"
              class="filter-input"
              placeholder="e.g., user_1234"
          />
        </div>

        <div class="filter-group">
          <label for="username">Username:</label>
          <input
              type="text"
              id="username"
              v-model="filters.username"
              class="filter-input"
              placeholder="e.g., johndoe"
          />
        </div>

        <div class="filter-group">
          <label for="email">Email:</label>
          <input
              type="email"
              id="email"
              v-model="filters.email"
              class="filter-input"
              placeholder="e.g., john@example.com"
          />
        </div>

        <div class="filter-group">
          <label for="firstName">First Name:</label>
          <input
              type="text"
              id="firstName"
              v-model="filters.firstName"
              class="filter-input"
              placeholder="e.g., John"
          />
        </div>

        <div class="filter-group">
          <label for="lastName">Last Name:</label>
          <input
              type="text"
              id="lastName"
              v-model="filters.lastName"
              class="filter-input"
              placeholder="e.g., Doe"
          />
        </div>

        <div class="filter-group">
          <label for="phoneNumber">Phone Number:</label>
          <input
              type="tel"
              id="phoneNumber"
              v-model="filters.phoneNumber"
              class="filter-input"
              placeholder="e.g., +1234567890"
          />
        </div>

        <div class="filter-group">
          <label for="isActive">Active Status:</label>
          <select id="isActive" v-model="filters.isActive" class="filter-input">
            <option value="">Any</option>
            <option :value="true">Active</option>
            <option :value="false">Inactive</option>
          </select>
        </div>

        <div class="filter-group">
          <label for="accountId">Account ID:</label>
          <input
              type="text"
              id="accountId"
              v-model="filters.accountId"
              class="filter-input"
              placeholder="e.g., acc_1234"
          />
        </div>

        <div class="filter-group">
          <label for="loanId">Loan ID:</label>
          <input
              type="text"
              id="loanId"
              v-model="filters.loanId"
              class="filter-input"
              placeholder="e.g., loan_5678"
          />
        </div>

        <div class="filter-group">
          <label for="transactionId">Transaction ID:</label>
          <input
              type="text"
              id="transactionId"
              v-model="filters.transactionId"
              class="filter-input"
              placeholder="e.g., tx_9012"
          />
        </div>

        <div class="filter-buttons">
          <button @click="applyFilters" class="apply-filters-btn">Apply Filters</button>
          <button @click="clearFilters" class="clear-filters-btn">Clear Filters</button>
        </div>
      </div>
    </section>

    <section class="section">
      <h2 class="section-title">User List</h2>

      <div v-if="error" class="empty-state-message">{{ error }}</div>
      <div v-else-if="users.length === 0" class="empty-state-message">
        No users found.
      </div>

      <table v-else class="audit-table">
        <thead>
        <tr>
          <th>User ID</th>
          <th>Full Name</th>
          <th>Username</th>
          <th>Phone Number</th>
          <th>Address</th>
          <th>Birthdate</th>
          <th>Email</th>
          <th>Active</th>
          <th>Created At</th>
        </tr>
        </thead>
        <tbody>
        <tr
            v-for="user in users"
            :key="user.id"
            @click="goToUser(user.id)"
            class="clickable-row"
        >
          <td>{{ user.id }}</td>
          <td>{{ user.firstName + ' ' + user.lastName || '-' }}</td>
          <td>{{ user.username }}</td>
          <td>{{ user.phoneNumber || '-' }}</td>
          <td>{{ user.address }}</td>
          <td>{{ formatDate(user.birthDate) }}</td>
          <td>{{ user.email || '-' }}</td>
          <td>{{ user.active ? 'Yes' : 'No' }}</td>
          <td>{{ formatDate(user.createdAt) }}</td>
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
import { ref, reactive, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAdminUsersStore } from '@/stores/admin/adminUsersStore.js'

const store = useAdminUsersStore()
const router = useRouter()

const users = ref([])
const error = ref(null)
const currentPage = ref(0)
const totalPages = ref(0)

const filters = reactive({
  userId: null,
  username: null,
  email: null,
  firstName: null,
  lastName: null,
  phoneNumber: null,
  isActive: null,
  accountId: null,
  loanId: null,
  transactionId: null,
})

let filterDebounceTimer = null

const debouncedFetch = () => {
  if (filterDebounceTimer) clearTimeout(filterDebounceTimer)
  filterDebounceTimer = setTimeout(() => {
    currentPage.value = 0
    fetchUsers()
  }, 500)
}

watch(filters, debouncedFetch, { deep: true })

async function fetchUsers(page = currentPage.value) {
  const params = {}
  for (const key in filters) {
    const val = filters[key]
    if (val !== null && val !== undefined && (typeof val !== 'string' || val !== '')) {
      params[key] = val;
    }
  }

  try {
    const adminIdForCall = router.currentRoute.value.params.adminId || 'YOUR_ADMIN_ID_HERE';
    await store.filterUsers(adminIdForCall, params, page)
    users.value = store.users
    currentPage.value = store.currentPage
    totalPages.value = store.totalPages
    error.value = store.error
  } catch (err) {
    error.value = 'Failed to fetch users.'
    users.value = []
  }
}

function clearFilters() {
  for (const key in filters) {
    filters[key] = null
  }
  fetchUsers(0)
}

function nextPage() {
  if (currentPage.value < totalPages.value - 1) {
    fetchUsers(currentPage.value + 1)
  }
}

function prevPage() {
  if (currentPage.value > 0) {
    fetchUsers(currentPage.value - 1)
  }
}

function goToUser(userId) {
  router.push(`/admin/users/${userId}`)
}

function formatDate(isoString) {
  if (!isoString) return '-'
  const date = new Date(isoString)
  return date.toLocaleString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

onMounted(() => {
  document.title = 'Admin Users'
  fetchUsers()
})
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
.clickable-row:hover {
  cursor: pointer;
  transition: background-color 0.2s ease-in-out;
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

.apply-filters-btn {
  background-color: #3498db;
  color: #ffffff;
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.2);
}

.apply-filters-btn:hover {
  background-color: #2980b9;
  transform: translateY(-1px);
  box-shadow: 0 6px 15px rgba(41, 128, 185, 0.3);
}
</style>
