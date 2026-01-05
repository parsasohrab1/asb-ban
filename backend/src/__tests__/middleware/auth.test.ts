import { Request, Response, NextFunction } from 'express';
import { authenticate } from '../../middleware/auth';
import jwt from 'jsonwebtoken';

jest.mock('jsonwebtoken');

describe('Auth Middleware', () => {
  let mockRequest: Partial<Request>;
  let mockResponse: Partial<Response>;
  let mockNext: NextFunction;

  beforeEach(() => {
    mockRequest = {
      headers: {},
    };
    mockResponse = {
      status: jest.fn().mockReturnThis(),
      json: jest.fn(),
    };
    mockNext = jest.fn();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('should authenticate user with valid token', async () => {
    const token = 'valid_token';
    mockRequest.headers = {
      authorization: `Bearer ${token}`,
    };

    (jwt.verify as jest.Mock).mockReturnValueOnce({ id: '1', email: 'test@example.com' });

    await authenticate(
      mockRequest as Request,
      mockResponse as Response,
      mockNext
    );

    expect(mockRequest.user).toEqual({ id: '1', email: 'test@example.com' });
    expect(mockNext).toHaveBeenCalled();
  });

  it('should return 401 if no token provided', async () => {
    mockRequest.headers = {};

    await authenticate(
      mockRequest as Request,
      mockResponse as Response,
      mockNext
    );

    expect(mockResponse.status).toHaveBeenCalledWith(401);
    expect(mockResponse.json).toHaveBeenCalledWith(
      expect.objectContaining({
        success: false,
      })
    );
  });

  it('should return 401 if token is invalid', async () => {
    const token = 'invalid_token';
    mockRequest.headers = {
      authorization: `Bearer ${token}`,
    };

    (jwt.verify as jest.Mock).mockImplementationOnce(() => {
      throw new Error('Invalid token');
    });

    await authenticate(
      mockRequest as Request,
      mockResponse as Response,
      mockNext
    );

    expect(mockResponse.status).toHaveBeenCalledWith(401);
  });
});

