package dmitrij.mysenko.kmmnews.remote

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent

@Serializable
data class NewsResult(
    val category: Categoty,
    val success: Boolean,
    val data: List<NewsData>
)

@Serializable
data class NewsData(
    val author: String,
    val content: String,
    val date: String,
    val imageUrl: String,
    val readMoreUrl: String?,
    val time: String,
    val title: String,
    val url: String,
)

@Serializable
enum class Categoty {
    all,
    national,
    business,
    sports,
    world,
    politics,
    technology,
    startup,
    entertainment,
    miscellaneous,
    hatke,
    science,
    automobile
}

class NewsApi(
    private val client: HttpClient,
    var baseUrl: String = "https://inshortsapi.vercel.app",
) : KoinComponent {
    suspend fun fetchNews(category: Categoty = Categoty.all) = client.get<NewsResult>("$baseUrl/news"){parameter("category", category)}
}