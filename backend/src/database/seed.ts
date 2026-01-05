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
      { name: 'نژادهای اسب', slug: 'horse-breeds', description: 'معرفی و بررسی نژادهای مختلف اسب در ایران و جهان' },
      { name: 'بیماری‌ها و سلامت', slug: 'health-diseases', description: 'بیماری‌های رایج اسب، پیشگیری و درمان' },
      { name: 'تجهیزات و لوازم', slug: 'equipment', description: 'معرفی تجهیزات مورد نیاز برای نگهداری و سوارکاری' },
      { name: 'ورزش‌های سوارکاری', slug: 'equestrian-sports', description: 'مسابقات و ورزش‌های مختلف سوارکاری' },
      { name: 'تاریخ و فرهنگ', slug: 'history-culture', description: 'تاریخ اسب در ایران و جهان، فرهنگ و ادبیات' },
      { name: 'تغذیه و مراقبت', slug: 'nutrition-care', description: 'رژیم غذایی، مکمل‌ها و مراقبت‌های روزانه' },
      { name: 'آموزش و تربیت', slug: 'training-education', description: 'روش‌های آموزش و تربیت اسب' },
      { name: 'سوارکاری', slug: 'riding', description: 'تکنیک‌ها و مهارت‌های سوارکاری' }
    ];

    for (const cat of categories) {
      await query(
        `INSERT INTO blog_categories (name, slug, description)
         VALUES ($1, $2, $3)
         ON CONFLICT (slug) DO NOTHING`,
        [cat.name, cat.slug, cat.description]
      );
    }

    console.log('✅ Blog categories created');

    // Create product categories
    const productCategories = [
      { name: 'تجهیزات سوارکاری', slug: 'riding-equipment', description: 'زین، یراق، کلاه ایمنی و سایر تجهیزات سوارکاری' },
      { name: 'داروهای دامپزشکی', slug: 'veterinary-medicines', description: 'داروهای مورد نیاز برای درمان و پیشگیری از بیماری‌ها' },
      { name: 'مکمل‌های غذایی', slug: 'nutritional-supplements', description: 'ویتامین‌ها، مواد معدنی و مکمل‌های غذایی' },
      { name: 'وسایل مراقبت', slug: 'care-items', description: 'برس، شامپو، نعل و وسایل نگهداری' },
      { name: 'خوراک و علوفه', slug: 'feed-forage', description: 'خوراک آماده، یونجه، جو و سایر علوفه‌ها' },
      { name: 'ابزار و تجهیزات', slug: 'tools-equipment', description: 'ابزارهای مورد نیاز برای نگهداری و مراقبت' }
    ];

    for (const cat of productCategories) {
      await query(
        `INSERT INTO product_categories (name, slug, description)
         VALUES ($1, $2, $3)
         ON CONFLICT (slug) DO NOTHING`,
        [cat.name, cat.slug, cat.description]
      );
    }

    console.log('✅ Product categories created');

    console.log('🎉 Database seed completed successfully!');
    
    // Import and run content seed
    const { seedContent } = await import('./content-seed-full');
    await seedContent();
    
    process.exit(0);
  } catch (error) {
    console.error('❌ Error seeding database:', error);
    process.exit(1);
  }
}

seed();

