<template>
  <div class="max-w-md mx-auto bg-white p-8 rounded shadow mt-10">
    <h2 class="text-2xl font-bold mb-6 text-center">Forgot Password</h2>
    
    <div v-if="!submitted">
        <form @submit.prevent="handleForgotPassword" class="space-y-4">
        <div>
            <label class="block text-gray-700">Enter your Email</label>
            <input v-model="email" type="email" class="w-full border p-2 rounded" required />
        </div>
        <button type="submit" class="w-full bg-blue-500 text-white p-2 rounded hover:bg-blue-600">
            Send Reset Link
        </button>
        </form>
    </div>
    
    <div v-else class="text-center">
        <p :class="isError ? 'text-red-500' : 'text-green-500'">{{ message }}</p>
        <router-link to="/login" class="block mt-4 text-blue-500 hover:underline">Back to Login</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import api from '../services/api';

const email = ref('');
const message = ref('');
const isError = ref(false);
const submitted = ref(false);

const handleForgotPassword = async () => {
  try {
    // API: POST /forgot-password?email=...
    // Note: In HomeController it's /api/forgot-password, accepting @RequestParam email
    // Check axios base URL. Usually /api. So /api/forgot-password
    const formData = new FormData();
    formData.append('email', email.value);
    
    const response = await api.post('/forgot-password', formData); // Axios might send as JSON if object, but controller expects Param. 
    // Ideally use Params or Form Data.
    // Let's use query param for safety if Controller uses @RequestParam without @RequestBody
    // Actually HomeController: public ResponseEntity processForgotPassword(@RequestParam String email, ...)
    // So api.post('/forgot-password', null, { params: { email: email.value } })
    
    // Correction: In axios, post params are in 3rd argument if 2nd is data.
    
    await api.post('/forgot-password', null, { params: { email: email.value } });
    
    message.value = "Reset link sent successfully to your email.";
    isError.value = false;
  } catch (error) {
    message.value = "Failed to send reset link. Email might not exist.";
    isError.value = true;
  } finally {
      submitted.value = true;
  }
};
</script>
