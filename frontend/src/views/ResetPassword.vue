<template>
  <div class="max-w-md mx-auto bg-white p-8 rounded shadow mt-10">
    <h2 class="text-2xl font-bold mb-6 text-center">Reset Password</h2>
    
    <form @submit.prevent="handleResetPassword" class="space-y-4">
      <div>
        <label class="block text-gray-700">New Password</label>
        <input v-model="password" type="password" class="w-full border p-2 rounded" required />
      </div>
      <div>
        <label class="block text-gray-700">Confirm Password</label>
        <input v-model="confirmPassword" type="password" class="w-full border p-2 rounded" required />
      </div>
      
      <p v-if="message" :class="isError ? 'text-red-500' : 'text-green-500'">{{ message }}</p>

      <button type="submit" class="w-full bg-blue-500 text-white p-2 rounded hover:bg-blue-600">
        Reset Password
      </button>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../services/api';

const route = useRoute();
const router = useRouter();
const password = ref('');
const confirmPassword = ref('');
const message = ref('');
const isError = ref(false);

const token = route.query.token;

const handleResetPassword = async () => {
    if (password.value !== confirmPassword.value) {
        message.value = "Passwords do not match";
        isError.value = true;
        return;
    }

    try {
        // API: POST /reset-password?token=...&password=...
        await api.post('/reset-password', null, { 
            params: { 
                token: token, 
                password: password.value 
            } 
        });
        
        message.value = "Password reset successfully. Redirecting to login...";
        isError.value = false;
        setTimeout(() => router.push('/login'), 2000);
    } catch (error) {
        message.value = "Failed to reset password. Link might be expired.";
        isError.value = true;
    }
};
</script>
