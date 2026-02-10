<template>
  <div class="bg-white min-h-screen pb-10">
    <!-- Hero Section (Optional) -->
    <div class="bg-blue-600 text-white py-12 mb-8 shadow-lg">
        <div class="px-4 md:px-8 lg:px-12 xl:px-16 text-center">
            <h1 class="text-4xl md:text-5xl font-extrabold mb-4">Welcome to E-Shop</h1>
            <p class="text-lg md:text-xl text-blue-100 mb-6">Discover amazing products at unbeatable prices.</p>
            <a href="#products" class="bg-white text-blue-600 px-6 py-3 rounded-full font-bold shadow hover:bg-gray-100 transition">Shop Now</a>
        </div>
    </div>

    <div class="px-4 md:px-8 lg:px-12 xl:px-16 flex flex-col md:flex-row gap-8" id="products">
      
      <!-- Sidebar Filters -->
      <div class="w-full md:w-1/4 lg:w-1/5">
         <!-- Search Mobile Visibility -->
         <div class="md:hidden mb-6">
             <form @submit.prevent="handleSearch" class="relative">
                 <input v-model="searchQuery" type="text" class="w-full border border-gray-300 pl-10 pr-4 py-2 rounded-lg focus:ring-2 focus:ring-blue-500 focus:outline-none" placeholder="Search products...">
                 <i class="fas fa-search absolute left-3 top-3 text-gray-400"></i>
             </form>
         </div>

         <div class="bg-white p-6 rounded-xl shadow-sm border border-gray-100 sticky top-24">
             <h3 class="font-bold text-lg mb-4 text-gray-800 border-b pb-2">Categories</h3>
             <ul class="space-y-1">
                 <li>
                     <a href="#" @click.prevent="filterByCategory('')" 
                        class="block px-3 py-2 rounded-lg transition-colors duration-200" 
                        :class="selectedCategory === '' ? 'bg-blue-50 text-blue-700 font-bold' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'">
                        All Products
                     </a>
                 </li>
                 <li v-for="cat in categories" :key="cat.id">
                     <a href="#" @click.prevent="filterByCategory(cat.name)" 
                        class="block px-3 py-2 rounded-lg transition-colors duration-200"
                        :class="selectedCategory === cat.name ? 'bg-blue-50 text-blue-700 font-bold' : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'">
                        {{ cat.name }}
                     </a>
                 </li>
             </ul>
         </div>
      </div>

      <!-- Product Grid -->
      <div class="w-full md:w-3/4 lg:w-4/5">
        <div class="flex justify-between items-center mb-6">
            <h2 class="text-2xl font-bold text-gray-800">
                {{ selectedCategory ? selectedCategory : 'All Products' }}
            </h2>
            <div class="text-gray-500 text-sm">Showing {{ products.length }} results</div>
        </div>
        
        <div v-if="loading" class="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-4 gap-6">
            <!-- Skeleton Loader -->
            <div v-for="n in 4" :key="n" class="bg-white rounded-xl shadow p-4 animate-pulse">
                <div class="h-48 bg-gray-200 rounded-lg mb-4"></div>
                <div class="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
                <div class="h-4 bg-gray-200 rounded w-1/2"></div>
            </div>
        </div>
        
        <div v-else-if="products.length === 0" class="bg-white rounded-xl shadow p-12 text-center">
            <div class="text-gray-300 text-6xl mb-4"><i class="fas fa-box-open"></i></div>
            <h3 class="text-xl font-bold text-gray-700">No Products Found</h3>
            <p class="text-gray-500">Try adjusting your search or filter.</p>
        </div>
        
        <div v-else>
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
                <!-- Product Card -->
                <div v-for="product in products" :key="product.id" class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden hover:shadow-lg transition-shadow duration-300 flex flex-col">
                    <div class="relative group h-48 overflow-hidden bg-gray-100">
                        <img :src="getProductImage(product.image)" alt="Product" class="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110">
                        <div v-if="product.discount > 0" class="absolute top-2 right-2 bg-red-500 text-white text-xs font-bold px-2 py-1 rounded-full shadow">
                            -{{ product.discount }}%
                        </div>
                    </div>
                    
                    <div class="p-4 flex-grow flex flex-col">
                        <div class="text-xs text-blue-500 font-bold uppercase tracking-wider mb-1 flex justify-between">
                            <span>{{ product.category }}</span>
                            <span v-if="product.stock <= 0" class="text-red-500">Out of Stock</span>
                            <span v-else-if="product.stock <= 5" class="text-orange-500">Only {{ product.stock }} left</span>
                        </div>
                        <h3 class="text-lg font-bold text-gray-800 leading-tight mb-2 line-clamp-2 hover:text-blue-600 cursor-pointer" @click="$router.push('/product/'+product.id)">
                            {{ product.title }}
                        </h3>
                        
                        <div class="mt-auto pt-4 flex justify-between items-end border-t border-gray-50 mt-2">
                            <div>
                                <div class="text-xl font-extrabold text-blue-600">${{ product.discountPrice }}</div>
                                <div v-if="product.discount > 0" class="text-sm text-gray-400 line-through">${{ product.price }}</div>
                            </div>
                            <router-link :to="'/product/' + product.id" class="bg-gray-900 hover:bg-blue-600 text-white w-10 h-10 flex items-center justify-center rounded-full transition-colors shadow">
                                <i class="fas fa-arrow-right"></i>
                            </router-link>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Pagination -->
            <div class="mt-10 flex justify-center items-center space-x-2" v-if="totalPages > 1">
                <button @click="changePage(pageNo - 1)" :disabled="pageNo === 0" class="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition shadow-sm">
                    <i class="fas fa-chevron-left mr-1"></i> Prev
                </button>
                <div class="flex space-x-1">
                     <span class="w-10 h-10 flex items-center justify-center bg-blue-600 text-white font-bold rounded-lg shadow">{{ pageNo + 1 }}</span>
                     <!-- More advanced pagination could go here -->
                </div>
                 <button @click="changePage(pageNo + 1)" :disabled="pageNo === totalPages - 1" class="px-4 py-2 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition shadow-sm">
                    Next <i class="fas fa-chevron-right ml-1"></i>
                </button>
            </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import api from '../services/api';
