import { Request, Response, NextFunction } from 'express';
import { query } from '../database/connection';
import { createError } from '../middleware/errorHandler';
import { AuthRequest } from '../middleware/auth';
import { sendOrderConfirmationEmail, sendOrderStatusUpdateEmail } from '../services/emailService';
import { createNotification } from '../services/notificationService';

// Helper function to create slug
const createSlug = (text: string): string => {
  return text
    .toLowerCase()
    .replace(/[^\w\s-]/g, '')
    .replace(/\s+/g, '-')
    .replace(/-+/g, '-')
    .trim();
};

// Helper function to generate order number
const generateOrderNumber = (): string => {
  return `ORD-${Date.now()}-${Math.random().toString(36).substr(2, 9).toUpperCase()}`;
};

export const getProducts = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const page = parseInt(req.query.page as string) || 1;
    const limit = parseInt(req.query.limit as string) || 12;
    const categoryId = req.query.category_id as string;
    const search = req.query.search as string;
    const offset = (page - 1) * limit;

    let queryText = `
      SELECT 
        p.id, p.name, p.slug, p.short_description, p.price, 
        p.compare_at_price, p.images, p.stock_quantity, p.is_active,
        pc.name as category_name, pc.slug as category_slug
      FROM products p
      LEFT JOIN product_categories pc ON p.category_id = pc.id
      WHERE p.is_active = true
    `;
    const params: any[] = [];
    let paramCount = 1;

    if (categoryId) {
      queryText += ` AND p.category_id = $${paramCount++}`;
      params.push(categoryId);
    }
    if (search) {
      queryText += ` AND (p.name ILIKE $${paramCount} OR p.description ILIKE $${paramCount})`;
      params.push(`%${search}%`);
      paramCount++;
    }

    queryText += ` ORDER BY p.created_at DESC LIMIT $${paramCount++} OFFSET $${paramCount++}`;
    params.push(limit, offset);

    const result = await query(queryText, params);
    const countResult = await query(
      `SELECT COUNT(*) FROM products WHERE is_active = true${
        categoryId ? ' AND category_id = $1' : ''
      }${search ? (categoryId ? ' AND' : ' WHERE') + ' (name ILIKE $' + (categoryId ? '2' : '1') + ' OR description ILIKE $' + (categoryId ? '2' : '1') + ')' : ''}`,
      categoryId && search ? [categoryId, `%${search}%`] : categoryId ? [categoryId] : search ? [`%${search}%`] : []
    );

    res.json({
      success: true,
      data: {
        products: result.rows,
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

export const getProduct = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { slug } = req.params;

    const result = await query(
      `SELECT 
        p.*,
        pc.name as category_name, pc.slug as category_slug
      FROM products p
      LEFT JOIN product_categories pc ON p.category_id = pc.id
      WHERE p.slug = $1 AND p.is_active = true`,
      [slug]
    );

    if (result.rows.length === 0) {
      return next(createError('Product not found', 404));
    }

    res.json({
      success: true,
      data: result.rows[0]
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
      'SELECT * FROM product_categories ORDER BY name'
    );

    res.json({
      success: true,
      data: result.rows
    });
  } catch (error) {
    next(error);
  }
};

export const createOrder = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
) => {
  try {
    const { items, shipping_address, payment_method } = req.body;
    const userId = req.user!.id;

    if (!items || !Array.isArray(items) || items.length === 0) {
      return next(createError('Order items are required', 400));
    }

    // Calculate total and validate products
    let totalAmount = 0;
    const orderItems = [];

    for (const item of items) {
      const product = await query(
        'SELECT id, name, price, stock_quantity FROM products WHERE id = $1 AND is_active = true',
        [item.product_id]
      );

      if (product.rows.length === 0) {
        return next(createError(`Product ${item.product_id} not found`, 404));
      }

      if (product.rows[0].stock_quantity < item.quantity) {
        return next(createError(`Insufficient stock for ${product.rows[0].name}`, 400));
      }

      const itemTotal = parseFloat(product.rows[0].price) * item.quantity;
      totalAmount += itemTotal;

      orderItems.push({
        product_id: item.product_id,
        quantity: item.quantity,
        price: product.rows[0].price
      });
    }

    // Create order
    const orderNumber = generateOrderNumber();
    const orderResult = await query(
      `INSERT INTO orders 
       (user_id, order_number, total_amount, shipping_address, payment_method, status, payment_status)
       VALUES ($1, $2, $3, $4, $5, 'pending', 'pending')
       RETURNING *`,
      [userId, orderNumber, totalAmount, shipping_address, payment_method]
    );

    const orderId = orderResult.rows[0].id;

    // Create order items and update stock
    for (const item of orderItems) {
      await query(
        'INSERT INTO order_items (order_id, product_id, quantity, price) VALUES ($1, $2, $3, $4)',
        [orderId, item.product_id, item.quantity, item.price]
      );

      await query(
        'UPDATE products SET stock_quantity = stock_quantity - $1 WHERE id = $2',
        [item.quantity, item.product_id]
      );
    }

    const finalOrder = await query(
      `SELECT o.*, 
        json_agg(
          json_build_object(
            'id', oi.id,
            'product_id', oi.product_id,
            'quantity', oi.quantity,
            'price', oi.price,
            'product_name', p.name
          )
        ) as items
      FROM orders o
      LEFT JOIN order_items oi ON o.id = oi.order_id
      LEFT JOIN products p ON oi.product_id = p.id
      WHERE o.id = $1
      GROUP BY o.id`,
      [orderId]
    );

    // Get user info for email
    const userResult = await query(
      'SELECT email, full_name FROM users WHERE id = $1',
      [userId]
    );
    const user = userResult.rows[0];

    // Prepare order items for email
    const orderItems = finalOrder.rows[0].items.map((item: any) => ({
      name: item.product_name,
      quantity: item.quantity,
      price: parseFloat(item.price),
    }));

    // Create notification for order
    try {
      await createNotification(
        userId,
        'order',
        'سفارش جدید',
        `سفارش شما با شماره ${orderNumber} ثبت شد. مبلغ کل: ${totalAmount.toLocaleString('fa-IR')} تومان`,
        `/profile/orders/${orderId}`
      );
    } catch (notifError) {
      console.error('Error creating notification:', notifError);
    }

    // Send order confirmation email
    try {
      await sendOrderConfirmationEmail(
        user.email,
        user.full_name,
        {
          orderNumber,
          totalAmount,
          items: orderItems,
          shippingAddress: shipping_address,
        }
      );
    } catch (emailError) {
      console.error('Error sending order confirmation email:', emailError);
      // Don't fail order creation if email fails
    }

    res.status(201).json({
      success: true,
      message: 'Order created successfully',
      data: finalOrder.rows[0]
    });
  } catch (error) {
    next(error);
  }
};

export const getOrders = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
) => {
  try {
    const userId = req.user!.id;

    const result = await query(
      `SELECT * FROM orders 
       WHERE user_id = $1 
       ORDER BY created_at DESC`,
      [userId]
    );

    res.json({
      success: true,
      data: result.rows
    });
  } catch (error) {
    next(error);
  }
};

