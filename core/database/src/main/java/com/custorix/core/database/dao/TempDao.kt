package com.custorix.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.custorix.core.database.model.TempEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TempDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tempEntity: TempEntity)

    @Query("SELECT * FROM `temp`")
    suspend fun getAll(): List<TempEntity>

    @Query("SELECT * FROM `temp`")
    fun getAllFlow(): Flow<List<TempEntity>>
}