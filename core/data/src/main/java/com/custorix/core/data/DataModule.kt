package com.custorix.core.data

import com.custorix.core.domain.TestRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    abstract fun bindsTestRepository(impl: TestRepositoryImpl): TestRepository
}