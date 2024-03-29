package com.jarroyo.feature.home.shared.di

import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.jarroyo.feature.home.shared.sqldelight.Database
import com.jarroyo.feature.home.shared.sqldelight.DatabaseWrapper
import io.ktor.client.engine.android.Android
import org.koin.dsl.module

actual fun platformModule() = module {
    single {
        val driver = AndroidSqliteDriver(Database.Schema, get(), "Rockets.db")
        DatabaseWrapper(Database(driver))
    }
    single { Android.create() }
}
