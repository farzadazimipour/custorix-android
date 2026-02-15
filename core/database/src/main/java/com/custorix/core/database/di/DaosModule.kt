package com.custorix.core.database.di

import com.custorix.core.database.CustorixDatabase
import com.custorix.core.database.dao.TempDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {
    @Provides
    fun providesTempDao(db: CustorixDatabase): TempDao = db.tempDao()
}