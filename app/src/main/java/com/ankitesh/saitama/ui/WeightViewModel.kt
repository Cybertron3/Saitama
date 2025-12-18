package com.ankitesh.saitama.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ankitesh.saitama.data.WeightRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class WeightViewModel(
    private val repository: WeightRepository
) : ViewModel() {

    val averageWeight: StateFlow<String> = repository.getAverageWeight()
        .map { avg ->
            avg?.let { String.format("%.2f", it) } ?: "No data"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "No data"
        )

    val allWeights: StateFlow<Map<Date, Double>> = repository.getAllWeights()
        .map { entries ->
            entries.associate { it.date to it.weight }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val sixMonthWeights: StateFlow<List<Pair<Date, Double>>> = repository.getWeightsForLastSixMonths()
        .map { entries ->
            entries
                .sortedBy { it.date }
                .map { it.date to it.weight }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Today's weight for homepage
    val todayWeight: StateFlow<Double?> = repository.getTodayWeight()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Last recorded weight (excluding today) for trend indicator
    private val _lastRecordedWeight = MutableStateFlow<Double?>(null)
    val lastRecordedWeight: StateFlow<Double?> = _lastRecordedWeight.asStateFlow()

    init {
        viewModelScope.launch {
            _lastRecordedWeight.value = repository.getLastRecordedWeight()
        }
        // Update last recorded weight when weights change
        viewModelScope.launch {
            repository.getAllWeights().collect {
                _lastRecordedWeight.value = repository.getLastRecordedWeight()
            }
        }
    }

    fun saveWeight(date: Date, weight: Double) {
        viewModelScope.launch {
            repository.saveWeight(date, weight)
        }
    }

    // Save today's weight from homepage
    fun saveTodayWeight(weight: Double) {
        viewModelScope.launch {
            repository.saveTodayWeight(weight)
        }
    }

    suspend fun getWeightForDate(date: Date): Double? {
        return repository.getWeightForDate(date)?.weight
    }

    fun deleteWeight(date: Date) {
        viewModelScope.launch {
            repository.deleteWeight(date)
        }
    }
}

class WeightViewModelFactory(
    private val repository: WeightRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeightViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeightViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}