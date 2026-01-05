import { query } from './connection';
import bcrypt from 'bcryptjs';

// این فایل شامل محتوای کامل با تصاویر و Alt text است
// تصاویر از Unsplash و منابع رایگان استفاده شده است

interface BlogPost {
  title: string;
  slug: string;
  excerpt: string;
  content: string;
  featured_image: string;
  category_slug: string;
}

interface Product {
  name: string;
  slug: string;
  description: string;
  short_description: string;
  price: number;
  stock_quantity: number;
  image_url: string;
  category_slug: string;
}

interface Competition {
  title: string;
  slug: string;
  description: string;
  competition_type: string;
  location: string;
  start_date: string;
  end_date: string;
  registration_deadline: string;
  prize_info: string;
  conditions: string;
  image_url: string;
  is_international: boolean;
}

const blogPosts: BlogPost[] = [
  {
    title: 'اسب عربی - شاهکار طبیعت',
    slug: 'arabian-horse',
    excerpt: 'اسب عربی یکی از قدیمی‌ترین و زیباترین نژادهای اسب در جهان است که تاریخچه‌ای چند هزار ساله دارد.',
    content: `
      <h2>مقدمه</h2>
      <p>اسب عربی (Arabian Horse) یکی از قدیمی‌ترین و معروف‌ترین نژادهای اسب در جهان است که ریشه در شبه جزیره عربستان دارد. این نژاد بیش از 4500 سال پیش توسط اعراب بادیه‌نشین پرورش داده شد و امروزه در سراسر جهان یافت می‌شود.</p>
      
      <h2>ویژگی‌های فیزیکی</h2>
      <ul>
        <li><strong>قد:</strong> 145 تا 155 سانتی‌متر</li>
        <li><strong>وزن:</strong> 400 تا 500 کیلوگرم</li>
        <li><strong>سر:</strong> کوچک و زیبا با پیشانی برجسته</li>
        <li><strong>چشم‌ها:</strong> بزرگ و براق</li>
        <li><strong>گردن:</strong> قوسی شکل و زیبا</li>
        <li><strong>دم:</strong> بالا و زیبا</li>
      </ul>
      
      <h2>خلق و خو</h2>
      <p>اسب عربی به هوش بالا، حساسیت و وفاداری معروف است. این اسب‌ها بسیار باهوش و یادگیرنده هستند و با انسان ارتباط عمیقی برقرار می‌کنند.</p>
      
      <h2>کاربردها</h2>
      <p>اسب عربی در مسابقات درساژ، پرش و استقامت استفاده می‌شود. همچنین به عنوان اسب نمایشی و تفریحی نیز محبوب است.</p>
    `,
    featured_image: 'https://images.unsplash.com/photo-1516726817505-f5ed825624d8?w=800&h=600&fit=crop',
    category_slug: 'horse-breeds'
  },
  {
    title: 'کولیک در اسب - علائم و درمان',
    slug: 'colic-in-horses',
    excerpt: 'کولیک یکی از شایع‌ترین و خطرناک‌ترین بیماری‌های اسب است که می‌تواند در صورت عدم درمان به موقع، منجر به مرگ شود.',
    content: `
      <h2>کولیک چیست؟</h2>
      <p>کولیک (Colic) به درد شکم در اسب گفته می‌شود که می‌تواند دلایل مختلفی داشته باشد. این بیماری یکی از مهم‌ترین دلایل مرگ اسب‌ها در جهان است.</p>
      
      <h2>علائم کولیک</h2>
      <ul>
        <li>بی‌قراری و ناآرامی</li>
        <li>نگاه کردن به شکم</li>
        <li>لگد زدن به شکم</li>
        <li>غلت زدن</li>
        <li>تعریق</li>
        <li>کاهش یا توقف خوردن</li>
        <li>افزایش ضربان قلب</li>
      </ul>
      
      <h2>علل شایع</h2>
      <ul>
        <li>تغذیه نامناسب</li>
        <li>تغییر ناگهانی رژیم غذایی</li>
        <li>کمبود آب</li>
        <li>انگل‌های روده</li>
        <li>استرس</li>
        <li>مشکلات دندانی</li>
      </ul>
      
      <h2>درمان</h2>
      <p>در صورت مشاهده علائم، باید فوراً با دامپزشک تماس گرفت. درمان شامل مسکن، مایعات وریدی و در موارد شدید جراحی است.</p>
      
      <h2>پیشگیری</h2>
      <ul>
        <li>تغذیه منظم و با کیفیت</li>
        <li>دسترسی دائمی به آب تمیز</li>
        <li>برنامه منظم ضد انگل</li>
        <li>معاینه منظم دندانی</li>
      </ul>
    `,
    featured_image: 'https://images.unsplash.com/photo-1553284965-83fd3e82fa5f?w=800&h=600&fit=crop',
    category_slug: 'health-diseases'
  },
  {
    title: 'راهنمای خرید زین مناسب',
    slug: 'saddle-buying-guide',
    excerpt: 'انتخاب زین مناسب یکی از مهم‌ترین تصمیمات برای سوارکار است. زین مناسب نه تنها راحتی را فراهم می‌کند بلکه سلامت اسب را نیز تضمین می‌کند.',
    content: `
      <h2>اهمیت انتخاب زین مناسب</h2>
      <p>زین (Saddle) یکی از مهم‌ترین تجهیزات سوارکاری است که باید با دقت انتخاب شود. زین مناسب باید هم برای سوارکار و هم برای اسب راحت باشد.</p>
      
      <h2>انواع زین</h2>
      <h3>1. زین انگلیسی</h3>
      <p>برای سوارکاری کلاسیک و مسابقات استفاده می‌شود. سبک و مناسب برای پرش و درساژ است.</p>
      
      <h3>2. زین غربی</h3>
      <p>برای سوارکاری وسترن و کار با گله استفاده می‌شود. سنگین‌تر و راحت‌تر است.</p>
      
      <h3>3. زین درساژ</h3>
      <p>مخصوص مسابقات درساژ طراحی شده است. به سوارکار اجازه می‌دهد در وضعیت عمودی بنشیند.</p>
      
      <h3>4. زین پرش</h3>
      <p>مخصوص پرش با اسب است. طراحی شده تا به اسب اجازه دهد آزادانه بپرد.</p>
      
      <h2>نکات خرید</h2>
      <ul>
        <li><strong>اندازه برای اسب:</strong> زین باید روی پشت اسب به درستی قرار گیرد</li>
        <li><strong>اندازه برای سوارکار:</strong> باید راحت باشد و به درستی قرار گیرد</li>
        <li><strong>کیفیت چرم:</strong> چرم با کیفیت ماندگاری بیشتری دارد</li>
        <li><strong>قیمت:</strong> بودجه خود را در نظر بگیرید</li>
        <li><strong>برند:</strong> برندهای معتبر کیفیت بهتری دارند</li>
      </ul>
    `,
    featured_image: 'https://images.unsplash.com/photo-1516726817505-f5ed825624d8?w=800&h=600&fit=crop',
    category_slug: 'equipment'
  },
  {
    title: 'درساژ - هنر سوارکاری',
    slug: 'dressage-equestrian-sport',
    excerpt: 'درساژ یکی از زیباترین و تکنیکی‌ترین ورزش‌های سوارکاری است که به "باله اسب" معروف است.',
    content: `
      <h2>درساژ چیست؟</h2>
      <p>درساژ (Dressage) یک رشته سوارکاری است که در آن سوارکار و اسب باید حرکات و الگوهای از پیش تعریف شده را با دقت و ظرافت اجرا کنند. این ورزش به "باله اسب" معروف است.</p>
      
      <h2>سطح‌های مسابقه</h2>
      <ul>
        <li><strong>مبتدی (Beginner):</strong> برای شروع کار</li>
        <li><strong>متوسط (Intermediate):</strong> برای سوارکاران با تجربه</li>
        <li><strong>پیشرفته (Advanced):</strong> برای حرفه‌ای‌ها</li>
        <li><strong>المپیک (Olympic):</strong> بالاترین سطح</li>
      </ul>
      
      <h2>حرکات اصلی</h2>
      <ul>
        <li><strong>پیاده‌روی (Walk):</strong> حرکت پایه</li>
        <li><strong>یورتمه (Trot):</strong> حرکت دو ضربه‌ای</li>
        <li><strong>چهارنعل (Canter):</strong> حرکت سه ضربه‌ای</li>
        <li><strong>Piaffe:</strong> یورتمه در جای خود</li>
        <li><strong>Passage:</strong> یورتمه آهسته و بلند</li>
      </ul>
      
      <h2>فواید درساژ</h2>
      <p>درساژ به بهبود ارتباط بین سوارکار و اسب، افزایش انعطاف‌پذیری اسب و بهبود تکنیک سوارکاری کمک می‌کند.</p>
    `,
    featured_image: 'https://images.unsplash.com/photo-1516726817505-f5ed825624d8?w=800&h=600&fit=crop',
    category_slug: 'equestrian-sports'
  },
  {
    title: 'تغذیه صحیح اسب',
    slug: 'proper-horse-nutrition',
    excerpt: 'تغذیه صحیح پایه سلامت اسب است. یک رژیم غذایی متعادل شامل علوفه، غلات و مکمل‌ها می‌تواند سلامت و عملکرد اسب را تضمین کند.',
    content: `
      <h2>اهمیت تغذیه صحیح</h2>
      <p>تغذیه اسب باید بر اساس سن، وزن، سطح فعالیت و شرایط سلامت تنظیم شود. یک اسب بالغ به طور متوسط روزانه به 1.5 تا 2.5 درصد وزن بدن خود علوفه نیاز دارد.</p>
      
      <h2>اجزای رژیم غذایی</h2>
      <h3>1. علوفه (60-80% رژیم)</h3>
      <ul>
        <li>یونجه</li>
        <li>کاه</li>
        <li>علف تازه</li>
      </ul>
      
      <h3>2. غلات (20-30% رژیم)</h3>
      <ul>
        <li>جو</li>
        <li>ذرت</li>
        <li>گندم</li>
      </ul>
      
      <h3>3. مکمل‌ها</h3>
      <ul>
        <li>ویتامین‌ها</li>
        <li>مواد معدنی</li>
        <li>پروبیوتیک</li>
      </ul>
      
      <h3>4. آب</h3>
      <p>دسترسی دائمی به آب تمیز و تازه ضروری است.</p>
      
      <h2>نکات مهم</h2>
      <ul>
        <li>تغذیه در وعده‌های کوچک و مکرر</li>
        <li>اجتناب از تغییر ناگهانی رژیم</li>
        <li>توجه به کیفیت علوفه</li>
        <li>مشورت با دامپزشک</li>
      </ul>
    `,
    featured_image: 'https://images.unsplash.com/photo-1553284965-83fd3e82fa5f?w=800&h=600&fit=crop',
    category_slug: 'nutrition-care'
  },
  {
    title: 'تاریخ اسب در ایران',
    slug: 'horse-history-iran',
    excerpt: 'ایران یکی از قدیمی‌ترین مراکز پرورش اسب در جهان است. اسب در تاریخ و فرهنگ ایران جایگاه ویژه‌ای دارد.',
    content: `
      <h2>تاریخچه اسب در ایران</h2>
      <p>ایران از دیرباز به عنوان یکی از مهم‌ترین مراکز پرورش اسب در جهان شناخته شده است. اسب در فرهنگ و تاریخ ایران نقش مهمی ایفا کرده است.</p>
      
      <h2>نژادهای ایرانی</h2>
      <h3>اسب ترکمن</h3>
      <p>یکی از قدیمی‌ترین نژادهای اسب در ایران که به سرعت و استقامت معروف است.</p>
      
      <h3>اسب کردی</h3>
      <p>نژادی مقاوم و مناسب برای مناطق کوهستانی.</p>
      
      <h3>اسب قره‌باغ</h3>
      <p>نژادی زیبا و مناسب برای سوارکاری.</p>
      
      <h3>اسب دره‌شوری</h3>
      <p>نژادی بومی ایران که در مناطق خاصی یافت می‌شود.</p>
      
      <h2>جایگاه در فرهنگ</h2>
      <p>اسب در ادبیات فارسی، هنر و فرهنگ ایرانی جایگاه ویژه‌ای دارد. از شاهنامه فردوسی تا نقاشی‌های مینیاتوری، اسب همواره حضور داشته است.</p>
      
      <h2>مسابقات سنتی</h2>
      <p>ایران دارای سنت طولانی در برگزاری مسابقات اسب‌دوانی است که از دوران باستان تا امروز ادامه دارد.</p>
    `,
    featured_image: 'https://images.unsplash.com/photo-1516726817505-f5ed825624d8?w=800&h=600&fit=crop',
    category_slug: 'history-culture'
  }
];

