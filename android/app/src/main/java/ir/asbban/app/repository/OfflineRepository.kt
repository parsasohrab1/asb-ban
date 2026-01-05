package ir.asbban.app.repository

import ir.asbban.app.data.local.database.AppDatabase
import ir.asbban.app.data.local.database.entity.*
import ir.asbban.app.data.model.*
import ir.asbban.app.utils.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineRepository(
    private val database: AppDatabase,
    private val isOnline: () -> Boolean
) {
    // Products
    suspend fun cacheProducts(products: List<Product>) {
        val cachedProducts = products.map { product ->
            CachedProduct(
                id = product.id,
                name = product.name,
                slug = product.slug,
                description = product.description,
                short_description = product.short_description,
                price = product.price,
                compare_at_price = product.compare_at_price,
                sku = product.sku,
                stock_quantity = product.stock_quantity,
                category_id = product.category_id,
                category_name = product.category_name,
                images = product.images,
                is_active = product.is_active
            )
        }
        database.productDao().insertProducts(cachedProducts)
    }
    
    fun getCachedProducts(): Flow<List<Product>> {
        return database.productDao().getAllProducts().map { cached ->
            cached.map { it.toProduct() }
        }
    }
    
    suspend fun getCachedProduct(slug: String): Product? {
        return database.productDao().getProductBySlug(slug)?.toProduct()
    }
    
    // Blog Posts
    suspend fun cacheBlogPosts(posts: List<BlogPost>) {
        val cachedPosts = posts.map { post ->
            CachedBlogPost(
                id = post.id,
                title = post.title,
                slug = post.slug,
                excerpt = post.excerpt,
                content = post.content,
                featured_image = post.featured_image,
                category_id = post.category_id,
                category_name = post.category_name,
                author_id = post.author_id,
                author_name = post.author_name,
                views_count = post.views_count,
                published_at = post.published_at
            )
        }
        database.blogPostDao().insertPosts(cachedPosts)
    }
    
    fun getCachedBlogPosts(): Flow<List<BlogPost>> {
        return database.blogPostDao().getAllPosts().map { cached ->
            cached.map { it.toBlogPost() }
        }
    }
    
    suspend fun getCachedBlogPost(slug: String): BlogPost? {
        return database.blogPostDao().getPostBySlug(slug)?.toBlogPost()
    }
    
    // Competitions
    suspend fun cacheCompetitions(competitions: List<Competition>) {
        val cachedCompetitions = competitions.map { comp ->
            CachedCompetition(
                id = comp.id,
                title = comp.title,
                slug = comp.slug,
                description = comp.description,
                competition_type = comp.competition_type,
                location = comp.location,
                start_date = comp.start_date,
                end_date = comp.end_date,
                registration_deadline = comp.registration_deadline,
                prize_info = comp.prize_info,
                conditions = comp.conditions,
                image_url = comp.image_url,
                is_international = comp.is_international
            )
        }
        database.competitionDao().insertCompetitions(cachedCompetitions)
    }
    
    fun getCachedCompetitions(): Flow<List<Competition>> {
        return database.competitionDao().getAllCompetitions().map { cached ->
            cached.map { it.toCompetition() }
        }
    }
    
    suspend fun getCachedCompetition(slug: String): Competition? {
        return database.competitionDao().getCompetitionBySlug(slug)?.toCompetition()
    }
    
    // Helper extension functions
    private fun CachedProduct.toProduct(): Product {
        return Product(
            id = id,
            name = name,
            slug = slug,
            description = description,
            short_description = short_description,
            price = price,
            compare_at_price = compare_at_price,
            sku = sku,
            stock_quantity = stock_quantity,
            category_id = category_id,
            category_name = category_name,
            images = images,
            is_active = is_active
        )
    }
    
    private fun CachedBlogPost.toBlogPost(): BlogPost {
        return BlogPost(
            id = id,
            title = title,
            slug = slug,
            excerpt = excerpt,
            content = content,
            featured_image = featured_image,
            category_id = category_id,
            category_name = category_name,
            category_slug = null,
            author_id = author_id,
            author_name = author_name,
            views_count = views_count,
            published_at = published_at,
            created_at = ""
        )
    }
    
    private fun CachedCompetition.toCompetition(): Competition {
        return Competition(
            id = id,
            title = title,
            slug = slug,
            description = description,
            competition_type = competition_type,
            location = location,
            start_date = start_date,
            end_date = end_date,
            registration_deadline = registration_deadline,
            prize_info = prize_info,
            conditions = conditions,
            image_url = image_url,
            is_international = is_international,
            is_published = true
        )
    }
}

