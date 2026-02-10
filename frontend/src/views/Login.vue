<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center px-4 py-12">
    <div class="max-w-md w-full bg-white p-8 rounded-2xl shadow-xl border border-gray-200">
      <div class="text-center mb-8">
        <h2 class="text-3xl font-bold text-gray-900 mb-2">Welcome Back</h2>
        <p class="text-gray-600">Sign in to your account</p>
      </div>
      
      <form @submit.prevent="handleLogin" class="space-y-6">
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">Email (Username)</label>
          <input 
            v-model="username" 
            type="text" 
            class="w-full px-4 py-3 border-2 border-gray-300 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-colors bg-white text-gray-900" 
            placeholder="Enter your email"
            required 
          />
        </div>
        
        <div>
          <label class="block text-sm font-semibold text-gray-700 mb-2">Password</label>
          <input 
            v-model="password" 
            type="password" 
            class="w-full px-4 py-3 border-2 border-gray-300 rounded-lg focus:border-blue-500 focus:ring-2 focus:ring-blue-200 transition-colors bg-white text-gray-900" 
            placeholder="Enter your password"
            required 
          />
        </div>
        
        <button 
          type="submit" 
          class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-4 rounded-lg transition-colors shadow-lg hover:shadow-xl transform hover:scale-[1.02]"
        >
          Sign In
        </button>
        
        <p v-if="error" class="text-red-600 text-center font-medium bg-red-50 py-2 px-4 rounded-lg border border-red-200">
          {{ error }}
        </p>
      </form>
      
      <div class="mt-6 text-center">
        <p class="text-gray-600">
          Don't have an account? 
          <router-link to="/register" class="text-blue-600 hover:text-blue-700 font-semibold hover:underline ml-1">
            Register here
          </router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '../stores/auth';
import { useRouter } from 'vue-router';

const username = ref('');
const password = ref('');
const error = ref('');
const authStore = useAuthStore();
const router = useRouter();

const handleLogin = async () => {
  const success = await authStore.login(username.value, password.value);
  if (success) {
    router.push('/');
  } else {
    error.value = 'Invalid credentials';
  }
};
</script>