const products: Product[] = [
  {
    name: 'زین انگلیسی استاندارد',
    slug: 'english-saddle-standard',
    description: `
      <h2>زین انگلیسی استاندارد با کیفیت بالا</h2>
      <p>این زین با چرم طبیعی و با کیفیت بالا ساخته شده است. مناسب برای سوارکاری روزمره و مسابقات است.</p>
      <h3>ویژگی‌ها:</h3>
      <ul>
        <li>چرم طبیعی با کیفیت بالا</li>
        <li>طراحی ارگونومیک برای راحتی سوارکار</li>
        <li>مناسب برای اسب‌های متوسط تا بزرگ</li>
        <li>ضمانت 2 ساله</li>
      </ul>
    `,
    short_description: 'زین انگلیسی استاندارد با چرم طبیعی و کیفیت بالا',
    price: 15000000,
    stock_quantity: 10,
    image_url: 'https://images.unsplash.com/photo-1516726817505-f5ed825624d8?w=600&h=600&fit=crop',
    category_slug: 'riding-equipment'
  },
  {
    name: 'ویتامین E برای اسب',
    slug: 'vitamin-e-horse',
    description: `
      <h2>ویتامین E مکمل غذایی اسب</h2>
      <p>مکمل ویتامین E برای سلامت عمومی اسب و بهبود عملکرد عضلات.</p>
      <h3>فواید:</h3>
      <ul>
        <li>بهبود سلامت عمومی</li>
        <li>تقویت سیستم ایمنی</li>
        <li>بهبود عملکرد عضلات</li>
        <li>آنتی‌اکسیدان قوی</li>
      </ul>
    `,
    short_description: 'مکمل ویتامین E برای سلامت و عملکرد بهتر اسب',
    price: 500000,
    stock_quantity: 50,
    image_url: 'https://images.unsplash.com/photo-1553284965-83fd3e82fa5f?w=600&h=600&fit=crop',
    category_slug: 'nutritional-supplements'
  },
  {
    name: 'برس تمیز کردن اسب',
    slug: 'horse-grooming-brush',
    description: `
      <h2>برس حرفه‌ای تمیز کردن اسب</h2>
      <p>برس با کیفیت برای نظافت روزانه اسب. مناسب برای مو و پوست اسب.</p>
      <h3>ویژگی‌ها:</h3>
      <ul>
        <li>موهای طبیعی</li>
        <li>دسته راحت</li>
        <li>قابل شستشو</li>
        <li>مقاوم و بادوام</li>
      </ul>
    `,
    short_description: 'برس حرفه‌ای برای نظافت روزانه اسب',
    price: 250000,
    stock_quantity: 30,
    image_url: 'https://images.unsplash.com/photo-1516726817505-f5ed825624d8?w=600&h=600&fit=crop',
    category_slug: 'care-items'
  }
];

