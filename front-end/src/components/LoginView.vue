<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>Welcome Back</h1>
        <p>Please sign in to continue</p>
      </div>

      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username">Username</label>
          <div class="input-wrapper">
            <i class="fas fa-user"></i>
            <input
                type="text"
                id="username"
                v-model="username"
                placeholder="Enter your username"
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
          </div>
        </div>

        <div class="remember-forgot">
          <div class="remember">
            <input type="checkbox" id="remember" v-model="rememberMe"/>
            <label for="remember">Remember me</label>
          </div>
          <a href="#" class="forgot-link">Forgot Password?</a>
        </div>

        <button type="submit" class="login-button" :disabled="loading">
          <span v-if="!loading">Sign In</span>
          <span v-else>Signing in...</span>
        </button>
      </form>

      <div v-if="error" class="error-message">
        {{ error }}
      </div>

      <div class="register-link">
        <RouterLink to="/sign-up">Don't have an account? Sign up</RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';
import { onMounted } from 'vue';

onMounted(() => {
  document.title = 'Login';
});

const username = ref('');
const password = ref('');
const rememberMe = ref(false);
const showPassword = ref(false);
const loading = ref(false);
const error = ref(null);

const router = useRouter();
const authStore = useAuthStore();

const handleLogin = async () => {
  error.value = null;
  // loading.value = true;

  try {
    await authStore.logIn({
      username: username.value,
      password: password.value,
      rememberMe: rememberMe.value
    });
    await router.push('/');
  } catch (err) {
    error.value = err.response?.data?.message || 'Login failed. Please try again.';
  } finally {
    loading.value = false;
  }
};
</script>


<style scoped>
.error-message {
  color: red;
  margin-top: 10px;
}
</style>
<style scoped>
/* Keep your existing styles or add new ones */
.error-message {
  color: red;
  margin-top: 10px;
}
</style>


<style>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css');

body {
  background: linear-gradient(135deg, #e6f0ff 0%, #d0e3ff 50%, #c2dbff 100%);
  font-family: sans-serif;
}

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #e6f0ff 0%, #d0e3ff 50%, #c2dbff 100%);
  position: relative;
  overflow: hidden;
}

.login-container::before,
.login-container::after {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  z-index: 0;
}

.login-container::before {
  background: radial-gradient(circle, rgba(59, 130, 246, 0.2) 0%, rgba(37, 99, 235, 0.1) 70%, transparent 100%);
  top: -100px;
  right: -100px;
}

.login-container::after {
  background: radial-gradient(circle, rgba(96, 165, 250, 0.2) 0%, rgba(59, 130, 246, 0.1) 70%, transparent 100%);
  bottom: -100px;
  left: -100px;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 500px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 105, 255, 0.15);
  padding: 3rem 2rem;
  overflow: hidden;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h1 {
  color: #1a56db;
  font-size: 32px;
  margin-bottom: 10px;
}

.login-header p {
  color: #64748b;
  font-size: 18px;
}

.login-form {
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

/* Adjust input padding to avoid overlap */
.input-wrapper input {
  width: 100%;
  padding: 16px 45px 16px 45px; /* left and right padding for icons */
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: border 0.3s, box-shadow 0.3s;
}


.input-wrapper input:focus {
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.2);
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.remember {
  display: flex;
  align-items: center;
  gap: 8px;
}

.remember label {
  color: #64748b;
  font-size: 16px;
}

.forgot-link {
  color: #2563eb;
  font-size: 16px;
  text-decoration: none;
  transition: color 0.2s;
}

.forgot-link:hover {
  color: #1d4ed8;
  text-decoration: underline;
}

.login-button {
  padding: 16px;
  background-color: #2563eb;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 18px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.login-button:hover {
  background-color: #1d4ed8;
}

.login-button:disabled {
  background-color: #93c5fd;
  cursor: not-allowed;
}

.register-link {
  text-align: center;
  margin-top: 40px;
  color: #64748b;
  font-size: 16px;
}

.register-link a {
  color: #2563eb;
  text-decoration: none;
  font-weight: 500;
}

.register-link a:hover {
  text-decoration: underline;
}
</style>
