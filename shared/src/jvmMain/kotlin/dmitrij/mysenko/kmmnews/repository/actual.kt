package dmitrij.mysenko.kmmnews.repository

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Logger
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import dmitrij.mysenko.kmmnews.di.NewsDatabaseWrapper
import local.db.NewsDatabase
import org.koin.dsl.module

actual fun platformModule() = module {
    single {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
            .also { NewsDatabase.Schema.create(it) }
        NewsDatabaseWrapper(NewsDatabase(driver))
    }

    single<Logger> { CommonLogger() }
}