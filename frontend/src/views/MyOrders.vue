<template>
  <div class="container mx-auto px-4 py-8 mt-4">
    <h1 class="text-3xl font-bold mb-6">My Orders</h1>
    
    <div v-if="loading" class="text-center">Loading orders...</div>
    
    <div v-else-if="orders.length === 0" class="text-center text-gray-500">
      You have no orders yet.
    </div>

    <div v-else class="space-y-4">
      <div v-for="order in orders" :key="order.id" class="bg-white p-6 rounded shadow border-l-4" 
           :class="getStatusColor(order.status)">
        <div class="flex justify-between items-start">
          <div>
            <p class="text-sm text-gray-500">Order ID: {{ order.orderId }}</p>
            <p class="text-sm text-gray-500">Date: {{ order.orderDate }}</p>
            <h3 class="text-xl font-bold mt-1">{{ order.product.title }}</h3>
            <p class="text-gray-700">Quantity: {{ order.quantity }}</p>
            <p class="text-gray-700">Price: ${{ order.price }}</p>
          </div>
          <div class="text-right">
             <span class="px-3 py-1 rounded text-white text-sm font-bold" :class="getStatusBadgeColor(order.status)">
               {{ order.status }}
             </span>
             <p class="mt-2 text-lg font-bold">Total: ${{ order.price * order.quantity }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const orders = ref([]);
const loading = ref(true);

onMounted(async () => {
  try {
    const response = await api.get('/user/user-orders');
    orders.value = response.data;
  } catch (error) {
    console.error('Failed to fetch orders:', error);
  } finally {
    loading.value = false;
  }
});

const getStatusColor = (status) => {
  switch(status) {
    case 'Delivered': return 'border-green-500';
    case 'Cancelled': return 'border-red-500';
    default: return 'border-blue-500';
  }
};

const getStatusBadgeColor = (status) => {
  switch(status) {
    case 'Delivered': return 'bg-green-500';
    case 'Cancelled': return 'bg-red-500';
    default: return 'bg-blue-500';
  }
};
</script>
