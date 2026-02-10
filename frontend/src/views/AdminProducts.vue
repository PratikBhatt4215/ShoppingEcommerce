<template>
  <div class="container mx-auto mt-10">
    <h1 class="text-3xl font-bold mb-6">Manage Products</h1>

    <div class="flex justify-end mb-4">
        <router-link to="/admin/add-product" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
            + Add Product
        </router-link>
    </div>

    <!-- Product List -->
    <div class="bg-white rounded shadow p-6 overflow-x-auto">
      <table class="w-full text-left border-collapse min-w-max">
        <thead>
          <tr>
            <th class="border-b p-2">ID</th>
            <th class="border-b p-2">Image</th>
            <th class="border-b p-2">Title</th>
            <th class="border-b p-2">Category</th>
            <th class="border-b p-2">Price</th>
            <th class="border-b p-2">Stock</th>
            <th class="border-b p-2">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="p in products" :key="p.id" class="hover:bg-gray-50">
            <td class="border-b p-2">{{ p.id }}</td>
            <td class="border-b p-2">
                <img :src="'http://localhost:8080/img/product_img/' + p.image" class="w-12 h-12 object-cover rounded">
            </td>
            <td class="border-b p-2">{{ p.title }}</td>
            <td class="border-b p-2">{{ p.category }}</td>
            <td class="border-b p-2">${{ p.discountPrice }} <span class="text-xs text-gray-400 line-through">${{p.price}}</span></td>
             <td class="border-b p-2">{{ p.stock }}</td>
            <td class="border-b p-2 space-x-2">
              <button @click="editProduct(p.id)" class="bg-yellow-500 text-white px-2 py-1 rounded text-sm">Edit</button>
              <button @click="deleteProduct(p.id)" class="bg-red-500 text-white px-2 py-1 rounded text-sm">Delete</button>
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
import { useRouter } from 'vue-router';

const products = ref([]);
const router = useRouter();

const fetchProducts = async () => {
    try {
        const response = await api.get('/admin/products');
        // Backend returns Page<Product>
        products.value = response.data.content || response.data;
    } catch (error) {
        console.error("Failed to fetch products", error);
    }
};

const deleteProduct = async (id) => {
    if(!confirm("Delete this product?")) return;
    try {
        await api.get(`/admin/deleteProduct/${id}`);
        fetchProducts();
    } catch(error) {
        alert("Failed to delete product");
    }
};

const editProduct = (id) => {
    // Navigate to edit page (user reuse Add Product form with ID)
    router.push(`/admin/edit-product/${id}`);
};

onMounted(() => {
    fetchProducts();
});
</script>
