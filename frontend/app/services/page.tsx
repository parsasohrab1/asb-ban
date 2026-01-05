import Link from 'next/link';
import { FaUserMd, FaTruck, FaMapMarkerAlt } from 'react-icons/fa';

export default function ServicesPage() {
  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-4xl font-bold mb-8 text-center">خدمات اعزام</h1>

      {/* Map View Button */}
      <div className="mb-8 text-center">
        <Link
          href="/services/map"
          className="inline-flex items-center gap-2 bg-primary-600 text-white px-6 py-3 rounded-lg hover:bg-primary-700 transition shadow-lg"
        >
          <FaMapMarkerAlt />
          <span>مشاهده روی نقشه</span>
        </Link>
      </div>

      <div className="grid md:grid-cols-2 gap-8 mb-12">
        {/* Veterinarians */}
        <Link
          href="/services/veterinarians"
          className="bg-white p-8 rounded-lg shadow-md hover:shadow-lg transition text-center"
        >
          <FaUserMd className="text-6xl text-primary-600 mx-auto mb-4" />
          <h2 className="text-2xl font-bold mb-4">دامپزشکان</h2>
          <p className="text-gray-600 mb-4">
            جستجو و رزرو آنلاین دامپزشک متخصص در سراسر کشور
          </p>
          <span className="text-primary-600 font-semibold">مشاهده دامپزشکان →</span>
        </Link>

        {/* Transporters */}
        <Link
          href="/services/transporters"
          className="bg-white p-8 rounded-lg shadow-md hover:shadow-lg transition text-center"
        >
          <FaTruck className="text-6xl text-primary-600 mx-auto mb-4" />
          <h2 className="text-2xl font-bold mb-4">اسب‌کش‌ها</h2>
          <p className="text-gray-600 mb-4">
            پیدا کردن و رزرو خدمات حمل و نقل اسب با تجهیزات مناسب
          </p>
          <span className="text-primary-600 font-semibold">مشاهده اسب‌کش‌ها →</span>
        </Link>
      </div>

      <div className="bg-primary-50 p-6 rounded-lg">
        <h3 className="text-xl font-bold mb-4">رزرو خدمات</h3>
        <p className="text-gray-700 mb-4">
          برای رزرو خدمات، ابتدا وارد حساب کاربری خود شوید و سپس از طریق لیست ارائه‌دهندگان
          خدمات، مورد مناسب خود را انتخاب و رزرو کنید.
        </p>
        <Link
          href="/auth/login"
          className="inline-block bg-primary-600 text-white px-6 py-2 rounded-lg hover:bg-primary-700 transition"
        >
          ورود / ثبت‌نام
        </Link>
      </div>
    </div>
  );
}