export const getOrder = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
) => {
  try {
    const { id } = req.params;
    const userId = req.user!.id;

    const orderResult = await query(
      'SELECT * FROM orders WHERE id = $1 AND user_id = $2',
      [id, userId]
    );

    if (orderResult.rows.length === 0) {
      return next(createError('Order not found', 404));
    }

    const itemsResult = await query(
      `SELECT oi.*, p.name as product_name, p.images[1] as product_image
       FROM order_items oi
       LEFT JOIN products p ON oi.product_id = p.id
       WHERE oi.order_id = $1`,
      [id]
    );

    res.json({
      success: true,
      data: {
        ...orderResult.rows[0],
        items: itemsResult.rows
      }
    });
  } catch (error) {
    next(error);
  }
};

export const updateOrderStatus = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
) => {
  try {
    const { id } = req.params;
    const { status, payment_status } = req.body;

    const updates: string[] = [];
    const values: any[] = [];
    let paramCount = 1;

    if (status) {
      if (!['pending', 'processing', 'shipped', 'delivered', 'cancelled'].includes(status)) {
        return next(createError('Invalid status', 400));
      }
      updates.push(`status = $${paramCount++}`);
      values.push(status);
    }

    if (payment_status) {
      if (!['pending', 'paid', 'failed', 'refunded'].includes(payment_status)) {
        return next(createError('Invalid payment status', 400));
      }
      updates.push(`payment_status = $${paramCount++}`);
      values.push(payment_status);
    }

    if (updates.length === 0) {
      return next(createError('No fields to update', 400));
    }

    updates.push(`updated_at = CURRENT_TIMESTAMP`);
    values.push(id);

    const result = await query(
      `UPDATE orders SET ${updates.join(', ')} WHERE id = $${paramCount} RETURNING *`,
      values
    );

    if (result.rows.length === 0) {
      return next(createError('Order not found', 404));
    }

    const order = result.rows[0];

    // Send status update email if status changed
    if (status) {
      try {
        const userResult = await query(
          'SELECT email, full_name FROM users WHERE id = $1',
          [order.user_id]
        );
        if (userResult.rows.length > 0) {
          const user = userResult.rows[0];
          await sendOrderStatusUpdateEmail(
            user.email,
            user.full_name,
            order.order_number,
            status
          );
        }
      } catch (emailError) {
        console.error('Error sending order status update email:', emailError);
        // Don't fail status update if email fails
      }
    }

    res.json({
      success: true,
      message: 'Order updated successfully',
      data: result.rows[0]
    });
  } catch (error) {
    next(error);
  }
};

