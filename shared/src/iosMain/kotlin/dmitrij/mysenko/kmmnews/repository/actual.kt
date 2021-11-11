package dmitrij.mysenko.kmmnews.repository

import co.touchlab.kermit.Logger
import co.touchlab.kermit.NSLogLogger
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver
import dmitrij.mysenko.kmmnews.di.NewsDatabaseWrapper
import local.db.NewsDatabase
import org.koin.dsl.module

actual fun platformModule() = module {
    single {
        val driver = NativeSqliteDriver(NewsDatabase.Schema, "news.db")
        NewsDatabaseWrapper(NewsDatabase(driver))
    }
    single<Logger> { NSLogLogger() }

}