package com.ankitesh.saitama.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "cig_entries")
data class CigEntry(
    @PrimaryKey
    val date: Date,
    val count: Int,
    val lastUpdated: Date = Date()
)
