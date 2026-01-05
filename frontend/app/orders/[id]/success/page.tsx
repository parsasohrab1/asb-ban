'use client';

import { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import Link from 'next/link';
import { shopAPI } from '@/lib/api';
import { FaCheckCircle, FaHome, FaShoppingBag } from 'react-icons/fa';

export default function OrderSuccessPage() {
  const params = useParams();
  const router = useRouter();
  const orderId = params.id as string;
  const [order, setOrder] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (orderId) {
      loadOrder();
    }
  }, [orderId]);

  const loadOrder = async () => {
    try {
      const response = await shopAPI.getOrder(orderId);
      if (response.data.success) {
        setOrder(response.data.data);
      }
    } catch (error) {
      console.error('Error loading order:', error);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex justify-center items-center min-h-screen">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600"></div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="max-w-2xl mx-auto text-center">
        <div className="bg-white rounded-lg shadow-md p-8">
          <div className="flex justify-center mb-6">
            <div className="bg-green-100 rounded-full p-4">
              <FaCheckCircle className="text-6xl text-green-600" />
            </div>
          </div>

          <h1 className="text-3xl font-bold mb-4 text-green-600">
            سفارش شما با موفقیت ثبت شد!
          </h1>

          {order && (
            <div className="mt-6 space-y-2">
              <p className="text-lg">
                <span className="font-semibold">شماره سفارش:</span>{' '}
                {order.order_number}
              </p>
              <p className="text-lg">
                <span className="font-semibold">مبلغ کل:</span>{' '}
                {parseFloat(order.total_amount).toLocaleString('fa-IR')} تومان
              </p>
              <p className="text-lg">
                <span className="font-semibold">وضعیت:</span>{' '}
                {order.status === 'pending' ? 'در انتظار پردازش' : order.status}
              </p>
            </div>
          )}

          <div className="mt-8 flex flex-col sm:flex-row gap-4 justify-center">
            <Link
              href="/profile/orders"
              className="bg-primary-600 text-white px-6 py-3 rounded-lg hover:bg-primary-700 transition flex items-center justify-center gap-2"
            >
              <FaShoppingBag />
              مشاهده سفارشات من
            </Link>
            <Link
              href="/"
              className="bg-gray-200 text-gray-700 px-6 py-3 rounded-lg hover:bg-gray-300 transition flex items-center justify-center gap-2"
            >
              <FaHome />
              بازگشت به صفحه اصلی
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}

