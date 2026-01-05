import { Request, Response, NextFunction } from 'express';
import { query } from '../database/connection';
import { createError } from '../middleware/errorHandler';

// Helper function to create slug
const createSlug = (text: string): string => {
  return text
    .toLowerCase()
    .replace(/[^\w\s-]/g, '')
    .replace(/\s+/g, '-')
    .replace(/-+/g, '-')
    .trim();
};

export const getCompetitions = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { type, is_international, start_date, end_date } = req.query;

    let queryText = `
      SELECT id, title, slug, competition_type, location, 
             start_date, end_date, registration_deadline, 
             prize_info, image_url, is_international, is_published
      FROM competitions
      WHERE is_published = true
    `;
    const params: any[] = [];
    let paramCount = 1;

    if (type) {
      queryText += ` AND competition_type = $${paramCount++}`;
      params.push(type);
    }
    if (is_international !== undefined) {
      queryText += ` AND is_international = $${paramCount++}`;
      params.push(is_international === 'true');
    }
    if (start_date) {
      queryText += ` AND start_date >= $${paramCount++}`;
      params.push(start_date);
    }
    if (end_date) {
      queryText += ` AND end_date <= $${paramCount++}`;
      params.push(end_date);
    }

    queryText += ' ORDER BY start_date ASC';

    const result = await query(queryText, params);

    res.json({
      success: true,
      data: result.rows
    });
  } catch (error) {
    next(error);
  }
};

export const getCompetition = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { slug } = req.params;

    const result = await query(
      'SELECT * FROM competitions WHERE slug = $1 AND is_published = true',
      [slug]
    );

    if (result.rows.length === 0) {
      return next(createError('Competition not found', 404));
    }

    res.json({
      success: true,
      data: result.rows[0]
    });
  } catch (error) {
    next(error);
  }
};

export const createCompetition = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const {
      title,
      description,
      competition_type,
      location,
      start_date,
      end_date,
      registration_deadline,
      prize_info,
      conditions,
      image_url,
      is_international
    } = req.body;

    const slug = createSlug(title);

    // Check if slug exists
    const existing = await query('SELECT id FROM competitions WHERE slug = $1', [slug]);
    if (existing.rows.length > 0) {
      return next(createError('Competition with this title already exists', 400));
    }

    const result = await query(
      `INSERT INTO competitions 
       (title, slug, description, competition_type, location, start_date, end_date,
        registration_deadline, prize_info, conditions, image_url, is_international, is_published)
       VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, true)
       RETURNING *`,
      [
        title, slug, description, competition_type, location,
        start_date, end_date, registration_deadline, prize_info,
        conditions, image_url, is_international || false
      ]
    );

    res.status(201).json({
      success: true,
      message: 'Competition created successfully',
      data: result.rows[0]
    });
  } catch (error) {
    next(error);
  }
};

export const updateCompetition = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { id } = req.params;
    const {
      title,
      description,
      competition_type,
      location,
      start_date,
      end_date,
      registration_deadline,
      prize_info,
      conditions,
      image_url,
      is_international,
      is_published
    } = req.body;

    const updates: string[] = [];
    const values: any[] = [];
    let paramCount = 1;

    if (title) {
      const slug = createSlug(title);
      updates.push(`title = $${paramCount++}`, `slug = $${paramCount++}`);
      values.push(title, slug);
    }
    if (description !== undefined) { updates.push(`description = $${paramCount++}`); values.push(description); }
    if (competition_type) { updates.push(`competition_type = $${paramCount++}`); values.push(competition_type); }
    if (location) { updates.push(`location = $${paramCount++}`); values.push(location); }
    if (start_date) { updates.push(`start_date = $${paramCount++}`); values.push(start_date); }
    if (end_date) { updates.push(`end_date = $${paramCount++}`); values.push(end_date); }
    if (registration_deadline) { updates.push(`registration_deadline = $${paramCount++}`); values.push(registration_deadline); }
    if (prize_info) { updates.push(`prize_info = $${paramCount++}`); values.push(prize_info); }
    if (conditions) { updates.push(`conditions = $${paramCount++}`); values.push(conditions); }
    if (image_url) { updates.push(`image_url = $${paramCount++}`); values.push(image_url); }
    if (is_international !== undefined) { updates.push(`is_international = $${paramCount++}`); values.push(is_international); }
    if (is_published !== undefined) { updates.push(`is_published = $${paramCount++}`); values.push(is_published); }

    updates.push(`updated_at = CURRENT_TIMESTAMP`);
    values.push(id);

    const result = await query(
      `UPDATE competitions SET ${updates.join(', ')} WHERE id = $${paramCount} RETURNING *`,
      values
    );

    if (result.rows.length === 0) {
      return next(createError('Competition not found', 404));
    }

    res.json({
      success: true,
      message: 'Competition updated successfully',
      data: result.rows[0]
    });
  } catch (error) {
    next(error);
  }
};

export const deleteCompetition = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { id } = req.params;

    const result = await query('DELETE FROM competitions WHERE id = $1 RETURNING id', [id]);

    if (result.rows.length === 0) {
      return next(createError('Competition not found', 404));
    }

    res.json({
      success: true,
      message: 'Competition deleted successfully'
    });
  } catch (error) {
    next(error);
  }
};

export const getCompetitionResults = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { id } = req.params;

    const result = await query(
      'SELECT * FROM competition_results WHERE competition_id = $1 ORDER BY position ASC',
      [id]
    );

    res.json({
      success: true,
      data: result.rows
    });
  } catch (error) {
    next(error);
  }
};

export const addCompetitionResult = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const { id } = req.params;
    const { position, participant_name, horse_name, score, notes } = req.body;

    const result = await query(
      `INSERT INTO competition_results 
       (competition_id, position, participant_name, horse_name, score, notes)
       VALUES ($1, $2, $3, $4, $5, $6)
       RETURNING *`,
      [id, position, participant_name, horse_name, score, notes]
    );

    res.status(201).json({
      success: true,
      message: 'Result added successfully',
      data: result.rows[0]
    });
  } catch (error) {
    next(error);
  }
};

