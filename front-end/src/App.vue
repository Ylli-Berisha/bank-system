<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore.js';

const authStore = useAuthStore();
const router = useRouter();

onMounted(() => {
  if (!authStore.isTokenValid()) {
    authStore.logOut();
    router.push('/login');
  }
});
</script>

<template>
  <router-view />
</template>

<style>
html, body, #app {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
}
</style>
