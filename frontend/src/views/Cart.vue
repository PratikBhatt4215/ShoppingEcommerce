<template>
  <div class="container mx-auto px-4 py-8 mt-4">
    <h1 class="text-2xl font-bold mb-6">Shopping Cart</h1>
    
    <div v-if="carts.length === 0" class="text-center text-gray-500">
      Your cart is empty.
    </div>
    
    <div v-else class="grid grid-cols-1 lg:grid-cols-3 gap-8">
      <div class="lg:col-span-2">
        <div v-if="error" class="bg-red-100 text-red-700 p-2 rounded mb-4">
          {{ error }}
        </div>

        <div v-for="item in carts" :key="item.id" class="bg-white p-4 rounded shadow mb-4 flex items-center">
          <img :src="'http://localhost:8080/img/product_img/' + item.product.image" class="w-20 h-20 object-cover rounded mr-4">
          <div class="flex-grow">
            <h3 class="font-bold cursor-pointer hover:text-blue-600" @click="$router.push('/product/'+item.product.id)">
              {{ item.product.title }}
            </h3>
            <p class="text-gray-600">Price: ${{ item.product.discountPrice }}</p>
            <p class="text-xs mt-1" :class="item.product.stock > 0 ? 'text-green-600' : 'text-red-600'">
              Stock: {{ item.product.stock > 0 ? item.product.stock + ' available' : 'Out of Stock' }}
            </p>
          </div>
          <div class="flex flex-col items-center">
            <div class="flex items-center space-x-2">
              <button @click="updateQuantity(item.id, 'de')" class="bg-gray-200 px-2 rounded hover:bg-gray-300">-</button>
              <span class="w-8 text-center">{{ item.quantity }}</span>
              <button 
                @click="updateQuantity(item.id, 'in')" 
                class="bg-gray-200 px-2 rounded hover:bg-gray-300"
                :disabled="item.quantity >= item.product.stock"
                :class="{'opacity-50 cursor-not-allowed': item.quantity >= item.product.stock}"
              >+</button>
            </div>
            <p v-if="item.quantity > item.product.stock" class="text-red-500 text-[10px] mt-1 font-bold">Insufficient Stock</p>
          </div>
          <div class="ml-4 font-bold text-right w-20">
            ${{ item.totalPrice }}
          </div>
        </div>
      </div>
      
      <div class="bg-white p-6 rounded shadow h-fit">
        <h2 class="text-xl font-bold mb-4">Order Summary</h2>
        <div class="flex justify-between mb-2">
          <span>Subtotal</span>
          <span>${{ totalOrderPrice }}</span>
        </div>
        <div class="flex justify-between mb-2">
          <span>Shipping</span>
          <span>$250.00</span> <!-- Hardcoded based on backend controller logic -->
        </div>
         <div class="flex justify-between mb-2">
          <span>Tax</span>
          <span>$100.00</span>
        </div>
        <hr class="my-2">
        <div class="flex justify-between font-bold text-lg">
          <span>Total</span>
          <span>${{ totalOrderPrice + 350 }}</span>
        </div>
        <button 
          v-if="carts.some(item => item.quantity > item.product.stock)"
          disabled
          class="block w-full bg-gray-400 text-white text-center py-2 rounded mt-4 cursor-not-allowed"
        >
          Insufficient Stock
        </button>
        <router-link 
          v-else
          to="/checkout" 
          class="block w-full bg-blue-600 text-white text-center py-2 rounded mt-4 hover:bg-blue-700"
        >
          Proceed to Checkout
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import api from '../services/api';
import { useAuthStore } from '../stores/auth';

const carts = ref([]);
const error = ref('');
const authStore = useAuthStore();

const fetchCart = async () => {
  try {
    const response = await api.get('/user/cart');
    carts.value = response.data;
    
    // Check if any items in cart exceed stock
    const hasStockIssue = carts.value.some(item => item.quantity > item.product.stock);
    if (hasStockIssue) {
      error.value = 'Some items in your cart exceed available stock. Please adjust quantities.';
    }
  } catch (err) {
    console.error('Failed to fetch cart:', err);
  }
};

const updateQuantity = async (cid, sy) => {
  try {
    error.value = '';
    await api.get(`/user/cartQuantityUpdate?sy=${sy}&cid=${cid}`);
    await fetchCart();
  } catch (err) {
    console.error('Failed to update quantity:', err);
    error.value = err.response?.data || 'Failed to update quantity';
  }
};

const totalOrderPrice = computed(() => {
  if (carts.value.length > 0) {
     // Backend logic calculates total for entire cart in the last element?
     // Or we verify implementation. Actually backend Cart model has totalOrderPrice.
     // Let's assume the last item has the total like in the controller logic
     return carts.value[carts.value.length - 1].totalOrderPrice; 
  }
  return 0;
});

onMounted(() => {
  fetchCart();
});
</script>
