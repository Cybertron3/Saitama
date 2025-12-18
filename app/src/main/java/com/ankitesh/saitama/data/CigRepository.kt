package com.ankitesh.saitama.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class CigRepository(private val cigDao: CigDao) {

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

    // Get today's count
    fun getTodayCount(): Flow<Int> {
        val today = normalizeDate(Date())
        return cigDao.getAllEntries().map { entries ->
            entries.find { normalizeDate(it.date) == today }?.count ?: 0
        }
    }

    // Get entry for a specific date
    suspend fun getEntryForDate(date: Date): CigEntry? {
        return cigDao.getEntryForDate(normalizeDate(date))
    }

    // Save entry for a specific date
    suspend fun saveEntry(date: Date, count: Int) {
        val normalizedDate = normalizeDate(date)
        cigDao.insertOrUpdate(
            CigEntry(
                date = normalizedDate,
                count = count.coerceIn(0, 99),
                lastUpdated = Date()
            )
        )
    }

    // Increment today's count
    suspend fun incrementTodayCount() {
        val today = normalizeDate(Date())
        val currentEntry = cigDao.getEntryForDate(today)
        val newCount = ((currentEntry?.count ?: 0) + 1).coerceAtMost(99)
        saveEntry(today, newCount)
    }

    // Decrement today's count
    suspend fun decrementTodayCount() {
        val today = normalizeDate(Date())
        val currentEntry = cigDao.getEntryForDate(today)
        val newCount = ((currentEntry?.count ?: 0) - 1).coerceAtLeast(0)
        saveEntry(today, newCount)
    }

    // Get all entries
    fun getAllEntries(): Flow<List<CigEntry>> {
        return cigDao.getAllEntries()
    }

    // Get entries for last 6 months
    fun getEntriesForLastSixMonths(): Flow<List<CigEntry>> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.time

        calendar.add(Calendar.MONTH, -6)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startDate = calendar.time

        return cigDao.getEntriesBetween(startDate, endDate)
    }

    // Target operations
    fun getTarget(): Flow<Int?> {
        return cigDao.getTarget().map { it?.target }
    }

    suspend fun getTargetSync(): Int? {
        return cigDao.getTargetSync()?.target
    }

    suspend fun saveTarget(target: Int) {
        cigDao.saveTarget(
            CigTarget(
                id = 1,
                target = target.coerceIn(0, 10),
                lastUpdated = Date()
            )
        )
    }

    // Delete entry
    suspend fun deleteEntry(date: Date) {
        val normalizedDate = normalizeDate(date)
        val entry = cigDao.getEntryForDate(normalizedDate)
        entry?.let { cigDao.delete(it) }
    }
}
