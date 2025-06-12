Okay, I've updated the LoansView.vue component to remove any explicit handling of the userId in the newLoan object, as it's being handled by the global client instance through headers.

LoansView.vue
Code snippet

<template>
  <div class="page-container">
    <header class="header">
      <h1>Your Loans</h1>
      <p>Detailed overview of your loan accounts</p>
    </header>

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

    <section class="section">
      <h2 class="section-title">Active Loans</h2>
      <div v-if="activeLoans.length" class="card-grid">
        <div v-for="loan in activeLoans" :key="loan.id" class="card active-loan">
          <h3>{{ formatLoanType(loan.loanType) }} Loan</h3>
          <p><strong>Amount:</strong> ${{ loan.amount.toFixed(2) }}</p>
          <p><strong>Interest Rate:</strong> {{ loan.interestRate.toFixed(2) }}%</p>
          <p><strong>Status:</strong> {{ formatStatus(loan.status) }}</p>
          <p><strong>Start Date:</strong> {{ formatDate(loan.startDate) }}</p>
          <p><strong>End Date:</strong> {{ formatDate(loan.endDate) }}</p>
          <p><strong>Loan ID:</strong> {{ loan.id }}</p>
        </div>
      </div>
      <p v-else class="empty-state-message">No active loans found.</p>
    </section>

    <section class="section pending-section">
      <h2 class="section-title">Pending Loans</h2>
      <div v-if="pendingLoans.length" class="card-grid">
        <div v-for="loan in pendingLoans" :key="loan.id" class="card pending">
          <h3>{{ formatLoanType(loan.loanType) }} Loan</h3>
          <p><strong>Amount:</strong> ${{ loan.amount.toFixed(2) }}</p>
          <p><strong>Interest Rate:</strong> {{ loan.interestRate.toFixed(2) }}%</p>
          <p><strong>Status:</strong> {{ formatStatus(loan.status) }}</p>
          <p><strong>Applied On:</strong> {{ formatDate(loan.createdAt) }}</p>
          <p><strong>Loan ID:</strong> {{ loan.id }}</p>
        </div>
      </div>
      <p v-else class="empty-state-message">No pending loan applications found.</p>
    </section>

    <section class="section paid-off-section">
      <h2 class="section-title">Paid Off Loans</h2>
      <div v-if="paidOffLoans.length" class="card-grid">
        <div v-for="loan in paidOffLoans" :key="loan.id" class="card paid-off">
          <h3>{{ formatLoanType(loan.loanType) }} Loan</h3>
          <p><strong>Original Amount:</strong> ${{ loan.amount.toFixed(2) }}</p>
          <p><strong>Status:</strong> {{ formatStatus(loan.status) }}</p>
          <p><strong>Paid Off Date:</strong> {{ formatDate(loan.paidOffDate) }}</p>
          <p><strong>Loan ID:</strong> {{ loan.id }}</p>
        </div>
      </div>
      <p v-else class="empty-state-message">No paid off loans found.</p>
    </section>

    <p v-if="loansError" class="error">{{ loansError }}</p>

    <ConfirmationModal
        :is-open="showConfirmationModal"
        :title="confirmationModalTitle"
        :confirm="handleConfirmationModalConfirm"
        :cancel="handleConfirmationModalCancel"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
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
  applyForNewLoan
} = storeToRefs(loansStore)

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

const activeLoans = computed(() =>
    loans.value.filter(loan => loan.status === 'ACTIVE')
)

const pendingLoans = computed(() =>
    loans.value.filter(loan => loan.status === 'PENDING')
)

const paidOffLoans = computed(() =>
    loans.value.filter(loan => loan.status === 'PAID_OFF')
)

const activeAccountsForSelection = computed(() =>
    accounts.value.filter(acc => acc.status === 'ACTIVE')
);

onMounted(async () => {
  document.title = "Loans"
  await loansStore.fetchLoans()
  await loansStore.fetchLoanTypes()
  await accountsStore.fetchAccounts()

  if (loanTypes.value.length > 0) {
    newLoan.value.loanType = loanTypes.value[0]
  }

  if (activeAccountsForSelection.value.length > 0) {
    selectedSourceAccountId.value = activeAccountsForSelection.value[0].id;
  }
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

function formatLoanType(type) {
  return type ? type.toString().replace(/_/g, ' ').replace(/\b\w/g, char => char.toUpperCase()) : '';
}

function formatStatus(status) {
  return status ? status.toString().replace(/_/g, ' ').replace(/\b\w/g, char => char.toUpperCase()) : '';
}

function formatAccountDisplay(account) {
  if (!account) return '';
  return `${account.type.replace('_', ' ')} (ID: ${account.id}, Balance: $${account.balance.toFixed(2)})`;
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
    await loansStore.fetchLoans();
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
</script>

<style scoped>
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
  border-left: 6px solid #2d8cf0;
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

.pending-section .card.pending {
  background-color: #fffde7;
  border-left-color: #f1c40f;
  box-shadow: 0 5px 15px rgba(241, 196, 15, 0.1);
}

.paid-off-section .card.paid-off {
  background-color: #eafaf1;
  border-left-color: #2ecc71;
  box-shadow: 0 5px 15px rgba(46, 204, 113, 0.1);
}

.active-loan {
  border-left-color: #3498db;
}
</style>

<style scoped>
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
  border-left: 6px solid #2d8cf0;
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

.pending-section .card.pending {
  background-color: #fffde7;
  border-left-color: #f1c40f;
  box-shadow: 0 5px 15px rgba(241, 196, 15, 0.1);
}

.paid-off-section .card.paid-off {
  background-color: #eafaf1;
  border-left-color: #2ecc71;
  box-shadow: 0 5px 15px rgba(46, 204, 113, 0.1);
}

.active-loan {
  border-left-color: #3498db;
}
</style>