import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dmitrij.mysenko.kmmnews.di.initKoin
import dmitrij.mysenko.kmmnews.remote.NewsData
import dmitrij.mysenko.kmmnews.remote.NewsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image.Companion.makeFromEncoded
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO

private val koin = initKoin(enableNetworkLogs = true).koin

fun main() = application {
    val windowState = rememberWindowState()
    var newsState by remember { mutableStateOf(emptyList<NewsData>()) }
    var selectedNews by remember { mutableStateOf<NewsData?>(null) }
    val newsApi = koin.get<NewsApi>()

    LaunchedEffect(true) {
        newsState = newsApi.fetchNews().data
        selectedNews = newsState.first()
    }
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "News"
    ) {

        Row(Modifier.fillMaxSize()) {

            Box(Modifier.width(250.dp).fillMaxHeight().background(color = Color.LightGray)) {
                NewsList(newsState, selectedNews) {
                    selectedNews = it
                }
            }

            Spacer(modifier = Modifier.width(1.dp).fillMaxHeight())

            Box(Modifier.fillMaxHeight()) {
                selectedNews?.let {
                    NewsDetailsView(it)
                }
            }
        }
    }
}

@Composable
fun NewsList(
    news: List<NewsData>,
    selectedNews: NewsData?,
    newsSelected: (news: NewsData) -> Unit
) {
    if (news.isNotEmpty()) {
        LazyColumn {
            items(news) { news ->
                NewsHolder(news, selectedNews, newsSelected)
            }
        }
    }
}

@Composable
fun NewsDetailsView(data: NewsData) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        item {
            Text(
                text = data.title,
                fontSize = 25.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
        item {
            fetchImage(data.imageUrl)?.let {
                Image(
                    it,
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().aspectRatio(1.5f)
                )
            }
        }
        item {
            Text(
                text = data.content,
                fontSize = 14.sp,
                fontWeight = FontWeight.W400,
                modifier = Modifier
                    .fillMaxWidth().padding(top = 16.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewsHolder(news: NewsData, selectedNews: NewsData?, newsSelected: (news: NewsData) -> Unit) {
    Surface(
        onClick = { newsSelected(news) },
        elevation = 4.dp,
        color = if (news.title == selectedNews?.title) Color.DarkGray else Color.Transparent,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
        ) {
            fetchImage(news.imageUrl)?.let {
                Image(
                    it,
                    contentDescription = "image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(100.dp)
                )
            }
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = news.title, modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun fetchImage(url: String): ImageBitmap? {
    var image by remember(url) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(url) {
        loadFullImage(url)?.let { it ->
            image = makeFromEncoded(toByteArray(it)).toComposeImageBitmap()
        }
    }

    return image
}

fun toByteArray(bitmap: BufferedImage): ByteArray {
    val baos = ByteArrayOutputStream()
    ImageIO.write(bitmap, "png", baos)
    return baos.toByteArray()
}

suspend fun loadFullImage(source: String): BufferedImage? = withContext(Dispatchers.IO) {
    runCatching {
        val url = URL(source)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.connect()

        val input: InputStream = connection.inputStream
        val bitmap: BufferedImage? = ImageIO.read(input)
        bitmap
    }.getOrNull()
}