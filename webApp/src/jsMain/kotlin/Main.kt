import androidx.compose.runtime.*
import dmitrij.mysenko.kmmnews.di.initKoin
import dmitrij.mysenko.kmmnews.remote.NewsData
import dmitrij.mysenko.kmmnews.repository.NewsRepository
import dmitrij.mysenko.kmmnews.repository.NewsInterface
import kotlinx.browser.document
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import org.jetbrains.compose.common.foundation.layout.Column
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.css.selectors.attr
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable

private val koin = initKoin(enableNetworkLogs = true).koin

@InternalCoroutinesApi
fun main() {
    val repo = koin.get<NewsInterface>()

    renderComposable(rootElementId = "root") {
        Style(TextStyles)

        var news by remember { mutableStateOf(emptyList<NewsData>()) }
        var selectedNews by remember { mutableStateOf<NewsData?>(null) }

        LaunchedEffect(true) {
            news = repo.fetchNews()
            selectedNews = news.first()
        }

        Div {


            Div(attrs = {
                style {
                    display(DisplayStyle.Flex)
                }
            }) {
                Div(attrs = { style { padding(16.px) } }) {

                    H1(attrs = {
                        classes(TextStyles.titleText)
                        style {
                            paddingLeft(16.px)
                        }
                    }) {
                        Text("All News")
                    }

                    news.forEachIndexed { i, item ->
                        if (i < 10) {
                            Div(
                                attrs = {
                                    style {
                                        display(DisplayStyle.Flex)
                                        alignItems(AlignItems.Normal)
                                        if(item.title == selectedNews?.title)backgroundColor(Color("lightgray"))
                                    }
                                    onClick {
                                        selectedNews = item
                                    }
                                }
                            ) {
                                Img(
                                    src = item.imageUrl,

                                    attrs = {
                                        style {
                                            width(200.px)
                                            height(200.px)
                                            flexWrap(FlexWrap.Wrap)
                                            property("padding-right", 16.px)
                                            property("padding-bottom", 16.px)
                                        }
                                    }
                                )

                                Span(attrs = {
                                    classes(TextStyles.newsText)
                                    style {
                                        width(400.px)
                                    }
                                }) {
                                    Text(value = item.title)
                                }
                            }
                        }
                    }
                }
                Div(attrs = { style { padding(16.px) } }) {
                    selectedNews?.let { data ->
                        H1(attrs = {
                            classes(TextStyles.titleText)
                            style {
                                property("padding-bottom", 16.px)
                            }
                        }) {
                            Text(value = data.title)
                        }

                        Img(
                            src = data.imageUrl,
                            attrs = {
                                classes()
                                style {
                                    height(600.px)
                                    property("padding-bottom", 16.px)
                                }
                            }
                        )

                        Div {
                            Span(attrs = {
                                classes(TextStyles.newsText)
                            }) {
                                Text(value = data.content)
                            }
                        }

                    }
                }
            }
        }
    }
}

object TextStyles : StyleSheet() {

    val titleText by style {
        color(rgb(23, 24, 28))
        fontSize(50.px)
        property("font-size", 50.px)
        property("letter-spacing", (-1.5).px)
        property("font-weight", 900)
        property("line-height", 58.px)

        property(
            "font-family",
            "Gotham SSm A,Gotham SSm B,system-ui,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Oxygen,Ubuntu,Cantarell,Droid Sans,Helvetica Neue,Arial,sans-serif"
        )
    }

    val newsText by style {
        color(rgb(23, 24, 28))
        fontSize(24.px)
        property("font-size", 28.px)
        property("letter-spacing", "normal")
        property("font-weight", 300)
        property("line-height", 40.px)

        property(
            "font-family",
            "Gotham SSm A,Gotham SSm B,system-ui,-apple-system,BlinkMacSystemFont,Segoe UI,Roboto,Oxygen,Ubuntu,Cantarell,Droid Sans,Helvetica Neue,Arial,sans-serif"
        )
    }
}