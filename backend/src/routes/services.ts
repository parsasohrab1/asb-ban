import express from 'express';
import {
  getVeterinarians,
  getVeterinarian,
  registerVeterinarian,
  updateVeterinarian,
  getTransporters,
  getTransporter,
  registerTransporter,
  updateTransporter,
  createBooking,
  getBookings,
  updateBookingStatus,
  createReview,
  getReviews
} from '../controllers/serviceController';
import { authenticate } from '../middleware/auth';

const router = express.Router();

// Public routes
router.get('/veterinarians', getVeterinarians);
router.get('/veterinarians/:id', getVeterinarian);
router.get('/transporters', getTransporters);
router.get('/transporters/:id', getTransporter);
router.get('/reviews/:serviceType/:providerId', getReviews);

// Protected routes
router.post('/veterinarians', authenticate, registerVeterinarian);
router.put('/veterinarians/:id', authenticate, updateVeterinarian);
router.post('/transporters', authenticate, registerTransporter);
router.put('/transporters/:id', authenticate, updateTransporter);
router.post('/bookings', authenticate, createBooking);
router.get('/bookings', authenticate, getBookings);
router.put('/bookings/:id/status', authenticate, updateBookingStatus);
router.post('/reviews', authenticate, createReview);

export default router;

