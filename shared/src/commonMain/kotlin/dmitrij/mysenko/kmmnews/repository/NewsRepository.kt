package dmitrij.mysenko.kmmnews.repository

import co.touchlab.kermit.Kermit
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import dmitrij.mysenko.kmmnews.di.NewsDatabaseWrapper
import dmitrij.mysenko.kmmnews.remote.Categoty
import dmitrij.mysenko.kmmnews.remote.NewsApi
import dmitrij.mysenko.kmmnews.remote.NewsData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.CoroutineContext

interface NewsInterface {
    fun fetchNewsAsFlow(category: Categoty = Categoty.all): Flow<List<NewsData>>
    suspend fun fetchNews(category: Categoty = Categoty.all): List<NewsData>
    suspend fun fetchAndStoreNews(category: Categoty = Categoty.all)
}

class NewsRepository : KoinComponent, NewsInterface {
    private val newsApi: NewsApi by inject()
    private val logger: Kermit by inject()

    private val coroutineScope: CoroutineScope = MainScope()
    private val newsDatabase: NewsDatabaseWrapper by inject()
    private val newsQueries = newsDatabase.instance?.newsDatabaseQueries

    var newsJob: Job? = null

    init {
        coroutineScope.launch {
            fetchAndStoreNews()
        }
    }

    override fun fetchNewsAsFlow(category: Categoty): Flow<List<NewsData>> {
        return newsQueries?.selectAll(
            mapper = { url, author, content, date, imageUrl, readMoreUrl, time, title, _ ->
                NewsData(
                    author = author,
                    content = content,
                    date = date,
                    imageUrl = imageUrl,
                    readMoreUrl = readMoreUrl,
                    time = time,
                    title = title,
                    url = url,
                )
            }
        )?.asFlow()?.mapToList() ?: flowOf(emptyList())
    }

    override suspend fun fetchNews(category: Categoty): List<NewsData> =
        newsApi.fetchNews(category).data

    override suspend fun fetchAndStoreNews(category: Categoty) {
        logger.d { "fetchAndStoreNews" }
        try {
            val result = newsApi.fetchNews()

            newsQueries?.transaction {
                result.data.forEach { item ->
                    newsQueries.insertNews(
                        item.url,
                        item.author,
                        item.content,
                        item.date,
                        item.imageUrl,
                        item.readMoreUrl ?: "",
                        item.time,
                        item.title,
                        result.category.name
                    )
                }
            }
        } catch (e: Exception) {
            // TODO report error up to UI
            logger.w(e) { "Exception during fetchAndStoreNews: $e" }
        }
    }

    // called from Kotlin/Native clients
    fun startObservingNewsUpdates(category: Categoty, success: (List<NewsData>) -> Unit) {
        logger.d { "startObservingNewsUpdates" }
        newsJob = coroutineScope.launch {
            fetchNewsAsFlow(category).collect {
                success(it)
            }
        }
    }

    fun stopObservingPeopleUpdates() {
        logger.d { "stopObservingNewsUpdates, peopleJob = $newsJob" }
        newsJob?.cancel()
    }


    val iosScope: CoroutineScope = object : CoroutineScope {
        override val coroutineContext: CoroutineContext
            get() = SupervisorJob() + Dispatchers.Main
    }

    companion object {
        private const val POLL_INTERVAL = 10000L
    }
}

class KotlinNativeFlowWrapper<T>(private val flow: Flow<T>) {
    fun subscribe(
        scope: CoroutineScope,
        onEach: (item: T) -> Unit,
        onComplete: () -> Unit,
        onThrow: (error: Throwable) -> Unit
    ) = flow
        .onEach { onEach(it) }
        .catch { onThrow(it) }
        .onCompletion { onComplete() }
        .launchIn(scope)
}