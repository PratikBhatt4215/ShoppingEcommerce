<template>
  <div class="container mx-auto mt-10 max-w-2xl">
    <h1 class="text-3xl font-bold mb-6">{{ isEdit ? 'Edit Product' : 'Add New Product' }}</h1>

    <div class="bg-white p-8 rounded shadow">
        <form @submit.prevent="saveProduct" class="space-y-4">
            <div>
                <label class="block text-gray-700">Title</label>
                <input v-model="product.title" type="text" class="w-full border p-2 rounded" required>
            </div>
            
             <div>
                <label class="block text-gray-700">Description</label>
                <textarea v-model="product.description" class="w-full border p-2 rounded" rows="3" required></textarea>
            </div>

             <div class="grid grid-cols-2 gap-4">
                <div>
                     <label class="block text-gray-700">Category</label>
                     <select v-model="product.category" class="w-full border p-2 rounded" required>
                         <option v-for="c in categories" :key="c.id" :value="c.name">{{ c.name }}</option>
                     </select>
                </div>
                <div>
                    <label class="block text-gray-700">Price</label>
                    <input v-model="product.price" type="number" class="w-full border p-2 rounded" required>
                </div>
             </div>

             <div class="grid grid-cols-2 gap-4">
                 <div>
                    <label class="block text-gray-700">Stock</label>
                    <input v-model="product.stock" type="number" class="w-full border p-2 rounded" required>
                </div>
                <div>
                    <label class="block text-gray-700">Image</label>
                    <input type="file" @change="handleFileChange" class="w-full border p-2 rounded">
                </div>
             </div>

             <div class="grid grid-cols-2 gap-4">
                 <div>
                    <label class="block text-gray-700">Discount (%)</label>
                    <input v-model="product.discount" type="number" min="0" max="100" class="w-full border p-2 rounded" placeholder="0">
                </div>
                <div class="flex items-center">
                    <label class="flex items-center cursor-pointer">
                        <input v-model="product.isActive" type="checkbox" class="mr-2 w-5 h-5">
                        <span class="text-gray-700 font-semibold">Active</span>
                    </label>
                </div>
             </div>

             <button type="submit" class="w-full bg-blue-600 text-white font-bold py-2 rounded hover:bg-blue-700">
                 {{ isEdit ? 'Update Product' : 'Submit' }}
             </button>
             
             <p v-if="message" class="text-center mt-2" :class="isError? 'text-red-500' : 'text-green-500'">{{ message }}</p>
        </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import api from '../services/api';
import { useRoute, useRouter } from 'vue-router';

const route = useRoute();
const router = useRouter();
const categories = ref([]);
const isEdit = ref(false);
const message = ref('');
const isError = ref(false);
const selectedFile = ref(null);

const product = reactive({
    id: null,
    title: '',
    description: '',
    category: '',
    price: '',
    stock: '',
    image: '',
    discount: 0,
    isActive: true
});

onMounted(async () => {
    // Fetch Categories for dropdown
    const catResponse = await api.get('/admin/category');
    categories.value = catResponse.data.content || catResponse.data;

    // Check if editing
    if (route.params.id) {
        isEdit.value = true;
        const prodResponse = await api.get(`/product/${route.params.id}`); // Reusing public endpoint to get details
        Object.assign(product, prodResponse.data);
    }
});

const handleFileChange = (event) => {
    selectedFile.value = event.target.files[0];
};

const saveProduct = async () => {
    try {
        const formData = new FormData();
        formData.append('title', product.title);
        formData.append('description', product.description);
        formData.append('category', product.category);
        formData.append('price', product.price);
        formData.append('stock', product.stock);
        formData.append('discount', product.discount || 0);
        formData.append('isActive', product.isActive);
        formData.append('id', product.id || ''); // Send ID if update
        
        if (selectedFile.value) {
            formData.append('file', selectedFile.value);
        }

        if (isEdit.value) {
             await api.post('/admin/updateProduct', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
        } else {
             await api.post('/admin/saveProduct', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
        }
       
        message.value = "Product saved successfully!";
        isError.value = false;
        setTimeout(() => router.push('/admin/products'), 1500);

    } catch (error) {
        message.value = "Failed to save product";
        isError.value = true;
    }
};
</script>
