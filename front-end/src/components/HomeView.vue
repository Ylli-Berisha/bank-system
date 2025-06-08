<template>
  <div class="home-container">
    <h1>Welcome to the Home Page</h1>
    <p>You are successfully logged in!</p>
  </div>
</template>

<script>
export default {
  name: 'HomeView',
};
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f0f4f8;
  text-align: center;
  color: #1e3a8a;
}

.home-container h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.home-container p {
  font-size: 1.2rem;
  color: #334155;
}
</style>
