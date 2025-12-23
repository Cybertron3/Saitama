package com.waystone.saitama.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun WeightTrackerScreen(
    viewModel: WeightViewModel = viewModel()
) {
    val averageWeight by viewModel.averageWeight.collectAsState()
    val allWeights by viewModel.allWeights.collectAsState()
    val sixMonthWeights by viewModel.sixMonthWeights.collectAsState()
    
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(36) } // 3 years back
    val endMonth = remember { currentMonth }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    
    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )
    
    val isDarkTheme = isSystemInDarkTheme()
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val isCompactScreen = screenHeight < 700.dp
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = if (isCompactScreen) 8.dp else 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Average Weight Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(if (isCompactScreen) 0.22f else 0.25f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isCompactScreen) 16.dp else 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Average Weight",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = if (averageWeight == "No data") averageWeight else "$averageWeight",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    if (averageWeight != "No data") {
                        Text(
                            text = " kg",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
                Text(
                    text = "Last 7 days",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(if (isCompactScreen) 8.dp else 12.dp))
        
        // Weight Graph Section
        WeightGraphCard(
            weightData = sixMonthWeights,
            modifier = Modifier.weight(if (isCompactScreen) 0.33f else 0.35f),
            isCompact = isCompactScreen
        )
        
        Spacer(modifier = Modifier.height(if (isCompactScreen) 8.dp else 12.dp))
        
        // Calendar Section with Navigation
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(if (isCompactScreen) 0.45f else 0.4f)
        ) {
            // Month Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isCompactScreen) 4.dp else 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.firstVisibleMonth.yearMonth.format(
                        DateTimeFormatter.ofPattern("MMMM yyyy")
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Calendar
            HorizontalCalendar(
                state = state,
                modifier = Modifier.fillMaxSize(),
                dayContent = { day ->
                Day(
                    day = day,
                    hasWeight = allWeights.containsKey(Date.from(day.date.atStartOfDay(ZoneId.systemDefault()).toInstant())),
                    isSelected = selectedDate == day.date,
                    isDarkTheme = isDarkTheme,
                    onClick = {
                        if (day.position == DayPosition.MonthDate && 
                            day.date <= LocalDate.now()) {
                            selectedDate = day.date
                            showDialog = true
                        }
                    }
                    )
                }
            )
        }
    }
    
    // Weight Entry Dialog
    if (showDialog && selectedDate != null) {
        WeightEntryDialog(
            date = selectedDate!!,
            viewModel = viewModel,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun Day(
    day: CalendarDay,
    hasWeight: Boolean,
    isSelected: Boolean,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    val isToday = day.date == LocalDate.now()
    val isFuture = day.date > LocalDate.now()
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                color = when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isToday -> MaterialTheme.colorScheme.secondaryContainer
                    else -> Color.Transparent
                }
            )
            .clickable(
                enabled = day.position == DayPosition.MonthDate && !isFuture,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = when {
                    isFuture -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    day.position != DayPosition.MonthDate -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.onSurface
                },
                fontSize = 12.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
            
            if (hasWeight && day.position == DayPosition.MonthDate) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Weight recorded",
                    modifier = Modifier.size(10.dp),
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary
                           else if (isDarkTheme) Color(0xFF00E5CC) // Bright teal in dark mode
                           else Color(0xFF00BFA5)  // Teal accent in light mode
                )
            }
        }
    }
}

@Composable
fun WeightEntryDialog(
    date: LocalDate,
    viewModel: WeightViewModel,
    onDismiss: () -> Unit
) {
    var weightText by remember { mutableStateOf("") }
    var hasExistingWeight by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    LaunchedEffect(date) {
        val existingWeight = viewModel.getWeightForDate(
            Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
        )
        weightText = existingWeight?.let { String.format("%.2f", it) } ?: ""
        hasExistingWeight = existingWeight != null
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { newValue ->
                        // Allow only numbers with up to 2 decimal places
                        if (newValue.isEmpty() || 
                            newValue.matches(Regex("^\\d{0,3}(\\.\\d{0,2})?$"))) {
                            weightText = newValue
                        }
                    },
                    label = { Text("Weight") },
                    suffix = { Text("kg") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Delete button on the left
                    if (hasExistingWeight) {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    viewModel.deleteWeight(
                                        Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
                                    )
                                    onDismiss()
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete")
                        }
                    } else {
                        // Empty space to maintain alignment
                        Spacer(modifier = Modifier.width(1.dp))
                    }
                    
                    // Cancel and Save buttons on the right
                    Row {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Button(
                            onClick = {
                                weightText.toDoubleOrNull()?.let { weight ->
                                    scope.launch {
                                        viewModel.saveWeight(
                                            Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                                            weight
                                        )
                                        onDismiss()
                                    }
                                }
                            },
                            enabled = weightText.isNotEmpty()
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}