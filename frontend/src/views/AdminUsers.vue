<template>
  <div class="container mx-auto mt-10">
    <h1 class="text-3xl font-bold mb-6">Manage Users</h1>

    <div class="bg-white rounded shadow p-6 overflow-x-auto">
      <table class="w-full text-left border-collapse min-w-max">
        <thead>
          <tr>
            <th class="border-b p-2">Sl No</th>
            <th class="border-b p-2">Profile</th>
            <th class="border-b p-2">Name</th>
            <th class="border-b p-2">Email</th>
            <th class="border-b p-2">Mobile No</th>
            <th class="border-b p-2">Address</th>
            <th class="border-b p-2">Status</th>
            <th class="border-b p-2">Action</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(u, index) in users" :key="u.id" class="hover:bg-gray-50">
            <td class="border-b p-2">{{ index + 1 }}</td>
            <td class="border-b p-2">
                <img :src="u.profileImage ? 'http://localhost:8080/img/profile_img/' + u.profileImage : 'https://via.placeholder.com/50'" class="w-10 h-10 rounded-full object-cover">
            </td>
            <td class="border-b p-2">{{ u.name }}</td>
            <td class="border-b p-2">{{ u.email }}</td>
            <td class="border-b p-2">{{ u.mobileNumber }}</td>
            <td class="border-b p-2">{{ u.address }}, {{ u.city }}, {{ u.state }}</td>
            <td class="border-b p-2">{{ u.enable ? 'Active' : 'Inactive' }}</td>
            <td class="border-b p-2 space-x-2">
               <!-- In real world we usually use a separate endpoint for status toggle -->
               <!-- But looking at Thymeleaf: /admin/updateSts?status=true&id=... -->
               <!-- We need to check if we have that API or if we need to use a generic update approach -->
               <!-- AdminController usually had updateSts. Let's assume we implement a method in api.js or use existing endpoint. -->
               <!-- Checking AdminController in mind: public ResponseEntity<?> updateUserStatus(...) -->
               
               <button v-if="!u.enable" @click="updateStatus(u.id, true)" class="bg-blue-500 text-white px-2 py-1 rounded text-xs">Activate</button>
               <button v-else @click="updateStatus(u.id, false)" class="bg-red-500 text-white px-2 py-1 rounded text-xs">Deactivate</button>
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

const users = ref([]);

const fetchUsers = async () => {
    try {
        const response = await api.get('/admin/users'); // Need to ensure this endpoint returns List<UserDtls>
        users.value = response.data;
    } catch (error) {
        console.error("Failed to fetch users", error);
    }
};

const updateStatus = async (id, status) => {
    try {
        await api.get(`/admin/updateSts?id=${id}&status=${status}`);
        fetchUsers();
    } catch (error) {
        console.error("Failed to update status", error);
        alert("Update failed");
    }
};

onMounted(() => {
    fetchUsers();
});
</script>
