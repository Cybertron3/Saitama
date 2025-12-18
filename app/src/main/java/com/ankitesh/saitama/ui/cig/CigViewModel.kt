package com.ankitesh.saitama.ui.cig

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ankitesh.saitama.data.CigRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class CigViewModel(
    private val repository: CigRepository
) : ViewModel() {

    // Today's cigarette count
    val todayCigCount: StateFlow<Int> = repository.getTodayCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // Current target (null if not set)
    val cigTarget: StateFlow<Int?> = repository.getTarget()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // All entries for calendar
    val allEntries: StateFlow<Map<Date, Int>> = repository.getAllEntries()
        .map { entries ->
            entries.associate { it.date to it.count }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Six month entries for graph
    val sixMonthEntries: StateFlow<List<Pair<Date, Int>>> = repository.getEntriesForLastSixMonths()
        .map { entries ->
            entries
                .sortedBy { it.date }
                .map { it.date to it.count }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Increment today's count
    fun incrementTodayCount() {
        viewModelScope.launch {
            repository.incrementTodayCount()
        }
    }

    // Decrement today's count
    fun decrementTodayCount() {
        viewModelScope.launch {
            repository.decrementTodayCount()
        }
    }

    // Save entry for a specific date
    fun saveEntry(date: Date, count: Int) {
        viewModelScope.launch {
            repository.saveEntry(date, count)
        }
    }

    // Get entry for a specific date
    suspend fun getEntryForDate(date: Date): Int {
        return repository.getEntryForDate(date)?.count ?: 0
    }

    // Save target
    fun saveTarget(target: Int) {
        viewModelScope.launch {
            repository.saveTarget(target)
        }
    }
}

class CigViewModelFactory(
    private val repository: CigRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CigViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CigViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
