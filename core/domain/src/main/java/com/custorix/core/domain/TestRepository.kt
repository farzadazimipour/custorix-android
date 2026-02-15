package com.custorix.core.domain

interface TestRepository {
    suspend fun getSampleData(): String
}