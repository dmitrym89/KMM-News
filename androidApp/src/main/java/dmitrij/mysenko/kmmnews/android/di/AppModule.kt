package dmitrij.mysenko.kmmnews.android.di

import dmitrij.mysenko.kmmnews.android.ui.NewsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { NewsViewModel(get()) }
}