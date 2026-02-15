package com.custorix.core.data.repository

import com.custorix.core.database.dao.TempDao
import com.custorix.core.database.model.TempEntity
import com.custorix.core.domain.TestRepository
import com.custorix.core.network.NetworkService
import javax.inject.Inject

internal class TestRepositoryImpl @Inject constructor(
    private val tempDao: TempDao,
) : TestRepository {
    override suspend fun getSampleData(): String {
        return NetworkService.getSampleData().fold(
            ifRight = {
                tempDao.insert(TempEntity(name = it))
                it
            },
            ifLeft = { "Error from Data layer" }
        )
    }
}