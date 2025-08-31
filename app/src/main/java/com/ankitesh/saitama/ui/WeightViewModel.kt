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

    fun saveWeight(date: Date, weight: Double) {
        viewModelScope.launch {
            repository.saveWeight(date, weight)
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