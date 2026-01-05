package ir.asbban.app.data.remote

import ir.asbban.app.data.model.*
import ir.asbban.app.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Auth
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
    
    @GET("auth/profile")
    suspend fun getProfile(): Response<ApiResponse<User>>
    
    @PUT("auth/profile")
    suspend fun updateProfile(@Body user: User): Response<ApiResponse<User>>
    
    // Blog
    @GET("blog/posts")
    suspend fun getBlogPosts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("category_id") categoryId: String? = null
    ): Response<BlogPostsResponse>
    
    @GET("blog/posts/{slug}")
    suspend fun getBlogPost(@Path("slug") slug: String): Response<BlogPost>
    
    @GET("blog/posts/search")
    suspend fun searchPosts(@Query("q") query: String): Response<List<BlogPost>>
    
    @GET("blog/categories")
    suspend fun getBlogCategories(): Response<List<BlogCategory>>
    
    // Services
    @GET("services/veterinarians")
    suspend fun getVeterinarians(
        @Query("region") region: String? = null,
        @Query("specialization") specialization: String? = null,
        @Query("latitude") latitude: String? = null,
        @Query("longitude") longitude: String? = null,
        @Query("radius") radius: String? = null
    ): Response<List<Veterinarian>>
    
    @GET("services/veterinarians/{id}")
    suspend fun getVeterinarian(@Path("id") id: Int): Response<Veterinarian>
    
    @GET("services/transporters")
    suspend fun getTransporters(
        @Query("region") region: String? = null,
        @Query("latitude") latitude: String? = null,
        @Query("longitude") longitude: String? = null,
        @Query("radius") radius: String? = null
    ): Response<List<Transporter>>
    
    @GET("services/transporters/{id}")
    suspend fun getTransporter(@Path("id") id: Int): Response<Transporter>
    
    @POST("services/bookings")
    suspend fun createBooking(@Body booking: BookingRequest): Response<Booking>
    
    @GET("services/bookings")
    suspend fun getBookings(): Response<List<Booking>>
    
    // Shop
    @GET("shop/products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 12,
        @Query("category_id") categoryId: String? = null,
        @Query("search") search: String? = null
    ): Response<ProductsResponse>
    
    @GET("shop/products/{slug}")
    suspend fun getProduct(@Path("slug") slug: String): Response<Product>
    
    @GET("shop/categories")
    suspend fun getProductCategories(): Response<List<ProductCategory>>
    
    @POST("shop/orders")
    suspend fun createOrder(@Body order: OrderRequest): Response<Order>
    
    @GET("shop/orders")
    suspend fun getOrders(): Response<List<Order>>
    
    @GET("shop/orders/{id}")
    suspend fun getOrder(@Path("id") id: Int): Response<Order>
    
    // Competitions
    @GET("competitions")
    suspend fun getCompetitions(
        @Query("type") type: String? = null,
        @Query("is_international") isInternational: Boolean? = null,
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): Response<List<Competition>>
    
    @GET("competitions/{slug}")
    suspend fun getCompetition(@Path("slug") slug: String): Response<Competition>
    
    @GET("competitions/{id}/results")
    suspend fun getCompetitionResults(@Path("id") id: Int): Response<List<CompetitionResult>>
}

