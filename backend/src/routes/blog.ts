import express from 'express';
import {
  getPosts,
  getPost,
  createPost,
  updatePost,
  deletePost,
  getCategories,
  searchPosts
} from '../controllers/blogController';
import { authenticate, authorize } from '../middleware/auth';

const router = express.Router();

// Public routes
router.get('/posts', getPosts);
router.get('/posts/search', searchPosts);
router.get('/posts/:slug', getPost);
router.get('/categories', getCategories);

// Protected routes (admin/author)
router.post('/posts', authenticate, authorize('admin', 'author'), createPost);
router.put('/posts/:id', authenticate, authorize('admin', 'author'), updatePost);
router.delete('/posts/:id', authenticate, authorize('admin'), deletePost);

export default router;

