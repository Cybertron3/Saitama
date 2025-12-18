package com.ankitesh.saitama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.ankitesh.saitama.data.CigRepository
import com.ankitesh.saitama.data.WeightDatabase
import com.ankitesh.saitama.data.WeightRepository
import com.ankitesh.saitama.ui.WeightViewModel
import com.ankitesh.saitama.ui.WeightViewModelFactory
import com.ankitesh.saitama.ui.cig.CigViewModel
import com.ankitesh.saitama.ui.cig.CigViewModelFactory
import com.ankitesh.saitama.ui.navigation.SaitamaNavHost
import com.ankitesh.saitama.ui.theme.SaitamaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database
        val database = WeightDatabase.getDatabase(this)

        // Weight repository and ViewModel
        val weightRepository = WeightRepository(database.weightDao())
        val weightViewModelFactory = WeightViewModelFactory(weightRepository)
        val weightViewModel = ViewModelProvider(this, weightViewModelFactory)[WeightViewModel::class.java]

        // Cig repository and ViewModel
        val cigRepository = CigRepository(database.cigDao())
        val cigViewModelFactory = CigViewModelFactory(cigRepository)
        val cigViewModel = ViewModelProvider(this, cigViewModelFactory)[CigViewModel::class.java]

        setContent {
            SaitamaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SaitamaNavHost(
                        weightViewModel = weightViewModel,
                        cigViewModel = cigViewModel
                    )
                }
            }
        }
    }
}