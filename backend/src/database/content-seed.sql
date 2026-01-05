-- محتوای نمونه برای بلاگ اسب
-- این فایل شامل دسته‌بندی‌ها و مقالات نمونه است

-- دسته‌بندی‌های بلاگ
INSERT INTO blog_categories (name, slug, description) VALUES
('نژادهای اسب', 'horse-breeds', 'معرفی و بررسی نژادهای مختلف اسب در ایران و جهان'),
('بیماری‌ها و سلامت', 'health-diseases', 'بیماری‌های رایج اسب، پیشگیری و درمان'),
('تجهیزات و لوازم', 'equipment', 'معرفی تجهیزات مورد نیاز برای نگهداری و سوارکاری'),
('ورزش‌های سوارکاری', 'equestrian-sports', 'مسابقات و ورزش‌های مختلف سوارکاری'),
('تاریخ و فرهنگ', 'history-culture', 'تاریخ اسب در ایران و جهان، فرهنگ و ادبیات'),
('تغذیه و مراقبت', 'nutrition-care', 'رژیم غذایی، مکمل‌ها و مراقبت‌های روزانه'),
('آموزش و تربیت', 'training-education', 'روش‌های آموزش و تربیت اسب'),
('سوارکاری', 'riding', 'تکنیک‌ها و مهارت‌های سوارکاری')
ON CONFLICT (slug) DO NOTHING;

-- مقالات نمونه (نیاز به author_id دارد - باید بعد از ایجاد کاربر اضافه شود)
-- این مقالات به عنوان template هستند

-- نمونه مقاله 1: نژاد اسب عربی
/*
INSERT INTO blog_posts (title, slug, excerpt, content, featured_image, category_id, author_id, is_published, published_at)
VALUES (
    'اسب عربی: شاهکار طبیعت',
    'arabian-horse',
    'اسب عربی یکی از قدیمی‌ترین و زیباترین نژادهای اسب در جهان است که تاریخچه‌ای چند هزار ساله دارد.',
    'محتوا...',
    '/images/arabian-horse.jpg',
    (SELECT id FROM blog_categories WHERE slug = 'horse-breeds'),
    1,
    true,
    CURRENT_TIMESTAMP
);
*/

-- دسته‌بندی‌های محصولات
INSERT INTO product_categories (name, slug, description) VALUES
('تجهیزات سوارکاری', 'riding-equipment', 'زین، یراق، کلاه ایمنی و سایر تجهیزات سوارکاری'),
('داروهای دامپزشکی', 'veterinary-medicines', 'داروهای مورد نیاز برای درمان و پیشگیری از بیماری‌ها'),
('مکمل‌های غذایی', 'nutritional-supplements', 'ویتامین‌ها، مواد معدنی و مکمل‌های غذایی'),
('وسایل مراقبت', 'care-items', 'برس، شامپو، نعل و وسایل نگهداری'),
('خوراک و علوفه', 'feed-forage', 'خوراک آماده، یونجه، جو و سایر علوفه‌ها'),
('ابزار و تجهیزات', 'tools-equipment', 'ابزارهای مورد نیاز برای نگهداری و مراقبت')
ON CONFLICT (slug) DO NOTHING;

-- محصولات نمونه
/*
INSERT INTO products (name, slug, description, short_description, price, stock_quantity, category_id, is_active)
VALUES (
    'زین انگلیسی استاندارد',
    'english-saddle-standard',
    'زین انگلیسی با کیفیت بالا مناسب برای سوارکاری روزمره...',
    'زین انگلیسی استاندارد با چرم طبیعی',
    15000000,
    10,
    (SELECT id FROM product_categories WHERE slug = 'riding-equipment'),
    true
);
*/

