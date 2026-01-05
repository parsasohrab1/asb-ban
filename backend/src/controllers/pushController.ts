import { Request, Response, NextFunction } from 'express';
import { AuthRequest } from '../middleware/auth';
import { createError } from '../middleware/errorHandler';
import {
  savePushSubscription,
  getUserPushSubscriptions,
  deletePushSubscription,
  getVAPIDPublicKey,
  sendPushNotificationToUser,
} from '../services/pushService';
import { createNotification } from '../services/notificationService';

// Get VAPID public key
export const getVAPIDKey = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const publicKey = getVAPIDPublicKey();
    if (!publicKey) {
      return next(createError('Push notifications not configured', 503));
    }

    res.json({
      success: true,
      data: {
        publicKey,
      },
    });
  } catch (error) {
    next(error);
  }
};

// Subscribe to push notifications
export const subscribeToPush = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
) => {
  try {
    const { subscription } = req.body;
    const userId = req.user!.id;

    if (!subscription || !subscription.endpoint || !subscription.keys) {
      return next(createError('Invalid subscription object', 400));
    }

    const userAgent = req.headers['user-agent'];

    await savePushSubscription(userId, subscription, userAgent);

    res.json({
      success: true,
      message: 'Push subscription saved successfully',
    });
  } catch (error) {
    next(error);
  }
};

// Unsubscribe from push notifications
export const unsubscribeFromPush = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
) => {
  try {
    const { endpoint } = req.body;

    if (!endpoint) {
      return next(createError('Endpoint is required', 400));
    }

    await deletePushSubscription(endpoint);

    res.json({
      success: true,
      message: 'Push subscription removed successfully',
    });
  } catch (error) {
    next(error);
  }
};

// Get user push subscriptions
export const getUserSubscriptions = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
) => {
  try {
    const userId = req.user!.id;
    const subscriptions = await getUserPushSubscriptions(userId);

    res.json({
      success: true,
      data: subscriptions,
    });
  } catch (error) {
    next(error);
  }
};

// Test push notification (for admin/testing)
export const testPushNotification = async (
  req: AuthRequest,
  res: Response,
  next: NextFunction
) => {
  try {
    const userId = req.user!.id;
    const { title, message, link, type } = req.body;

    if (!title || !message) {
      return next(createError('Title and message are required', 400));
    }

    // Create in-app notification
    await createNotification(
      userId,
      (type as any) || 'system',
      title,
      message,
      link
    );

    // Send push notification
    const successCount = await sendPushNotificationToUser(userId, {
      title,
      message,
      link,
      type: type || 'system',
    });

    res.json({
      success: true,
      message: `Push notification sent to ${successCount} device(s)`,
      data: {
        sentCount: successCount,
      },
    });
  } catch (error) {
    next(error);
  }
};

