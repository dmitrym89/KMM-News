package dmitrij.mysenko.kmmnews.repository

import co.touchlab.kermit.LogcatLogger
import co.touchlab.kermit.Logger
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dmitrij.mysenko.kmmnews.di.NewsDatabaseWrapper
import local.db.NewsDatabase
import org.koin.dsl.module

actual fun platformModule() = module {
    single {
        val driver =
            AndroidSqliteDriver(NewsDatabase.Schema, get(), "news.db")

        NewsDatabaseWrapper(NewsDatabase(driver))
    }
    single<Logger> { LogcatLogger() }
}