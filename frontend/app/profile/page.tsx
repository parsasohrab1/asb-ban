'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { authAPI } from '@/lib/api';
import { FaUser, FaEnvelope, FaPhone, FaEdit, FaSave, FaTimes } from 'react-icons/fa';
import ImageUpload from '@/components/ImageUpload';

interface UserProfile {
  id: number;
  email: string;
  full_name: string;
  phone: string;
  avatar_url: string;
  role: string;
  created_at: string;
}

export default function ProfilePage() {
  const router = useRouter();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [formData, setFormData] = useState({
    full_name: '',
    phone: '',
    avatar_url: ''
  });

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        router.push('/auth/login');
        return;
      }

      const response = await authAPI.getProfile();
      if (response.data.success) {
        const userData = response.data.data;
        setProfile(userData);
        setFormData({
          full_name: userData.full_name || '',
          phone: userData.phone || '',
          avatar_url: userData.avatar_url || ''
        });
      }
    } catch (error: any) {
      if (error.response?.status === 401) {
        router.push('/auth/login');
      } else {
        setError('خطا در بارگذاری پروفایل');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    setSaving(true);
    setError('');
    setSuccess('');

    try {
      const response = await authAPI.updateProfile(formData);
      if (response.data.success) {
        setProfile(response.data.data);
        setEditing(false);
        setSuccess('پروفایل با موفقیت به‌روزرسانی شد');
        setTimeout(() => setSuccess(''), 3000);
      }
    } catch (error: any) {
      setError(error.response?.data?.message || 'خطا در به‌روزرسانی پروفایل');
    } finally {
      setSaving(false);
    }
  };

  const handleCancel = () => {
    if (profile) {
      setFormData({
        full_name: profile.full_name || '',
        phone: profile.phone || '',
        avatar_url: profile.avatar_url || ''
      });
    }
    setEditing(false);
    setError('');
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    router.push('/');
  };

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex justify-center items-center min-h-screen">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto"></div>
            <p className="mt-4 text-gray-600">در حال بارگذاری...</p>
          </div>
        </div>
      </div>
    );
  }

  if (!profile) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="text-center py-16">
          <p className="text-gray-600">خطا در بارگذاری پروفایل</p>
          <button
            onClick={() => router.push('/auth/login')}
            className="mt-4 text-primary-600 hover:text-primary-700"
          >
            ورود به حساب کاربری
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="container mx-auto px-4 py-8 max-w-2xl">
      <h1 className="text-3xl font-bold mb-8 text-center">پروفایل کاربری</h1>

      {/* Success Message */}
      {success && (
        <div className="mb-4 p-4 bg-green-100 border border-green-400 text-green-700 rounded-lg">
          {success}
        </div>
      )}

      {/* Error Message */}
      {error && (
        <div className="mb-4 p-4 bg-red-100 border border-red-400 text-red-700 rounded-lg">
          {error}
        </div>
      )}

      <div className="bg-white rounded-lg shadow-md p-6">
        {/* Avatar Section */}
        <div className="text-center mb-6">
          <div className="relative inline-block">
            {editing ? (
              <div className="w-32 h-32 rounded-full bg-gray-200 flex items-center justify-center mx-auto mb-4">
                {formData.avatar_url ? (
                  <img
                    src={formData.avatar_url}
                    alt="Avatar"
                    className="w-32 h-32 rounded-full object-cover"
                  />
                ) : (
                  <FaUser className="text-6xl text-gray-400" />
                )}
              </div>
            ) : (
              <div className="w-32 h-32 rounded-full bg-gray-200 flex items-center justify-center mx-auto mb-4">
                {profile.avatar_url ? (
                  <img
                    src={profile.avatar_url}
                    alt="Avatar"
                    className="w-32 h-32 rounded-full object-cover"
                  />
                ) : (
                  <FaUser className="text-6xl text-gray-400" />
                )}
              </div>
            )}
          </div>
          {editing && (
            <div className="mt-4">
              <ImageUpload
                onUploadComplete={(url) => setFormData({ ...formData, avatar_url: url })}
                currentImageUrl={formData.avatar_url}
                label="آواتار"
              />
            </div>
          )}
        </div>

        {/* Profile Information */}
        <div className="space-y-4">
          {/* Email (Read-only) */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              <FaEnvelope className="inline ml-2" />
              ایمیل
            </label>
            <input
              type="email"
              value={profile.email}
              disabled
              className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-100 text-gray-600"
            />
            <p className="text-xs text-gray-500 mt-1">ایمیل قابل تغییر نیست</p>
          </div>

          {/* Full Name */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              <FaUser className="inline ml-2" />
              نام کامل
            </label>
            {editing ? (
              <input
                type="text"
                value={formData.full_name}
                onChange={(e) => setFormData({ ...formData, full_name: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="نام و نام خانوادگی"
              />
            ) : (
              <div className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50">
                {profile.full_name || 'تعریف نشده'}
              </div>
            )}
          </div>

          {/* Phone */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              <FaPhone className="inline ml-2" />
              شماره تماس
            </label>
            {editing ? (
              <input
                type="tel"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-primary-500 focus:border-transparent"
                placeholder="09123456789"
              />
            ) : (
              <div className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50">
                {profile.phone || 'تعریف نشده'}
              </div>
            )}
          </div>

          {/* Role */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">نقش</label>
            <div className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50">
              {profile.role === 'admin' ? 'مدیر' : profile.role === 'author' ? 'نویسنده' : 'کاربر'}
            </div>
          </div>

          {/* Member Since */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">عضو از</label>
            <div className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-50">
              {new Date(profile.created_at).toLocaleDateString('fa-IR')}
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="mt-6 flex gap-4">
          {editing ? (
            <>
              <button
                onClick={handleSave}
                disabled={saving}
                className="flex-1 bg-primary-600 text-white px-6 py-3 rounded-lg hover:bg-primary-700 transition flex items-center justify-center gap-2 disabled:opacity-50"
              >
                <FaSave />
                {saving ? 'در حال ذخیره...' : 'ذخیره تغییرات'}
              </button>
              <button
                onClick={handleCancel}
                disabled={saving}
                className="flex-1 bg-gray-200 text-gray-700 px-6 py-3 rounded-lg hover:bg-gray-300 transition flex items-center justify-center gap-2"
              >
                <FaTimes />
                انصراف
              </button>
            </>
          ) : (
            <>
              <button
                onClick={() => setEditing(true)}
                className="flex-1 bg-primary-600 text-white px-6 py-3 rounded-lg hover:bg-primary-700 transition flex items-center justify-center gap-2"
              >
                <FaEdit />
                ویرایش پروفایل
              </button>
              <button
                onClick={handleLogout}
                className="flex-1 bg-red-600 text-white px-6 py-3 rounded-lg hover:bg-red-700 transition"
              >
                خروج
              </button>
            </>
          )}
        </div>
      </div>

      {/* Additional Sections */}
      <div className="mt-6 grid md:grid-cols-2 gap-6">
        {/* My Orders */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-xl font-bold mb-4">سفارشات من</h2>
          <p className="text-gray-600 mb-4">مشاهده و پیگیری سفارشات</p>
          <button
            onClick={() => router.push('/profile/orders')}
            className="text-primary-600 hover:text-primary-700 font-semibold"
          >
            مشاهده سفارشات →
          </button>
        </div>

        {/* My Bookings */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h2 className="text-xl font-bold mb-4">رزروهای من</h2>
          <p className="text-gray-600 mb-4">مشاهده و مدیریت رزروها</p>
          <button
            onClick={() => router.push('/profile/bookings')}
            className="text-primary-600 hover:text-primary-700 font-semibold"
          >
            مشاهده رزروها →
          </button>
        </div>
      </div>
    </div>
  );
}

