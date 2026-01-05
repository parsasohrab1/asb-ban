import { Request, Response, NextFunction } from 'express';
import { getCache, setCache } from '../database/redis';

// Performance monitoring middleware
export const performanceMonitor = (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  const startTime = Date.now();

  res.on('finish', () => {
    const duration = Date.now() - startTime;
    const route = req.route?.path || req.path;
    
    // Log slow requests (> 1 second)
    if (duration > 1000) {
      console.warn(`⚠️ Slow request: ${req.method} ${route} took ${duration}ms`);
    }

    // Log in production for monitoring
    if (process.env.NODE_ENV === 'production') {
      // Could send to monitoring service
    }
  });

  next();
};

// Response compression check
export const shouldCompress = (req: Request, res: Response) => {
  // Check if response should be compressed
  const contentType = res.getHeader('content-type') as string;
  const compressibleTypes = [
    'application/json',
    'text/html',
    'text/css',
    'application/javascript',
    'text/plain'
  ];

  return compressibleTypes.some(type => contentType?.includes(type));
};

