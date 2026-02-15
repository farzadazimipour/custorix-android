package com.custorix.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "temp")
data class TempEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
)