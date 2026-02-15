package com.custorix.core.database.di

import android.content.Context
import androidx.room.Room
import com.custorix.core.database.CustorixDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun providesCustorixDatabase(
        @ApplicationContext context: Context,
    ): CustorixDatabase = Room.databaseBuilder(
        context = context,
        klass = CustorixDatabase::class.java,
        name = "custorix_database",
    ).build()
}