'use client';

import { FaExclamationCircle, FaTimes } from 'react-icons/fa';
import { useState } from 'react';

interface ErrorMessageProps {
  message: string;
  onDismiss?: () => void;
  dismissible?: boolean;
}

export default function ErrorMessage({
  message,
  onDismiss,
  dismissible = true,
}: ErrorMessageProps) {
  const [isVisible, setIsVisible] = useState(true);

  if (!isVisible) return null;

  const handleDismiss = () => {
    setIsVisible(false);
    if (onDismiss) {
      onDismiss();
    }
  };

  return (
    <div className="bg-red-50 border border-red-200 text-red-800 px-4 py-3 rounded-lg flex items-center justify-between">
      <div className="flex items-center gap-2">
        <FaExclamationCircle className="flex-shrink-0" />
        <p>{message}</p>
      </div>
      {dismissible && (
        <button
          onClick={handleDismiss}
          className="text-red-600 hover:text-red-800 transition ml-4"
        >
          <FaTimes />
        </button>
      )}
    </div>
  );
}

