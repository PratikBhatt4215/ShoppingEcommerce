import { createRouter, createWebHistory } from 'vue-router';
import Home from '../views/Home.vue';
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import ProductDetails from '../views/ProductDetails.vue';
import Cart from '../views/Cart.vue';
import AdminDashboard from '../views/AdminDashboard.vue';
import Checkout from '../views/Checkout.vue';
import MyOrders from '../views/MyOrders.vue';
import Profile from '../views/Profile.vue';
import AdminCategory from '../views/AdminCategory.vue';
import AdminProducts from '../views/AdminProducts.vue';
import AdminAddProduct from '../views/AdminAddProduct.vue';
import AdminOrders from '../views/AdminOrders.vue';
import AdminUsers from '../views/AdminUsers.vue';
import ForgotPassword from '../views/ForgotPassword.vue';
import ResetPassword from '../views/ResetPassword.vue';
import { useAuthStore } from '../stores/auth';

const routes = [
  { path: '/', name: 'Home', component: Home },
  { path: '/login', name: 'Login', component: Login },
  { path: '/register', name: 'Register', component: Register },
  { path: '/forgot-password', name: 'ForgotPassword', component: ForgotPassword },
  { path: '/reset-password', name: 'ResetPassword', component: ResetPassword },
  { path: '/product/:id', name: 'ProductDetails', component: ProductDetails },
  { 
    path: '/cart', 
    name: 'Cart', 
    component: Cart,
    meta: { requiresAuth: true }
  },
  { 
    path: '/checkout', 
    name: 'Checkout', 
    component: Checkout,
    meta: { requiresAuth: true }
  },
  { 
    path: '/my-orders', 
    name: 'MyOrders', 
    component: MyOrders,
    meta: { requiresAuth: true }
  },
  { 
    path: '/profile', 
    name: 'Profile', 
    component: Profile,
    meta: { requiresAuth: true }
  },
  { 
    path: '/admin', 
    name: 'AdminDashboard', 
    component: AdminDashboard,
    meta: { requiresAuth: true, isAdmin: true }
  },
  { 
    path: '/admin/category', 
    name: 'AdminCategory', 
    component: AdminCategory,
    meta: { requiresAuth: true, isAdmin: true }
  },
  { 
    path: '/admin/products', 
    name: 'AdminProducts', 
    component: AdminProducts,
    meta: { requiresAuth: true, isAdmin: true }
  },
  { 
    path: '/admin/add-product', 
    name: 'AdminAddProduct', 
    component: AdminAddProduct,
    meta: { requiresAuth: true, isAdmin: true }
  },
  { 
    path: '/admin/edit-product/:id', 
    name: 'AdminEditProduct', 
    component: AdminAddProduct,
    props: true,
    meta: { requiresAuth: true, isAdmin: true }
  },
  { 
    path: '/admin/orders', 
    name: 'AdminOrders', 
    component: AdminOrders,
    meta: { requiresAuth: true, isAdmin: true }
  },
  { 
    path: '/admin/users', 
    name: 'AdminUsers', 
    component: AdminUsers,
    meta: { requiresAuth: true, isAdmin: true }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login');
  } else {
    next();
  }
});

export default router;
