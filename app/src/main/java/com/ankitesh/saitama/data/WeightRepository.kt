package com.ankitesh.saitama.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class WeightRepository(private val weightDao: WeightDao) {
    
    fun getWeightsForLastSevenDays(): Flow<List<WeightEntry>> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time
        
        calendar.add(Calendar.DAY_OF_YEAR, -6) // 7 days including today
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.time
        
        return weightDao.getWeightsBetween(startDate, endDate)
    }
    
    fun getAverageWeight(): Flow<Double?> {
        return getWeightsForLastSevenDays().map { weights ->
            if (weights.isEmpty()) null
            else weights.map { it.weight }.average()
        }
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
}