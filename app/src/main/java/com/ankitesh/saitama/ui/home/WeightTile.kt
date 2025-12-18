package com.ankitesh.saitama.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun WeightTile(
    averageWeight: String,
    todayWeight: Double?,
    lastRecordedWeight: Double?,
    onWeightChange: (Double) -> Unit,
    onNavigateToDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    var weightText by remember(todayWeight) {
        mutableStateOf(todayWeight?.let { String.format("%.2f", it) } ?: "")
    }
    val scope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    val hasData = averageWeight != "No data"
    val trendDiff = if (todayWeight != null && lastRecordedWeight != null) {
        todayWeight - lastRecordedWeight
    } else null

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onNavigateToDetail() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header with title and navigation arrow
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WEIGHT",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Go to details",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                )

                if (hasData) {
                    // With Data State
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 7-day average
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "7-day avg",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = averageWeight,
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = " kg",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }

                        // Today's weight input
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Today",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                OutlinedTextField(
                                    value = weightText,
                                    onValueChange = { newValue ->
                                        if (newValue.isEmpty() ||
                                            newValue.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$"))
                                        ) {
                                            weightText = newValue
                                            debounceJob?.cancel()
                                            debounceJob = scope.launch {
                                                delay(1000)
                                                newValue.toDoubleOrNull()?.let { weight ->
                                                    onWeightChange(weight)
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .width(120.dp)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) { /* Prevent navigation */ },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal
                                    ),
                                    singleLine = true,
                                    suffix = { Text("kg") },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                            alpha = 0.3f
                                        ),
                                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                    )
                                )

                                // Trend indicator
                                if (trendDiff != null && abs(trendDiff) > 0.001) {
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (trendDiff > 0) Icons.Default.KeyboardArrowUp
                                            else Icons.Default.KeyboardArrowDown,
                                            contentDescription = if (trendDiff > 0) "Increased" else "Decreased",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = String.format("%.1f kg", abs(trendDiff)),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // No Data State
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Let's begin your journey!",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Enter your weight",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = weightText,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty() ||
                                    newValue.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$"))
                                ) {
                                    weightText = newValue
                                    debounceJob?.cancel()
                                    debounceJob = scope.launch {
                                        delay(1000)
                                        newValue.toDoubleOrNull()?.let { weight ->
                                            onWeightChange(weight)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .width(150.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { /* Prevent navigation */ },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            ),
                            singleLine = true,
                            suffix = { Text("kg") },
                            placeholder = { Text("0.00") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                    alpha = 0.3f
                                ),
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }
        }
    }
}
