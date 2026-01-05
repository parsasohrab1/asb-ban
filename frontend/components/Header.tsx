'use client';

import Link from 'next/link';
import { useState } from 'react';
import { FaHorse, FaBars, FaTimes } from 'react-icons/fa';

export default function Header() {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <header className="bg-white shadow-md sticky top-0 z-50">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link href="/" className="flex items-center gap-2 text-primary-600">
            <FaHorse className="text-2xl" />
            <span className="text-xl font-bold">اسب بان</span>
          </Link>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex items-center gap-6">
            <Link href="/blog" className="hover:text-primary-600 transition">
              مقالات
            </Link>
            <Link href="/services" className="hover:text-primary-600 transition">
              خدمات
            </Link>
            <Link href="/shop" className="hover:text-primary-600 transition">
              فروشگاه
            </Link>
            <Link href="/competitions" className="hover:text-primary-600 transition">
              مسابقات
            </Link>
            <Link
              href="/auth/login"
              className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition"
            >
              ورود / ثبت‌نام
            </Link>
          </nav>

          {/* Mobile Menu Button */}
          <button
            className="md:hidden text-gray-700"
            onClick={() => setIsMenuOpen(!isMenuOpen)}
          >
            {isMenuOpen ? <FaTimes size={24} /> : <FaBars size={24} />}
          </button>
        </div>

        {/* Mobile Navigation */}
        {isMenuOpen && (
          <nav className="md:hidden py-4 border-t">
            <div className="flex flex-col gap-4">
              <Link
                href="/blog"
                className="hover:text-primary-600 transition"
                onClick={() => setIsMenuOpen(false)}
              >
                مقالات
              </Link>
              <Link
                href="/services"
                className="hover:text-primary-600 transition"
                onClick={() => setIsMenuOpen(false)}
              >
                خدمات
              </Link>
              <Link
                href="/shop"
                className="hover:text-primary-600 transition"
                onClick={() => setIsMenuOpen(false)}
              >
                فروشگاه
              </Link>
              <Link
                href="/competitions"
                className="hover:text-primary-600 transition"
                onClick={() => setIsMenuOpen(false)}
              >
                مسابقات
              </Link>
              <Link
                href="/auth/login"
                className="bg-primary-600 text-white px-4 py-2 rounded-lg hover:bg-primary-700 transition text-center"
                onClick={() => setIsMenuOpen(false)}
              >
                ورود / ثبت‌نام
              </Link>
            </div>
          </nav>
        )}
      </div>
    </header>
  );
}

