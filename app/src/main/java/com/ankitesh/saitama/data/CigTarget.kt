package com.ankitesh.saitama.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "cig_target")
data class CigTarget(
    @PrimaryKey
    val id: Int = 1,
    val target: Int,
    val lastUpdated: Date = Date()
)
