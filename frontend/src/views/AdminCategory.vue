<template>
  <div class="container mx-auto mt-10">
    <h1 class="text-3xl font-bold mb-6">Manage Categories</h1>

    <!-- Add Category Form -->
    <div class="bg-gray-100 p-6 rounded shadow mb-8">
      <h2 class="text-xl font-bold mb-4">Add New Category</h2>
      <form @submit.prevent="addCategory" class="flex gap-4">
        <input v-model="newCategoryName" type="text" placeholder="Enter Category Name" class="border p-2 rounded flex-grow" required />
        
        <div class="flex items-center">
             <label class="mr-2">Active:</label>
             <input v-model="newCategoryActive" type="checkbox" class="h-5 w-5">
        </div>

        <input type="file" @change="handleFileChange" class="border p-2 rounded" required />
        
        <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">Save</button>
      </form>
      <p v-if="message" :class="isError ? 'text-red-500' : 'text-green-500'" class="mt-2">{{ message }}</p>
    </div>

    <!-- Category List -->
    <div class="bg-white rounded shadow p-6">
      <table class="w-full text-left border-collapse">
        <thead>
          <tr>
            <th class="border-b p-2">ID</th>
            <th class="border-b p-2">Category</th>
            <th class="border-b p-2">Status</th>
            <th class="border-b p-2">Image</th>
            <th class="border-b p-2">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="cat in categories" :key="cat.id" class="hover:bg-gray-50">
            <td class="border-b p-2">{{ cat.id }}</td>
            <td class="border-b p-2">{{ cat.name }}</td>
            <td class="border-b p-2">{{ cat.isActive ? 'Active' : 'Inactive' }}</td>
            <td class="border-b p-2">
                <img :src="'/img/category_img/' + cat.imageName" class="w-12 h-12 object-cover rounded">
            </td>
            <td class="border-b p-2">
              <button @click="deleteCategory(cat.id)" class="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 text-sm">Delete</button>
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

const categories = ref([]);
const newCategoryName = ref('');
const newCategoryActive = ref(true);
const selectedFile = ref(null);
const message = ref('');
const isError = ref(false);

const fetchCategories = async () => {
  try {
    const response = await api.get('/admin/category');
    // The backend returns a Page object, so we likely need response.data.content or just response.data if we adjusted it differently. 
    // Checking AdminController.category: returns ResponseEntity.ok(page) where page is Page<Category>
    // So categories are in response.data.content
    categories.value = response.data.content || response.data; 
  } catch (error) {
    console.error('Failed to fetch categories', error);
  }
};

const handleFileChange = (event) => {
  selectedFile.value = event.target.files[0];
};

const addCategory = async () => {
    try {
        const formData = new FormData();
        formData.append('name', newCategoryName.value);
        formData.append('isActive', newCategoryActive.value);
        formData.append('file', selectedFile.value);

        await api.post('/admin/saveCategory', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
        
        message.value = "Category added successfully";
        isError.value = false;
        newCategoryName.value = '';
        selectedFile.value = null;
        fetchCategories(); // Refresh list
    } catch (error) {
        message.value = "Failed to add category";
        isError.value = true;
    }
};

const deleteCategory = async (id) => {
    if(!confirm("Are you sure you want to delete this category?")) return;
    
    try {
        await api.delete(`/admin/deleteCategory/${id}`);
        fetchCategories();
    } catch (error) {
        alert("Failed to delete category");
    }
};

onMounted(() => {
    fetchCategories();
});
</script>
