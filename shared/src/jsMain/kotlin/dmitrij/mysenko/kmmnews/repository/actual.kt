package dmitrij.mysenko.kmmnews.repository

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Logger
import dmitrij.mysenko.kmmnews.di.NewsDatabaseWrapper
import org.koin.dsl.module

actual fun platformModule() = module {
    single {
        NewsDatabaseWrapper(null)
    }

    single<Logger> { CommonLogger() }
}