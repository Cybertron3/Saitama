package com.waystone.saitama.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey
    val date: Date,
    val weight: Double,
    val lastUpdated: Date = Date()
)