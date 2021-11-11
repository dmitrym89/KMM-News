package dmitrij.mysenko.kmmnews

import dmitrij.mysenko.kmmnews.di.initKoin
import dmitrij.mysenko.kmmnews.remote.NewsApi
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val koin = initKoin(enableNetworkLogs = true).koin
        val api = koin.get<NewsApi>()
        println(api.fetchNews())
    }
}