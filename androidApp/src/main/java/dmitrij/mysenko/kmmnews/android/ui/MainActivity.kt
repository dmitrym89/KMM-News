package dmitrij.mysenko.kmmnews.android.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import org.koin.androidx.compose.getViewModel
import dmitrij.mysenko.kmmnews.android.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainLayout()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainLayout() {
    val navController = rememberNavController()
    val newsViewModel = getViewModel<NewsViewModel>()


    MaterialTheme {
        NavHost(navController = navController, startDestination = "all_news") {
            composable("all_news") {
                ListNewsScreen(
                    viewModel = newsViewModel,
                    navController = navController
                )
            }
            composable("details_news") {
                DetailsNewsScreen(
                    viewModel = newsViewModel,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun DetailsNewsScreen(viewModel: NewsViewModel, navController: NavController) {
    val newsItem = viewModel.selectedNews
    BackHandler(enabled = true) {
        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
    ) {
        Text(text = newsItem?.title ?: "", fontSize = 20.sp, fontWeight = FontWeight.W700)

        newsItem?.imageUrl?.let { url ->
            Image(
                painter = rememberImagePainter(url),
                contentDescription = "image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(1.5f)
            )
        }

        Text(text = newsItem?.content ?: "")
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ListNewsScreen(viewModel: NewsViewModel, navController: NavController) {
    val news = viewModel.news.collectAsState()

    Log.e("AA", "newsValue = ${news.value.size}")
    if (news.value.isNotEmpty())
        Log.e("AA", "0 = ${news.value[0]}")

    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        news.value.forEach { newsItem ->
            item {
                Surface(
                    onClick = {
                        viewModel.selectedNews = newsItem
                        navController.navigate(
                            route = "details_news", navOptions = NavOptions.Builder()
                                .setEnterAnim(R.anim.slide_in_right)
                                .setExitAnim(R.anim.slide_out_left)
                                .setPopEnterAnim(R.anim.slide_in_left)
                                .setPopExitAnim(R.anim.slide_out_right)
                                .build()
                        )
                    },
                    elevation = 4.dp,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp)
                    ) {
                        Image(
                            painter = rememberImagePainter(newsItem.imageUrl),
                            contentDescription = "image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = newsItem.title, modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
