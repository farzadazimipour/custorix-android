package com.custorix.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.custorix.core.database.dao.TempDao
import com.custorix.core.database.model.TempEntity

@Database(
    exportSchema = true,
    entities = [
        TempEntity::class,
    ],
    version = 1,
    autoMigrations = [],
)
internal abstract class CustorixDatabase : RoomDatabase() {
    abstract fun tempDao(): TempDao
}