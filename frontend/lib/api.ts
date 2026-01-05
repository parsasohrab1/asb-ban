import axios from 'axios';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:3000/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests if available
api.interceptors.request.use((config) => {
  if (typeof window !== 'undefined') {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

// Handle response errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized - clear token and redirect to login
      if (typeof window !== 'undefined') {
        localStorage.removeItem('token');
        window.location.href = '/auth/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;

// API Functions
export const authAPI = {
  register: (data: { email: string; password: string; full_name: string; phone?: string }) =>
    api.post('/auth/register', data),
  login: (data: { email: string; password: string }) =>
    api.post('/auth/login', data),
  getProfile: () => api.get('/auth/profile'),
  updateProfile: (data: any) => api.put('/auth/profile', data),
};

export const blogAPI = {
  getPosts: (params?: { page?: number; limit?: number; category_id?: string }) =>
    api.get('/blog/posts', { params }),
  getPost: (slug: string) => api.get(`/blog/posts/${slug}`),
  searchPosts: (query: string) => api.get('/blog/posts/search', { params: { q: query } }),
  getCategories: () => api.get('/blog/categories'),
};

export const servicesAPI = {
  getVeterinarians: (params?: { region?: string; specialization?: string }) =>
    api.get('/services/veterinarians', { params }),
  getVeterinarian: (id: string) => api.get(`/services/veterinarians/${id}`),
  getTransporters: (params?: { region?: string }) =>
    api.get('/services/transporters', { params }),
  getTransporter: (id: string) => api.get(`/services/transporters/${id}`),
  createBooking: (data: any) => api.post('/services/bookings', data),
  getBookings: () => api.get('/services/bookings'),
  createReview: (data: any) => api.post('/services/reviews', data),
  getReviews: (serviceType: string, providerId: string) =>
    api.get(`/services/reviews/${serviceType}/${providerId}`),
};

export const shopAPI = {
  getProducts: (params?: { page?: number; limit?: number; category_id?: string; search?: string }) =>
    api.get('/shop/products', { params }),
  getProduct: (slug: string) => api.get(`/shop/products/${slug}`),
  getCategories: () => api.get('/shop/categories'),
  createOrder: (data: any) => api.post('/shop/orders', data),
  getOrders: () => api.get('/shop/orders'),
  getOrder: (id: string) => api.get(`/shop/orders/${id}`),
};

export const competitionsAPI = {
  getCompetitions: (params?: { type?: string; is_international?: boolean; start_date?: string; end_date?: string }) =>
    api.get('/competitions', { params }),
  getCompetition: (slug: string) => api.get(`/competitions/${slug}`),
  getResults: (id: string) => api.get(`/competitions/${id}/results`),
};

