<template>
  <div class="container mx-auto mt-10">
    <h1 class="text-3xl font-bold mb-6">Manage Orders</h1>

    <div class="bg-white rounded shadow p-6 overflow-x-auto">
      <table class="w-full text-left border-collapse min-w-max">
        <thead>
          <tr>
            <th class="border-b p-2">Order ID</th>
            <th class="border-b p-2">Name</th>
            <th class="border-b p-2">Email</th>
            <th class="border-b p-2">Address</th>
            <th class="border-b p-2">Product</th>
            <th class="border-b p-2">Price</th>
            <th class="border-b p-2">Status</th>
            <th class="border-b p-2">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in orders" :key="order.id" class="hover:bg-gray-50">
            <td class="border-b p-2">{{ order.orderId }}</td>
            <td class="border-b p-2">{{ order.orderAddress.firstName }} {{ order.orderAddress.lastName }}</td>
            <td class="border-b p-2">{{ order.orderAddress.email }}</td>
            <td class="border-b p-2">
                {{ order.orderAddress.address }}, {{ order.orderAddress.city }}, {{ order.orderAddress.state }}
            </td>
            <td class="border-b p-2">{{ order.product.title }}</td>
             <td class="border-b p-2">
                 {{ order.quantity }} x ${{ order.price }} = <b>${{ order.quantity * order.price }}</b>
             </td>
            <td class="border-b p-2">
                <span class="px-2 py-1 rounded text-white text-xs" :class="getStatusColor(order.status)">
                    {{ order.status }}
                </span>
            </td>
            <td class="border-b p-2">
                <form @submit.prevent="updateStatus(order.id, $event)">
                    <div class="flex space-x-2">
                        <select name="st" class="border p-1 rounded text-sm">
                            <option value="">--Status--</option>
                            <option value="1">In Progress</option>
                            <option value="2">Order Recieved</option>
                            <option value="3">Product Packed</option>
                            <option value="4">Out for Delivery</option>
                            <option value="5">Delivered</option>
                            <option value="6">Cancelled</option>
                        </select>
                        <button type="submit" class="bg-blue-500 text-white px-2 py-1 rounded text-xs hover:bg-blue-600">Update</button>
                    </div>
                </form>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';

const orders = ref([]);

const fetchOrders = async () => {
    try {
        const response = await api.get('/admin/orders');
        // Backend returns Page<ProductOrder>
        orders.value = response.data.content || response.data;
    } catch(error) {
        console.error("Failed to fetch orders", error);
    }
};

const updateStatus = async (id, event) => {
    const statusVal = event.target.elements.st.value;
    if (!statusVal) return; // verify selection

    try {
        // Backend: /admin/update-order-status?id=...&st=...
        await api.get(`/admin/update-order-status?id=${id}&st=${statusVal}`);
        alert("Status Updated");
        fetchOrders();
    } catch (error) {
        alert("Failed to update status");
    }
};

const getStatusColor = (status) => {
  switch(status) {
    case 'Delivered': return 'bg-green-500';
    case 'Cancelled': return 'bg-red-500';
    default: return 'bg-blue-500';
  }
};

onMounted(() => {
    fetchOrders();
});
</script>
