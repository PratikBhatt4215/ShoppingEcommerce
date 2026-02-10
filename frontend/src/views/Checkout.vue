<template>
  <div class="container mx-auto px-4 py-8 mt-4 max-w-lg">
    <h1 class="text-3xl font-bold mb-6 text-center">Checkout</h1>
    
    <div class="bg-white p-8 rounded shadow">
      <form @submit.prevent="placeOrder" class="space-y-4">
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-gray-700">First Name</label>
            <input v-model="form.firstName" type="text" class="w-full border p-2 rounded" required />
          </div>
          <div>
            <label class="block text-gray-700">Last Name</label>
            <input v-model="form.lastName" type="text" class="w-full border p-2 rounded" required />
          </div>
        </div>

        <div>
          <label class="block text-gray-700">Email</label>
          <input v-model="form.email" type="email" class="w-full border p-2 rounded" required />
        </div>
        
        <div>
          <label class="block text-gray-700">Mobile Number</label>
          <input v-model="form.mobileNo" type="text" class="w-full border p-2 rounded" required />
        </div>

        <div>
          <label class="block text-gray-700">Address</label>
          <textarea v-model="form.address" class="w-full border p-2 rounded" rows="3" required></textarea>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-gray-700">City</label>
            <input v-model="form.city" type="text" class="w-full border p-2 rounded" required />
          </div>
          <div>
            <label class="block text-gray-700">State</label>
            <input v-model="form.state" type="text" class="w-full border p-2 rounded" required />
          </div>
        </div>

        <div>
           <label class="block text-gray-700">Pincode</label>
           <input v-model="form.pincode" type="text" class="w-full border p-2 rounded" required />
        </div>

        <div>
           <label class="block text-gray-700 font-bold mb-2">Payment Type</label>
           <div class="flex items-center space-x-4">
             <label class="flex items-center">
               <input type="radio" v-model="form.paymentType" value="COD" class="mr-2"> Cash on Delivery
             </label>
             <label class="flex items-center">
               <input type="radio" v-model="form.paymentType" value="ONLINE" class="mr-2" disabled> Online Payment (Coming Soon)
             </label>
           </div>
        </div>

        <button type="submit" class="w-full bg-blue-600 text-white font-bold py-2 rounded hover:bg-blue-700 mt-4">
          Place Order
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { reactive } from 'vue';
import api from '../services/api';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const form = reactive({
  firstName: '',
  lastName: '',
  email: '',
  mobileNo: '',
  address: '',
  city: '',
  state: '',
  pincode: '',
  paymentType: 'COD'
});

const router = useRouter();
const authStore = useAuthStore();

// Pre-fill email/mobile if available in auth store
if (authStore.user) {
  form.email = authStore.user.email;
  form.mobileNo = authStore.user.mobileNumber;
}

const placeOrder = async () => {
  try {
    const formData = new FormData();
    for (const key in form) {
      formData.append(key, form[key]);
    }
    
    await api.post('/user/save-order', formData);
    alert('Order Placed Successfully!');
    router.push('/my-orders');
  } catch (error) {
    console.error('Failed to place order:', error);
    const errorMsg = error.response?.data || 'Failed to place order. Please try again.';
    alert(errorMsg);
  }
};
</script>
