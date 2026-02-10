<template>
  <div class="min-h-screen bg-white flex flex-col">
    <Navbar />
    <main class="flex-grow">
      <router-view />
    </main>
    <footer class="bg-gray-800 text-white p-4 text-center">
      &copy; 2024 E-Commerce App
    </footer>
  </div>
</template>

<script setup>
import Navbar from './components/Navbar.vue';
import { useAuthStore } from './stores/auth';
import { onMounted } from 'vue';

const authStore = useAuthStore();

onMounted(() => {
  authStore.fetchUser();
});
</script>
