package com.dev.core.model.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BaseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
