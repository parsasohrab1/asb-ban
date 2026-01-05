import { Request, Response, NextFunction } from 'express';
import { query } from '../database/connection';
import { createError } from '../middleware/errorHandler';
import { getCache, setCache, deleteCache } from '../database/redis';

// Helper function to create slug
const createSlug = (text: string): string => {
  return text
    .toLowerCase()
    .replace(/[^\w\s-]/g, '')
    .replace(/\s+/g, '-')
    .replace(/-+/g, '-')
    .trim();
};

export const getPosts = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const page = parseInt(req.query.page as string) || 1;
    const limit = parseInt(req.query.limit as string) || 10;
    const categoryId = req.query.category_id as string;
    const offset = (page - 1) * limit;

    let queryText = `
      SELECT 
        bp.id, bp.title, bp.slug, bp.excerpt, bp.featured_image,
        bp.views_count, bp.published_at, bp.created_at,
        bc.name as category_name, bc.slug as category_slug,
        u.full_name as author_name
      FROM blog_posts bp
      LEFT JOIN blog_categories bc ON bp.category_id = bc.id
      LEFT JOIN users u ON bp.author_id = u.id
      WHERE bp.is_published = true
    `;
    const params: any[] = [];
    let paramCount = 1;

    if (categoryId) {
      queryText += ` AND bp.category_id = $${paramCount++}`;
      params.push(categoryId);
    }

    queryText += ` ORDER BY bp.published_at DESC LIMIT $${paramCount++} OFFSET $${paramCount++}`;
    params.push(limit, offset);

    const result = await query(queryText, params);
    const countResult = await query(
      'SELECT COUNT(*) FROM blog_posts WHERE is_published = true' +
      (categoryId ? ' AND category_id = $1' : ''),
      categoryId ? [categoryId] : []
    );

    res.json({
      success: true,
      data: {
        posts: result.rows,
        pagination: {
          page,
          limit,
          total: parseInt(countResult.rows[0].count),
          totalPages: Math.ceil(parseInt(countResult.rows[0].count) / limit)
        }
      }
    });
  } catch (error) {
    next(error);
  }
};

export const getPost = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { slug } = req.params;

    // Try cache first
    const cacheKey = `blog_post:${slug}`;
    const cached = await getCache(cacheKey);
    if (cached) {
      return res.json({
        success: true,
        data: JSON.parse(cached),
        cached: true
      });
    }

    const result = await query(
      `SELECT 
        bp.*,
        bc.name as category_name, bc.slug as category_slug,
        u.full_name as author_name, u.avatar_url as author_avatar
      FROM blog_posts bp
      LEFT JOIN blog_categories bc ON bp.category_id = bc.id
      LEFT JOIN users u ON bp.author_id = u.id
      WHERE bp.slug = $1 AND bp.is_published = true`,
      [slug]
    );

    if (result.rows.length === 0) {
      return next(createError('Post not found', 404));
    }

    // Increment views
    await query(
      'UPDATE blog_posts SET views_count = views_count + 1 WHERE id = $1',
      [result.rows[0].id]
    );

    // Cache for 1 hour
    await setCache(cacheKey, JSON.stringify(result.rows[0]), 3600);

    res.json({
      success: true,
      data: result.rows[0]
    });
  } catch (error) {
    next(error);
  }
};

export const createPost = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { title, excerpt, content, featured_image, category_id } = req.body;
    const authorId = (req as any).user.id;
    const slug = createSlug(title);

    // Check if slug exists
    const existing = await query('SELECT id FROM blog_posts WHERE slug = $1', [slug]);
    if (existing.rows.length > 0) {
      return next(createError('Post with this title already exists', 400));
    }

    const result = await query(
      `INSERT INTO blog_posts 
       (title, slug, excerpt, content, featured_image, category_id, author_id, is_published, published_at)
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8, CURRENT_TIMESTAMP)
       RETURNING *`,
      [title, slug, excerpt, content, featured_image, category_id, authorId, true]
    );

    // Clear cache
    await deleteCache('blog_posts:*');

    res.status(201).json({
      success: true,
      message: 'Post created successfully',
      data: result.rows[0]
    });
  } catch (error) {
    next(error);
  }
};

export const updatePost = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { id } = req.params;
    const { title, excerpt, content, featured_image, category_id, is_published } = req.body;

    const updates: string[] = [];
    const values: any[] = [];
    let paramCount = 1;

    if (title) {
      const slug = createSlug(title);
      updates.push(`title = $${paramCount++}`, `slug = $${paramCount++}`);
      values.push(title, slug);
    }
    if (excerpt !== undefined) {
      updates.push(`excerpt = $${paramCount++}`);
      values.push(excerpt);
    }
    if (content !== undefined) {
      updates.push(`content = $${paramCount++}`);
      values.push(content);
    }
    if (featured_image !== undefined) {
      updates.push(`featured_image = $${paramCount++}`);
      values.push(featured_image);
    }
    if (category_id !== undefined) {
      updates.push(`category_id = $${paramCount++}`);
      values.push(category_id);
    }
    if (is_published !== undefined) {
      updates.push(`is_published = $${paramCount++}`);
      values.push(is_published);
      if (is_published) {
        updates.push(`published_at = CURRENT_TIMESTAMP`);
      }
    }

    updates.push(`updated_at = CURRENT_TIMESTAMP`);
    values.push(id);

    const result = await query(
      `UPDATE blog_posts SET ${updates.join(', ')} WHERE id = $${paramCount} RETURNING *`,
      values
    );

    if (result.rows.length === 0) {
      return next(createError('Post not found', 404));
    }

    // Clear cache
    await deleteCache('blog_post:*');

    res.json({
      success: true,
      message: 'Post updated successfully',
      data: result.rows[0]
    });
  } catch (error) {
    next(error);
  }
};

export const deletePost = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { id } = req.params;

    const result = await query('DELETE FROM blog_posts WHERE id = $1 RETURNING id', [id]);

    if (result.rows.length === 0) {
      return next(createError('Post not found', 404));
    }

    // Clear cache
    await deleteCache('blog_post:*');

    res.json({
      success: true,
      message: 'Post deleted successfully'
    });
  } catch (error) {
    next(error);
  }
};

export const getCategories = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const result = await query(
      'SELECT * FROM blog_categories ORDER BY name'
    );

    res.json({
      success: true,
      data: result.rows
    });
  } catch (error) {
    next(error);
  }
};

export const searchPosts = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { q } = req.query;
    if (!q || typeof q !== 'string') {
      return next(createError('Search query is required', 400));
    }

    const result = await query(
      `SELECT 
        bp.id, bp.title, bp.slug, bp.excerpt, bp.featured_image,
        bp.published_at, bc.name as category_name
      FROM blog_posts bp
      LEFT JOIN blog_categories bc ON bp.category_id = bc.id
      WHERE bp.is_published = true 
        AND (bp.title ILIKE $1 OR bp.content ILIKE $1 OR bp.excerpt ILIKE $1)
      ORDER BY bp.published_at DESC
      LIMIT 20`,
      [`%${q}%`]
    );

    res.json({
      success: true,
      data: result.rows
    });
  } catch (error) {
    next(error);
  }
};

