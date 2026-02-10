import { defineStore } from 'pinia';
import api from '../services/api';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: localStorage.getItem('token') || null,
    isAuthenticated: !!localStorage.getItem('token'),
  }),
  actions: {
    async login(username, password) {
      try {
        const response = await api.post('/auth/login', { username, password });
        const { token, user } = response.data;
        this.token = token;
        this.user = user;
        this.isAuthenticated = true;
        localStorage.setItem('token', token);
        return true;
      } catch (error) {
        console.error('Login failed:', error);
        return false;
      }
    },
    async register(userData) {
      try {
        const formData = new FormData();
        Object.keys(userData).forEach(key => formData.append(key, userData[key]));
        await api.post('/auth/register', formData, {
          headers: { 'Content-Type': 'multipart/form-data' } 
        });
        return true;
      } catch (error) {
        console.error('Registration failed:', error);
        return false;
      }
    },
    logout() {
      this.token = null;
      this.user = null;
      this.isAuthenticated = false;
      localStorage.removeItem('token');
    },
    async fetchUser() {
      if (!this.token) return;
      try {
        const response = await api.get('/user/profile');
        this.user = response.data;
      } catch (error) {
        this.logout();
      }
    }
  },
});
