import express from 'express';
import {
  getCompetitions,
  getCompetition,
  createCompetition,
  updateCompetition,
  deleteCompetition,
  getCompetitionResults,
  addCompetitionResult
} from '../controllers/competitionController';
import { authenticate, authorize } from '../middleware/auth';

const router = express.Router();

// Public routes
router.get('/', getCompetitions);
router.get('/:slug', getCompetition);
router.get('/:id/results', getCompetitionResults);

// Protected routes (admin)
router.post('/', authenticate, authorize('admin'), createCompetition);
router.put('/:id', authenticate, authorize('admin'), updateCompetition);
router.delete('/:id', authenticate, authorize('admin'), deleteCompetition);
router.post('/:id/results', authenticate, authorize('admin'), addCompetitionResult);

export default router;

