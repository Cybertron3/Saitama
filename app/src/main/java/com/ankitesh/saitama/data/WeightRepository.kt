package com.ankitesh.saitama.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class WeightRepository(private val weightDao: WeightDao) {

    // Changed: Now returns last 7 entries instead of last 7 calendar days
    fun getLastSevenEntries(): Flow<List<WeightEntry>> {
        return weightDao.getLastNEntries(7)
    }

    fun getAverageWeight(): Flow<Double?> {
        return getLastSevenEntries().map { weights ->
            if (weights.isEmpty()) null
            else weights.map { it.weight }.average()
        }
    }

    // Get today's weight
    fun getTodayWeight(): Flow<Double?> {
        val today = normalizeDate(Date())
        return weightDao.getAllWeights().map { entries ->
            entries.find { normalizeDate(it.date) == today }?.weight
        }
    }

    // Get the last recorded weight (excluding today)
    suspend fun getLastRecordedWeight(): Double? {
        val today = normalizeDate(Date())
        return weightDao.getLastEntryBefore(today)?.weight
    }

    // Save today's weight
    suspend fun saveTodayWeight(weight: Double) {
        saveWeight(Date(), weight)
    }

    private fun normalizeDate(date: Date): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }
    
    suspend fun getWeightForDate(date: Date): WeightEntry? {
        // Normalize date to start of day
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return weightDao.getWeightForDate(calendar.time)
    }
    
    suspend fun saveWeight(date: Date, weight: Double) {
        // Normalize date to start of day
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        weightDao.insertOrUpdate(
            WeightEntry(
                date = calendar.time,
                weight = weight,
                lastUpdated = Date()
            )
        )
    }
    
    fun getAllWeights(): Flow<List<WeightEntry>> {
        return weightDao.getAllWeights()
    }
    
    fun getWeightsForLastSixMonths(): Flow<List<WeightEntry>> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        
        calendar.add(Calendar.MONTH, -6) // 6 months back
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.time
        
        return weightDao.getWeightsBetween(startDate, endDate)
    }
    
    suspend fun deleteWeight(date: Date) {
        // Normalize date to start of day
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        val entry = weightDao.getWeightForDate(calendar.time)
        entry?.let {
            weightDao.delete(it)
        }
    }
}