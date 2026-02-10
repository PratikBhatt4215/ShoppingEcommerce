<template>
  <nav class="bg-white shadow-md sticky top-0 z-50">
    <div class="px-4 md:px-8 lg:px-12 xl:px-16 py-4 flex justify-between items-center">
      <!-- Logo -->
      <router-link to="/" class="flex items-center gap-2 text-2xl font-extrabold text-blue-600 tracking-wide hover:text-blue-700 transition">
        <i class="fas fa-shopping-bag"></i>
        <span>E-Shop</span>
      </router-link>

      <!-- Search Bar (Desktop) -->
      <div class="hidden md:flex flex-grow max-w-lg mx-8 relative">
        <input 
          type="text" 
          placeholder="Search for products..." 
          class="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent transition shadow-sm"
        >
        <i class="fas fa-search absolute left-3 top-3 text-gray-400"></i>
      </div>

      <!-- Navigation Links -->
      <div class="flex items-center space-x-6 font-medium text-gray-700">
        <router-link to="/" class="hover:text-blue-600 transition flex items-col items-center gap-1">
          <i class="fas fa-home text-lg"></i>
          <span class="hidden lg:inline">Home</span>
        </router-link>

        <template v-if="authStore.isAuthenticated">
          <!-- Admin Link -->
          <router-link v-if="authStore.user?.role === 'ROLE_ADMIN'" to="/admin" class="hover:text-blue-600 transition flex items-center gap-1">
             <i class="fas fa-tachometer-alt text-lg"></i>
             <span class="hidden lg:inline">Admin</span>
          </router-link>

          <!-- Cart -->
          <router-link to="/cart" class="hover:text-blue-600 transition relative flex items-center gap-1">
             <i class="fas fa-shopping-cart text-lg"></i>
             <span class="hidden lg:inline">Cart</span>
             <span v-if="cartCount > 0" class="absolute -top-2 -right-2 bg-red-500 text-white text-xs font-bold px-1.5 py-0.5 rounded-full shadow-sm">{{ cartCount }}</span>
          </router-link>
          
          <!-- User Dropdown (Simplified as links for now) -->
          <router-link to="/profile" class="hover:text-blue-600 transition flex items-center gap-1">
             <i class="fas fa-user-circle text-lg"></i>
             <span class="hidden lg:inline">{{ authStore.user?.name }}</span>
          </router-link>

          <button @click="handleLogout" class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-full text-sm font-bold shadow transition transform hover:scale-105">
            Logout
          </button>
        </template>
        
        <template v-else>
          <router-link to="/login" class="hover:text-blue-600 transition font-bold">Login</router-link>
          <router-link to="/register" class="bg-blue-600 hover:bg-blue-700 text-white px-5 py-2 rounded-full font-bold shadow-lg transition transform hover:scale-105">
            Register
          </router-link>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';
import { computed } from 'vue';

const authStore = useAuthStore();
const router = useRouter();

// Mock cart count or fetch from store
const cartCount = computed(() => 0); // Replace with actual store getter later

const handleLogout = () => {
  authStore.logout();
  router.push('/login');
};
</script>
