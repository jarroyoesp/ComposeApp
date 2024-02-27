package com.jarroyo.feature.home.shared.interactor

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.jarroyo.feature.home.api.interactor.RemoveFavoriteInteractor
import com.jarroyo.feature.home.shared.di.DatabaseWrapper
import com.jarroyo.feature.home.shared.sqldelight.dao.FavoriteRocketsDao

class RemoveFavoriteInteractorImpl(
    private val databaseWrapper: DatabaseWrapper,
) : RemoveFavoriteInteractor {
    override suspend operator fun invoke(
        id: String,
    ): Result<Boolean, Exception> = try {
        FavoriteRocketsDao(checkNotNull(databaseWrapper.instance)).removeItem(id)
        Ok(true)
    } catch (e: Exception) {
        Err(e)
    }
}