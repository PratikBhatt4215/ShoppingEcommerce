<template>
  <div class="container mx-auto px-4 py-8 mt-4 max-w-4xl">
    <h1 class="text-3xl font-bold mb-6">User Profile</h1>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
      <!-- Profile Information -->
      <div class="bg-white p-6 rounded shadow">
        <h2 class="text-xl font-bold mb-4">Account Details</h2>
        <div class="flex items-center mb-6">
           <img :src="user.profileImage ? `http://localhost:8080/img/profile_img/${user.profileImage}?t=${Date.now()}` : 'https://via.placeholder.com/100'" 
                class="w-24 h-24 rounded-full object-cover mr-4">
           <div>
             <p class="font-bold text-lg">{{ user.name }}</p>
             <p class="text-gray-600">{{ user.email }}</p>
             <p class="text-gray-600">{{ user.role }}</p>
           </div>
        </div>

        <form @submit.prevent="updateProfile" class="space-y-4">
           <div>
             <label class="block text-gray-700 mb-2">Profile Picture</label>
             <input 
               type="file" 
               @change="handleFileChange" 
               accept="image/*"
               class="w-full border p-2 rounded"
             >
             <p class="text-sm text-gray-500 mt-1">Upload a new profile picture (JPG, PNG)</p>
           </div>
           
           <div>
             <label class="block text-gray-700">Full Name</label>
             <input v-model="profileForm.name" type="text" class="w-full border p-2 rounded">
           </div>
           <div>
             <label class="block text-gray-700">Mobile Number</label>
             <input v-model="profileForm.mobileNumber" type="text" class="w-full border p-2 rounded">
           </div>
           
           <p v-if="profileMsg" :class="profileError ? 'text-red-500' : 'text-green-500'">{{ profileMsg }}</p>
           
           <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
             Update Profile
           </button>
        </form>
      </div>

      <!-- Change Password -->
      <div class="bg-white p-6 rounded shadow h-fit">
        <h2 class="text-xl font-bold mb-4">Change Password</h2>
        <form @submit.prevent="changePassword" class="space-y-4">
          <div>
            <label class="block text-gray-700">Current Password</label>
            <input v-model="passwordForm.currentPassword" type="password" class="w-full border p-2 rounded" required>
          </div>
          <div>
            <label class="block text-gray-700">New Password</label>
            <input v-model="passwordForm.newPassword" type="password" class="w-full border p-2 rounded" required>
          </div>
          <div>
            <label class="block text-gray-700">Confirm Password</label>
            <input v-model="passwordForm.confirmPassword" type="password" class="w-full border p-2 rounded" required>
          </div>
          
          <p v-if="passwordMsg" :class="passwordError ? 'text-red-500' : 'text-green-500'">{{ passwordMsg }}</p>
          
          <button type="submit" class="bg-gray-800 text-white px-4 py-2 rounded hover:bg-gray-900">
            Change Password
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import api from '../services/api';
import { useAuthStore } from '../stores/auth';

const authStore = useAuthStore();
const user = ref({});
const profileForm = reactive({ name: '', mobileNumber: '' });
const profileFile = ref(null);
const profileMsg = ref('');
const profileError = ref(false);
const passwordForm = reactive({ currentPassword: '', newPassword: '', confirmPassword: '' });
const passwordMsg = ref('');
const passwordError = ref(false);

onMounted(async () => {
  await authStore.fetchUser();
  if (authStore.user) {
    user.value = authStore.user;
    profileForm.name = authStore.user.name;
    profileForm.mobileNumber = authStore.user.mobileNumber;
  }
});

const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    profileFile.value = file; // Changed to profileFile.value
  }
};

const updateProfile = async () => {
  try {
    const formData = new FormData();
    
    // Get user ID from either user.value or authStore
    const userId = user.value?.id || authStore.user?.id;
    console.log('User ID:', userId);
    console.log('user.value:', user.value);
    console.log('authStore.user:', authStore.user);
    
    if (!userId) {
      console.error('No user ID found!');
      alert('Unable to identify user. Please log in again.');
      return;
    }
    
    formData.append('id', userId);
    formData.append('name', profileForm.name); // Changed to profileForm.name
    formData.append('email', user.value.email);
    formData.append('mobileNumber', profileForm.mobileNumber); // Changed to profileForm.mobileNumber
    
    // Only append image if a file was selected
    if (profileFile.value) { // Changed to profileFile.value
      console.log('Appending image file:', profileFile.value.name); // Changed to profileFile.value
      formData.append('img', profileFile.value); // Changed to profileFile.value
    } else {
      console.log('No image file selected');
    }

    console.log('FormData contents:');
    for (let pair of formData.entries()) {
      console.log(pair[0] + ':', pair[1]);
    }

    const response = await axios.post('http://localhost:8080/user/update-profile', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    console.log('Backend response:', response.data);
    console.log('Updated profile image path:', response.data.profileImage);

    profileMsg.value = 'Profile updated successfully!'; // Changed to use profileMsg
    profileError.value = false; // Changed to use profileError
    
    // Refresh user data
    await authStore.fetchUser();
    
    // Force image reload with cache-busting
    if (response.data.profileImage) {
      user.value.profileImage = response.data.profileImage + `?t=${Date.now()}`;
    }
  } catch (error) {
    console.error('Error updating profile:', error);
    profileMsg.value = error.response?.data || 'Failed to update profile'; // Changed to use profileMsg
    profileError.value = true; // Changed to use profileError
  }
};

const changePassword = async () => {
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
        passwordMsg.value = "New passwords do not match!";
        passwordError.value = true;
        return;
    }

    try {
        const formData = new FormData();
        formData.append('currentPassword', passwordForm.currentPassword);
        formData.append('newPassword', passwordForm.newPassword);
        
        // Ensure backend endpoint matches and returns string message
        const response = await api.post('/user/change-password', null, { 
            params: { 
                currentPassword: passwordForm.currentPassword,
                newPassword: passwordForm.newPassword 
            }
        });
        
        passwordMsg.value = "Password updated successfully";
        passwordError.value = false;
        passwordForm.currentPassword = '';
        passwordForm.newPassword = '';
        passwordForm.confirmPassword = '';
    } catch (error) {
        passwordMsg.value = error.response?.data || "Failed to update password";
        passwordError.value = true;
    }
};
</script>
