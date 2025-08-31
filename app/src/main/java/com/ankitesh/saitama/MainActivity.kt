package com.ankitesh.saitama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.ankitesh.saitama.data.WeightDatabase
import com.ankitesh.saitama.data.WeightRepository
import com.ankitesh.saitama.ui.WeightTrackerScreen
import com.ankitesh.saitama.ui.WeightViewModel
import com.ankitesh.saitama.ui.WeightViewModelFactory
import com.ankitesh.saitama.ui.theme.SaitamaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database and repository
        val database = WeightDatabase.getDatabase(this)
        val repository = WeightRepository(database.weightDao())
        val viewModelFactory = WeightViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[WeightViewModel::class.java]
        
        setContent {
            SaitamaTheme {
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