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
  createPost: (data: any) => api.post('/blog/posts', data),
  updatePost: (id: number, data: any) => api.put(`/blog/posts/${id}`, data),
  deletePost: (id: number) => api.delete(`/blog/posts/${id}`),
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

export const searchAPI = {
  globalSearch: (params: { q: string; type?: string; category?: string; sort?: string; page?: number; limit?: number }) =>
    api.get('/search', { params }),
};

export const notificationsAPI = {
  getNotifications: (limit?: number) => api.get('/notifications', { params: { limit } }),
  getUnreadCount: () => api.get('/notifications/unread-count'),
  markAsRead: (id: number) => api.put(`/notifications/${id}/read`),
  markAllAsRead: () => api.put('/notifications/read-all'),
  deleteNotification: (id: number) => api.delete(`/notifications/${id}`),
};

export const pushAPI = {
  getVAPIDKey: () => api.get('/push/vapid-key'),
  subscribe: (subscription: any) => api.post('/push/subscribe', { subscription }),
  unsubscribe: (endpoint: string) => api.post('/push/unsubscribe', { endpoint }),
  getSubscriptions: () => api.get('/push/subscriptions'),
  test: (data: { title: string; message: string; link?: string; type?: string }) =>
    api.post('/push/test', data),
};

export const uploadAPI = {
  uploadImage: (file: File) => {
    const formData = new FormData();
    formData.append('image', file);
    return api.post('/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  uploadImages: (files: File[]) => {
    const formData = new FormData();
    files.forEach((file) => formData.append('images', file));
    return api.post('/upload/images', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  uploadAvatar: (file: File) => {
    const formData = new FormData();
    formData.append('avatar', file);
    return api.post('/upload/avatar', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};

