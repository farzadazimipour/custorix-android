package com.custorix.core.network

import arrow.core.Either

object NetworkService {
    fun getSampleData(): Either<Nothing, String> {
        return Either.Right("Hello From Network Module")
    }
}