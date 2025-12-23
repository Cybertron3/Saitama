package com.waystone.saitama.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getWeightsBetween(startDate: Date, endDate: Date): Flow<List<WeightEntry>>

    @Query("SELECT * FROM weight_entries WHERE date = :date")
    suspend fun getWeightForDate(date: Date): WeightEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(weight: WeightEntry)

    @Delete
    suspend fun delete(weight: WeightEntry)

    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    fun getAllWeights(): Flow<List<WeightEntry>>
}