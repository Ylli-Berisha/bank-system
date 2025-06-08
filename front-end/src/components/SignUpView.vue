<template>
  <div class="signup-container">
    <div class="signup-card">
      <div class="signup-header">
        <h1>Create Account</h1>
        <p>Sign up to get started</p>
      </div>

      <form @submit.prevent="handleSignUp" class="signup-form">
        <div class="form-group">
          <label for="firstName">First Name</label>
          <div class="input-wrapper">
            <i class="fas fa-user"></i>
            <input
                type="text"
                id="firstName"
                v-model="firstName"
                placeholder="Enter your first name"
                required
            />
          </div>
        </div>

        <div class="form-group">
          <label for="lastName">Last Name</label>
          <div class="input-wrapper">
            <i class="fas fa-user"></i>
            <input
                type="text"
                id="lastName"
                v-model="lastName"
                placeholder="Enter your last name"
                required
            />
          </div>
        </div>

        <div class="form-group">
          <label for="email">Email</label>
          <div class="input-wrapper">
            <i class="fas fa-envelope"></i>
            <input
                type="email"
                id="email"
                v-model="email"
                placeholder="Enter your email"
                required
            />
          </div>
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <div class="input-wrapper">
            <i class="fas fa-lock"></i>
            <input
                :type="showPassword ? 'text' : 'password'"
                id="password"
                v-model="password"
                placeholder="Enter your password"
                required
            />
            <button
                type="button"
                class="show-password-toggle"
                @click="showPassword = !showPassword"
                :aria-label="showPassword ? 'Hide password' : 'Show password'"
            >
              <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
            </button>
          </div>
        </div>

        <div class="form-group">
          <label for="phone">Phone Number</label>
          <div class="input-wrapper">
            <i class="fas fa-phone"></i>
            <input
                type="tel"
                id="phone"
                v-model="phone"
                placeholder="Enter your phone number"
                required
            />
          </div>
        </div>

        <div class="form-group">
          <label for="address">Address</label>
          <div class="input-wrapper">
            <i class="fas fa-home"></i>
            <input
                type="text"
                id="address"
                v-model="address"
                placeholder="Enter your address"
                required
            />
          </div>
        </div>

        <div class="form-group">
          <label for="birthdate">Birthdate</label>
          <div class="input-wrapper">
            <i class="fas fa-calendar-alt"></i>
            <input
                type="date"
                id="birthdate"
                v-model="birthdate"
                required
            />
          </div>
        </div>

        <button type="submit" class="signup-button">
          <span>Sign Up</span>
        </button>

      </form>

      <div class="login-link">
        Already have an account? <a href="#">Sign in</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore.js';

onMounted(() => {
  document.title = "Sign-Up";
});

const authStore = useAuthStore();
const router = useRouter();

const firstName = ref('');
const lastName = ref('');
const email = ref('');
const password = ref('');
const phone = ref('');
const address = ref('');
const birthdate = ref('');
const showPassword = ref(false);
const error = ref(null);
const loading = ref(false);

const handleSignUp = async () => {
  error.value = null;
  // loading.value = true;

  try {
    await authStore.signUp({
      firstName: firstName.value,
      lastName: lastName.value,
      email: email.value,
      password: password.value,
      phone: phone.value,
      address: address.value,
      birthdate: birthdate.value
    });
    await router.push('/');
  } catch (err) {
    error.value = err.response?.data?.message || 'Sign up failed. Please try again.';
  } finally {
    loading.value = false;
  }
};
</script>



<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css');

.signup-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #e6f0ff 0%, #d0e3ff 50%, #c2dbff 100%);
  position: relative;
  overflow: hidden;
}

.signup-container::before,
.signup-container::after {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  z-index: 0;
}

.signup-container::before {
  background: radial-gradient(circle, rgba(59, 130, 246, 0.2) 0%, rgba(37, 99, 235, 0.1) 70%, transparent 100%);
  top: -100px;
  right: -100px;
}

.signup-container::after {
  background: radial-gradient(circle, rgba(96, 165, 250, 0.2) 0%, rgba(59, 130, 246, 0.1) 70%, transparent 100%);
  bottom: -100px;
  left: -100px;
}

.signup-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 600px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 105, 255, 0.15);
  padding: 3rem 2.5rem;
  overflow: hidden;
}

.signup-header {
  text-align: center;
  margin-bottom: 40px;
}

.signup-header h1 {
  color: #1a56db;
  font-size: 32px;
  margin-bottom: 10px;
}

.signup-header p {
  color: #64748b;
  font-size: 18px;
}

.signup-form {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin: 0 auto;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  color: #475569;
  font-size: 16px;
  font-weight: 500;
}

.input-wrapper {
  position: relative;
  width: 100%;
}

.input-wrapper i {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  color: #94a3b8;
  font-size: 18px;
  z-index: 1;
}

.input-wrapper input {
  width: 100%;
  padding: 16px 45px 16px 45px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border 0.3s, box-shadow 0.3s;
  box-sizing: border-box;
}

.input-wrapper input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
}

.show-password-toggle {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: none;
  cursor: pointer;
  color: #94a3b8;
  font-size: 18px;
  z-index: 2;
}

.signup-button {
  padding: 16px;
  background-color: #2563eb;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 18px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  display: flex;
  justify-content: center;
  align-items: center;
}

.signup-button:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.signup-button:hover:not(:disabled) {
  background-color: #1d4ed8;
}


.login-link {
  margin-top: 32px;
  text-align: center;
  font-size: 16px;
  color: #475569;
}

.login-link a {
  color: #2563eb;
  font-weight: 600;
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