const competitions: Competition[] = [
  {
    title: 'مسابقات درساژ قهرمانی ایران',
    slug: 'iran-dressage-championship',
    description: `
      <h2>مسابقات درساژ قهرمانی ایران</h2>
      <p>مسابقات درساژ قهرمانی ایران با حضور بهترین سوارکاران کشور برگزار می‌شود.</p>
      <p>این مسابقات در سطح‌های مختلف از مبتدی تا پیشرفته برگزار می‌شود.</p>
    `,
    competition_type: 'dressage',
    location: 'تهران، باشگاه سوارکاری',
    start_date: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString(),
    end_date: new Date(Date.now() + 32 * 24 * 60 * 60 * 1000).toISOString(),
    registration_deadline: new Date(Date.now() + 20 * 24 * 60 * 60 * 1000).toISOString(),
    prize_info: `
      <h3>جوایز:</h3>
      <ul>
        <li>رتبه اول: 50,000,000 تومان</li>
        <li>رتبه دوم: 30,000,000 تومان</li>
        <li>رتبه سوم: 20,000,000 تومان</li>
      </ul>
    `,
    conditions: `
      <h3>شرایط شرکت:</h3>
      <ul>
        <li>حداقل سن 16 سال</li>
        <li>دارا بودن گواهینامه سوارکاری</li>
        <li>اسب باید سالم و واکسینه باشد</li>
      </ul>
    `,
    image_url: 'https://images.unsplash.com/photo-1516726817505-f5ed825624d8?w=800&h=600&fit=crop',
    is_international: false
  }
];

