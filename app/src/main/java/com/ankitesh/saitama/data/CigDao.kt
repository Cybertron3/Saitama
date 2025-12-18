package com.ankitesh.saitama.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface CigDao {
    @Query("SELECT * FROM cig_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getEntriesBetween(startDate: Date, endDate: Date): Flow<List<CigEntry>>

    @Query("SELECT * FROM cig_entries WHERE date = :date")
    suspend fun getEntryForDate(date: Date): CigEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(entry: CigEntry)

    @Delete
    suspend fun delete(entry: CigEntry)

    @Query("SELECT * FROM cig_entries ORDER BY date DESC")
    fun getAllEntries(): Flow<List<CigEntry>>

    // Target operations
    @Query("SELECT * FROM cig_target WHERE id = 1")
    fun getTarget(): Flow<CigTarget?>

    @Query("SELECT * FROM cig_target WHERE id = 1")
    suspend fun getTargetSync(): CigTarget?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTarget(target: CigTarget)
}
