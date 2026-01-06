package com.custorix.core.data

import com.custorix.core.domain.TestRepository
import com.custorix.core.network.NetworkService
import javax.inject.Inject

internal class TestRepositoryImpl @Inject constructor() : TestRepository {
    override fun getSampleData(): String {
        return NetworkService.getSampleData().fold(
            ifRight = { it },
            ifLeft = { "Error from Data layer" }
        )
    }
}