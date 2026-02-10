<template>
  <div class="container mx-auto px-4 py-8 mt-4">
    <h1 class="text-3xl font-bold mb-6">Admin Dashboard</h1>
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <router-link to="/admin/category" class="bg-blue-500 text-white p-6 rounded shadow cursor-pointer hover:bg-blue-600 transition block">
         <h2 class="text-xl font-bold">Manage Categories</h2>
         <p>Add, edit or delete categories</p>
      </router-link>
       <router-link to="/admin/products" class="bg-green-500 text-white p-6 rounded shadow cursor-pointer hover:bg-green-600 transition block">
         <h2 class="text-xl font-bold">Manage Products</h2>
         <p>Add, edit or delete products</p>
      </router-link>
       <router-link to="/admin/orders" class="bg-purple-500 text-white p-6 rounded shadow cursor-pointer hover:bg-purple-600 transition block">
         <h2 class="text-xl font-bold">Manage Orders</h2>
         <p>View and update order status</p>
      </router-link>
       <router-link to="/admin/users" class="bg-indigo-500 text-white p-6 rounded shadow cursor-pointer hover:bg-indigo-600 transition block">
         <h2 class="text-xl font-bold">Manage Users</h2>
         <p>View and update user status</p>
      </router-link>
    </div>
    <!-- Implement actual management views/components here or link to them -->
  </div>
</template>

<script setup>
</script>
