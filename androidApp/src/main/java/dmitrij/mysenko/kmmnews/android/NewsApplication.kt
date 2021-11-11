package dmitrij.mysenko.kmmnews.android

import android.app.Application
import co.touchlab.kermit.Kermit
import dmitrij.mysenko.kmmnews.android.di.appModule
import dmitrij.mysenko.kmmnews.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NewsApplication : Application(), KoinComponent {
    private val logger: Kermit by inject()

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger()
            androidContext(this@NewsApplication)
            modules(appModule)
        }

        logger.d { "KMM-News" }
    }
}