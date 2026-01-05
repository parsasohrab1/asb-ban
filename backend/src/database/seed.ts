import { query } from './connection';
import bcrypt from 'bcryptjs';

// Seed script for initial data
async function seed() {
  try {
    console.log('🌱 Starting database seed...');

    // Create admin user
    const adminPassword = await bcrypt.hash('admin123', 10);
    const adminResult = await query(
      `INSERT INTO users (email, password_hash, full_name, role)
       VALUES ($1, $2, $3, $4)
       ON CONFLICT (email) DO NOTHING
       RETURNING id`,
      ['admin@asb-ban.ir', adminPassword, 'مدیر سیستم', 'admin']
    );

    console.log('✅ Admin user created');

    // Create blog categories
    const categories = [
      { name: 'نژادهای اسب', slug: 'horse-breeds' },
      { name: 'بیماری‌ها و سلامت', slug: 'health-diseases' },
      { name: 'تجهیزات و لوازم', slug: 'equipment' },
      { name: 'ورزش‌های سوارکاری', slug: 'equestrian-sports' },
      { name: 'تاریخ و فرهنگ', slug: 'history-culture' }
    ];

    for (const cat of categories) {
      await query(
        `INSERT INTO blog_categories (name, slug)
         VALUES ($1, $2)
         ON CONFLICT (slug) DO NOTHING`,
        [cat.name, cat.slug]
      );
    }

    console.log('✅ Blog categories created');

    // Create product categories
    const productCategories = [
      { name: 'تجهیزات', slug: 'equipment' },
      { name: 'داروها', slug: 'medicines' },
      { name: 'مکمل‌های غذایی', slug: 'supplements' },
      { name: 'وسایل مراقبت', slug: 'care-items' }
    ];

    for (const cat of productCategories) {
      await query(
        `INSERT INTO product_categories (name, slug)
         VALUES ($1, $2)
         ON CONFLICT (slug) DO NOTHING`,
        [cat.name, cat.slug]
      );
    }

    console.log('✅ Product categories created');

    console.log('🎉 Database seed completed successfully!');
    process.exit(0);
  } catch (error) {
    console.error('❌ Error seeding database:', error);
    process.exit(1);
  }
}

seed();

