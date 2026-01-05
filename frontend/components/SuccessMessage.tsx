'use client';

import { FaCheckCircle, FaTimes } from 'react-icons/fa';
import { useState, useEffect } from 'react';

interface SuccessMessageProps {
  message: string;
  onDismiss?: () => void;
  autoHide?: boolean;
  autoHideDelay?: number;
}

export default function SuccessMessage({
  message,
  onDismiss,
  autoHide = true,
  autoHideDelay = 3000,
}: SuccessMessageProps) {
  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    if (autoHide && isVisible) {
      const timer = setTimeout(() => {
        setIsVisible(false);
        if (onDismiss) {
          onDismiss();
        }
      }, autoHideDelay);

      return () => clearTimeout(timer);
    }
  }, [autoHide, autoHideDelay, isVisible, onDismiss]);

  if (!isVisible) return null;

  const handleDismiss = () => {
    setIsVisible(false);
    if (onDismiss) {
      onDismiss();
    }
  };

  return (
    <div className="bg-green-50 border border-green-200 text-green-800 px-4 py-3 rounded-lg flex items-center justify-between">
      <div className="flex items-center gap-2">
        <FaCheckCircle className="flex-shrink-0" />
        <p>{message}</p>
      </div>
      <button
        onClick={handleDismiss}
        className="text-green-600 hover:text-green-800 transition ml-4"
      >
        <FaTimes />
      </button>
    </div>
  );
}

