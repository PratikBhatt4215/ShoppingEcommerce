<template>
  <div v-if="product" class="container mx-auto px-4 py-8 mt-4 bg-white rounded shadow-lg">
    <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
      <div>
        <img :src="'http://localhost:8080/img/product_img/' + product.image" alt="Product" class="w-full rounded shadow-lg">
      </div>
      <div>
        <h1 class="text-3xl font-bold mb-4">{{ product.title }}</h1>
        <p class="text-gray-600 mb-4">{{ product.description }}</p>
        <p class="text-gray-800 font-semibold mb-2">Category: {{ product.category }}</p>
        
        <!-- Stock Availability -->
        <div class="mb-4">
          <span v-if="product.stock > 10" class="text-green-600 font-semibold">✓ In Stock</span>
          <span v-else-if="product.stock > 0 && product.stock <= 10" class="text-orange-600 font-semibold">
            ⚠ Only {{ product.stock }} left in stock
          </span>
          <span v-else class="text-red-600 font-semibold">✗ Out of Stock</span>
        </div>
        
        <div class="flex items-center space-x-4 mb-6">
          <span class="text-2xl font-bold text-green-600">${{ product.discountPrice }}</span>
          <span v-if="product.discount > 0" class="text-lg line-through text-gray-400">${{ product.price }}</span>
          <span v-if="product.discount > 0" class="text-sm bg-red-100 text-red-600 px-2 py-1 rounded">
            {{ product.discount }}% OFF
          </span>
        </div>

        <div class="flex space-x-4">
          <button 
            @click="addToCart" 
            :disabled="product.stock <= 0 || !product.isActive"
            :class="product.stock <= 0 || !product.isActive ? 'bg-gray-400 cursor-not-allowed' : 'bg-yellow-500 hover:bg-yellow-600'"
            class="text-white px-6 py-2 rounded font-bold"
          >
            {{ product.stock <= 0 ? 'Out of Stock' : 'Add to Cart' }}
          </button>
          <router-link to="/" class="bg-gray-500 text-white px-6 py-2 rounded hover:bg-gray-600">
            Back
          </router-link>
        </div>

        <div v-if="message" :class="`mt-4 p-2 rounded ${isError ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'}`">
          {{ message }}
        </div>
      </div>
    </div>
  </div>
  <div v-else class="text-center mt-10">Loading...</div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import api from '../services/api';
import { useAuthStore } from '../stores/auth';

const route = useRoute();
const authStore = useAuthStore();
const product = ref(null);
const message = ref('');
const isError = ref(false);

onMounted(async () => {
  try {
    const response = await api.get(`/product/${route.params.id}`);
    product.value = response.data;
  } catch (error) {
    console.error('Failed to fetch product:', error);
  }
});

const addToCart = async () => {
  if (!authStore.isAuthenticated) {
    message.value = 'Please login to add items to cart';
    isError.value = true;
    return;
  }
  
  if (product.value.stock <= 0) {
    message.value = 'This product is out of stock';
    isError.value = true;
    return;
  }
  
  try {
    await api.get(`/user/addCart?pid=${product.value.id}&uid=${authStore.user.id}`);
    message.value = 'Product added to cart successfully';
    isError.value = false;
    
    // Refresh product to get updated stock info
    const response = await api.get(`/product/${route.params.id}`);
    product.value = response.data;
  } catch (error) {
    // Handle backend stock validation errors
    const errorMsg = error.response?.data || 'Failed to add product to cart';
    message.value = errorMsg;
    isError.value = true;
    
    // Refresh product to get current stock
    try {
      const response = await api.get(`/product/${route.params.id}`);
      product.value = response.data;
    } catch (e) {
      console.error('Failed to refresh product:', e);
    }
  }
};
</script>