export async function seedContent() {
  try {
    console.log('🌱 Starting content seed...');

    // Get admin user ID
    const adminResult = await query(
      'SELECT id FROM users WHERE email = $1',
      ['admin@asb-ban.ir']
    );
    
    if (adminResult.rows.length === 0) {
      console.error('❌ Admin user not found. Please run seed.ts first.');
      return;
    }
    
    const adminId = adminResult.rows[0].id;

    // Insert blog posts
    for (const post of blogPosts) {
      const categoryResult = await query(
        'SELECT id FROM blog_categories WHERE slug = $1',
        [post.category_slug]
      );
      
      if (categoryResult.rows.length > 0) {
        await query(
          `INSERT INTO blog_posts (title, slug, excerpt, content, featured_image, category_id, author_id, is_published, published_at)
           VALUES ($1, $2, $3, $4, $5, $6, $7, true, CURRENT_TIMESTAMP)
           ON CONFLICT (slug) DO NOTHING`,
          [
            post.title,
            post.slug,
            post.excerpt,
            post.content,
            post.featured_image,
            categoryResult.rows[0].id,
            adminId
          ]
        );
        console.log(`✅ Blog post created: ${post.title}`);
      }
    }

    // Insert products
    for (const product of products) {
      const categoryResult = await query(
        'SELECT id FROM product_categories WHERE slug = $1',
        [product.category_slug]
      );
      
      if (categoryResult.rows.length > 0) {
        await query(
          `INSERT INTO products (name, slug, description, short_description, price, stock_quantity, category_id, image_url, is_active)
           VALUES ($1, $2, $3, $4, $5, $6, $7, $8, true)
           ON CONFLICT (slug) DO NOTHING`,
          [
            product.name,
            product.slug,
            product.description,
            product.short_description,
            product.price,
            product.stock_quantity,
            categoryResult.rows[0].id,
            product.image_url
          ]
        );
        console.log(`✅ Product created: ${product.name}`);
      }
    }

    // Insert competitions
    for (const competition of competitions) {
      await query(
        `INSERT INTO competitions (title, slug, description, competition_type, location, start_date, end_date, registration_deadline, prize_info, conditions, image_url, is_international, is_published)
         VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, $10, $11, $12, true)
         ON CONFLICT (slug) DO NOTHING`,
        [
          competition.title,
          competition.slug,
          competition.description,
          competition.competition_type,
          competition.location,
          competition.start_date,
          competition.end_date,
          competition.registration_deadline,
          competition.prize_info,
          competition.conditions,
          competition.image_url,
          competition.is_international
        ]
      );
      console.log(`✅ Competition created: ${competition.title}`);
    }

    console.log('🎉 Content seed completed successfully!');
  } catch (error) {
    console.error('❌ Error seeding content:', error);
    throw error;
  }
}

// Run if called directly
if (require.main === module) {
  seedContent()
    .then(() => process.exit(0))
    .catch(() => process.exit(1));
}

