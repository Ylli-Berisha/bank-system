<template>
  <div class="notfound-container">
    <div class="illustration">
      <div class="circle"></div>
      <div class="circle small"></div>
      <div class="credit-card">
        <div class="chip"></div>
        <div class="stripe"></div>
      </div>
    </div>

    <header class="header">
      <h1>Oops! Page Not Found 🚫</h1>
      <p>Looks like you’ve wandered off the banking path.</p>
    </header>

    <section class="section">
      <div class="notfound-card">
        <h2>Error 404</h2>
        <p>
          The page you’re looking for doesn’t exist, has been moved, or is temporarily unavailable.
        </p>
        <router-link to="/" class="home-button">🏦 Return to Dashboard</router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'

onMounted(() => {
  document.title = "404 - Page Not Found"
})
</script>

<style scoped>
.notfound-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem;
  font-family: 'Segoe UI', sans-serif;
  background-color: #f5f9ff;
  color: #1a2b4c;
  text-align: center;
}

/* Illustration section */
.illustration {
  position: relative;
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}

.circle {
  width: 120px;
  height: 120px;
  background-color: #e6f0ff;
  border-radius: 50%;
  position: absolute;
  animation: float 4s ease-in-out infinite;
}

.circle.small {
  width: 60px;
  height: 60px;
  background-color: #cfe4ff;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  animation: float 5s ease-in-out infinite reverse;
}

.credit-card {
  width: 160px;
  height: 100px;
  background: linear-gradient(135deg, #1450a3, #2d8cf0);
  border-radius: 12px;
  box-shadow: 0 8px 16px rgba(0, 55, 123, 0.15);
  position: relative;
  z-index: 1;
  transform: rotate(-5deg);
  animation: float 6s ease-in-out infinite;
}

.chip {
  width: 30px;
  height: 20px;
  background-color: gold;
  border-radius: 4px;
  position: absolute;
  top: 15px;
  left: 15px;
}

.stripe {
  width: 100%;
  height: 8px;
  background-color: rgba(255, 255, 255, 0.3);
  position: absolute;
  bottom: 25px;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(-5deg); }
  50% { transform: translateY(-6px) rotate(-5deg); }
}

.header {
  margin-bottom: 1.5rem;
}

.header h1 {
  font-size: 2rem;
  color: #1450a3;
}

.section {
  margin-bottom: 2.5rem;
}

.notfound-card {
  background-color: #ffffff;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 55, 123, 0.1);
  max-width: 500px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease-in-out;
}

.notfound-card h2 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #dc3545;
}

.notfound-card p {
  margin-bottom: 1.5rem;
  color: #455a64;
}

.home-button {
  background-color: #1450a3;
  color: white;
  padding: 0.8rem 2rem;
  border-radius: 6px;
  text-decoration: none;
  font-weight: bold;
  display: inline-block;
  box-shadow: 0 4px 8px rgba(0, 55, 123, 0.2);
  transition: background-color 0.2s ease-in-out, transform 0.2s ease-in-out;
}

.home-button:hover {
  background-color: #0f3a7a;
  transform: translateY(-2px);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
