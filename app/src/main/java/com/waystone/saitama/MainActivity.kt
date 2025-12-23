package com.waystone.saitama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.waystone.saitama.data.WeightDatabase
import com.waystone.saitama.data.WeightRepository
import com.waystone.saitama.ui.WeightTrackerScreen
import com.waystone.saitama.ui.WeightViewModel
import com.waystone.saitama.ui.WeightViewModelFactory
import com.waystone.saitama.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database and repository
        val database = WeightDatabase.getDatabase(this)
        val repository = WeightRepository(database.weightDao())
        val viewModelFactory = WeightViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[WeightViewModel::class.java]

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeightTrackerScreen(viewModel = viewModel)
                }
            }
        }
    }
}
