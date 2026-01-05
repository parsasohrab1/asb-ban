import express from 'express';
import {
  getVAPIDKey,
  subscribeToPush,
  unsubscribeFromPush,
  getUserSubscriptions,
  testPushNotification,
} from '../controllers/pushController';
import { authenticate } from '../middleware/auth';

const router = express.Router();

// Public route - Get VAPID public key
router.get('/vapid-key', getVAPIDKey);

// Protected routes
router.post('/subscribe', authenticate, subscribeToPush);
router.post('/unsubscribe', authenticate, unsubscribeFromPush);
router.get('/subscriptions', authenticate, getUserSubscriptions);
router.post('/test', authenticate, testPushNotification);

export default router;