import { useRoute } from 'vue-router';

const categories = ref([]);
const products = ref([]);
const loading = ref(true);
const searchQuery = ref('');
const selectedCategory = ref('');
const pageNo = ref(0);
const pageSize = ref(12);
const totalPages = ref(0);

const fetchCategories = async () => {
    try {
        const response = await api.get('/home');
        categories.value = response.data.category;
    } catch(e) {
        console.error("Failed to load categories");
    }
};

const fetchProducts = async () => {
  loading.value = true;
  try {
    const params = {
        category: selectedCategory.value,
        ch: searchQuery.value,
        pageNo: pageNo.value,
        pageSize: pageSize.value
    };
    
    // Fallback if /products endpoint is not working exactly as expected, 
    // but assuming standard Page<Product> return from Controller
    // Controller: @GetMapping("/products") ... returns Page<Product>
    const response = await api.get('/products', { params });
    
    if (response.data && response.data.content) {
         products.value = response.data.content;
         totalPages.value = response.data.totalPages;
    } else {
        // Fallback for different response structure if any
        products.value = [];
    }
   
  } catch (error) {
    console.error('Failed to fetch products:', error);
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
    pageNo.value = 0;
    fetchProducts();
};

const filterByCategory = (cat) => {
    selectedCategory.value = cat;
    pageNo.value = 0;
    fetchProducts();
};

const changePage = (newPage) => {
    if(newPage >= 0 && newPage < totalPages.value) {
        pageNo.value = newPage;
        fetchProducts();
        window.scrollTo({ top: 0, behavior: 'smooth' });
    }
};

const getProductImage = (image) => {
    return image ? `http://localhost:8080/img/product_img/${image}` : 'https://via.placeholder.com/300?text=No+Image';
}

onMounted(() => {
    fetchCategories();
    fetchProducts();
});
</script>
