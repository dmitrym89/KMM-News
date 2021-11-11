package dmitrij.mysenko.kmmnews.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dmitrij.mysenko.kmmnews.remote.NewsData
import dmitrij.mysenko.kmmnews.repository.NewsInterface
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class NewsViewModel (
    private val newsRepository: NewsInterface
) : ViewModel() {

    val news = newsRepository.fetchNewsAsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var selectedNews: NewsData? = null

}