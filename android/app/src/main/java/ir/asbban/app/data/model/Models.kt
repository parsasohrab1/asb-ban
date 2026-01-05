package ir.asbban.app.data.model

// Auth Models
data class RegisterRequest(
    val email: String,
    val password: String,
    val full_name: String,
    val phone: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: AuthData
)

data class AuthData(
    val user: User,
    val token: String
)

data class User(
    val id: Int? = null,
    val email: String,
    val full_name: String? = null,
    val phone: String? = null,
    val role: String = "user",
    val avatar_url: String? = null
)

// Blog Models
data class BlogPost(
    val id: Int,
    val title: String,
    val slug: String,
    val excerpt: String?,
    val content: String,
    val featured_image: String?,
    val category_id: Int?,
    val category_name: String?,
    val category_slug: String?,
    val author_id: Int?,
    val author_name: String?,
    val views_count: Int,
    val published_at: String?,
    val created_at: String
)

data class BlogPostsResponse(
    val success: Boolean,
    val data: BlogPostsData
)

data class BlogPostsData(
    val posts: List<BlogPost>,
    val pagination: Pagination
)

data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)

data class BlogCategory(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String?,
    val parent_id: Int?
)

// Service Models
data class Veterinarian(
    val id: Int,
    val user_id: Int?,
    val full_name: String,
    val specialization: String?,
    val region: String?,
    val phone: String,
    val email: String?,
    val resume: String?,
    val image_url: String?,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val rating: Double,
    val total_reviews: Int,
    val is_verified: Boolean,
    val is_active: Boolean,
    val distance: Double? = null
)

data class Transporter(
    val id: Int,
    val user_id: Int?,
    val company_name: String?,
    val contact_name: String,
    val phone: String,
    val email: String?,
    val region: String?,
    val latitude: Double?,
    val longitude: Double?,
    val address: String?,
    val equipment: String?,
    val transport_info: String?,
    val rating: Double,
    val total_reviews: Int,
    val is_verified: Boolean,
    val is_active: Boolean,
    val distance: Double? = null
)

data class BookingRequest(
    val service_type: String,
    val service_provider_id: Int,
    val booking_date: String,
    val description: String?
)

data class Booking(
    val id: Int,
    val user_id: Int,
    val service_type: String,
    val service_provider_id: Int,
    val booking_date: String,
    val description: String?,
    val status: String,
    val created_at: String
)

// Shop Models
data class Product(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String?,
    val short_description: String?,
    val price: Double,
    val compare_at_price: Double?,
    val sku: String?,
    val stock_quantity: Int,
    val category_id: Int?,
    val category_name: String?,
    val images: List<String>?,
    val is_active: Boolean
)

data class ProductsResponse(
    val success: Boolean,
    val data: ProductsData
)

data class ProductsData(
    val products: List<Product>,
    val pagination: Pagination
)

data class ProductCategory(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String?,
    val parent_id: Int?,
    val image_url: String?
)

data class OrderRequest(
    val items: List<OrderItemRequest>,
    val shipping_address: String,
    val payment_method: String
)

data class OrderItemRequest(
    val product_id: Int,
    val quantity: Int
)

data class Order(
    val id: Int,
    val user_id: Int,
    val order_number: String,
    val total_amount: Double,
    val status: String,
    val shipping_address: String,
    val payment_status: String,
    val payment_method: String?,
    val created_at: String,
    val items: List<OrderItem>? = null
)

data class OrderItem(
    val id: Int,
    val product_id: Int,
    val quantity: Int,
    val price: Double,
    val product_name: String? = null
)

// Competition Models
data class Competition(
    val id: Int,
    val title: String,
    val slug: String,
    val description: String?,
    val competition_type: String?,
    val location: String,
    val start_date: String,
    val end_date: String?,
    val registration_deadline: String?,
    val prize_info: String?,
    val conditions: String?,
    val image_url: String?,
    val is_international: Boolean,
    val is_published: Boolean
)

data class CompetitionResult(
    val id: Int,
    val competition_id: Int,
    val position: Int?,
    val participant_name: String?,
    val horse_name: String?,
    val score: Double?,
    val notes: String?
)

