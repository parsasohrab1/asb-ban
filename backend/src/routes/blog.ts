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

/**
 * @swagger
 * /blog/posts:
 *   get:
 *     summary: Get blog posts
 *     tags: [Blog]
 *     parameters:
 *       - in: query
 *         name: page
 *         schema:
 *           type: integer
 *           default: 1
 *       - in: query
 *         name: limit
 *         schema:
 *           type: integer
 *           default: 10
 *       - in: query
 *         name: category_id
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: List of blog posts
 */
router.get('/posts', getPosts);

/**
 * @swagger
 * /blog/posts/search:
 *   get:
 *     summary: Search blog posts
 *     tags: [Blog]
 *     parameters:
 *       - in: query
 *         name: q
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Search results
 */
router.get('/posts/search', searchPosts);

/**
 * @swagger
 * /blog/posts/{slug}:
 *   get:
 *     summary: Get a blog post by slug
 *     tags: [Blog]
 *     parameters:
 *       - in: path
 *         name: slug
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Blog post details
 *       404:
 *         description: Post not found
 */
router.get('/posts/:slug', getPost);

/**
 * @swagger
 * /blog/categories:
 *   get:
 *     summary: Get blog categories
 *     tags: [Blog]
 *     responses:
 *       200:
 *         description: List of categories
 */
router.get('/categories', getCategories);

/**
 * @swagger
 * /blog/posts:
 *   post:
 *     summary: Create a new blog post
 *     tags: [Blog]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - title
 *               - content
 *               - category_id
 *             properties:
 *               title:
 *                 type: string
 *               content:
 *                 type: string
 *               excerpt:
 *                 type: string
 *               category_id:
 *                 type: integer
 *               featured_image:
 *                 type: string
 *     responses:
 *       201:
 *         description: Post created successfully
 *       401:
 *         description: Unauthorized
 */
router.post('/posts', authenticate, authorize('admin', 'author'), createPost);

/**
 * @swagger
 * /blog/posts/{id}:
 *   put:
 *     summary: Update a blog post
 *     tags: [Blog]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: Post updated successfully
 *       401:
 *         description: Unauthorized
 */
router.put('/posts/:id', authenticate, authorize('admin', 'author'), updatePost);

/**
 * @swagger
 * /blog/posts/{id}:
 *   delete:
 *     summary: Delete a blog post
 *     tags: [Blog]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: Post deleted successfully
 *       401:
 *         description: Unauthorized
 */
router.delete('/posts/:id', authenticate, authorize('admin'), deletePost);

export default router;

