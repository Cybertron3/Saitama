package com.ankitesh.saitama.ui.cig

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
fun CigTrackerScreen(
    viewModel: CigViewModel,
    openTargetDialog: Boolean = false,
    onNavigateBack: () -> Unit
) {
    val target by viewModel.cigTarget.collectAsState()
    val allEntries by viewModel.allEntries.collectAsState()
    val sixMonthEntries by viewModel.sixMonthEntries.collectAsState()

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showEntryDialog by remember { mutableStateOf(false) }
    var showTargetDialog by remember { mutableStateOf(openTargetDialog) }

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(36) }
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
            .padding(horizontal = 16.dp, vertical = if (isCompactScreen) 8.dp else 12.dp)
    ) {
        // Back button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        // Target Selector Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(if (isCompactScreen) 0.22f else 0.20f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            TargetSelector(
                currentTarget = target,
                onSaveTarget = { viewModel.saveTarget(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(if (isCompactScreen) 8.dp else 12.dp))

        // Graph Section
        CigGraphCard(
            cigData = sixMonthEntries,
            modifier = Modifier.weight(if (isCompactScreen) 0.28f else 0.30f),
            isCompact = isCompactScreen
        )

        Spacer(modifier = Modifier.height(if (isCompactScreen) 8.dp else 12.dp))

        // Calendar Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(if (isCompactScreen) 0.50f else 0.50f)
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
                    CigDay(
                        day = day,
                        count = allEntries[Date.from(day.date.atStartOfDay(ZoneId.systemDefault()).toInstant())],
                        target = target ?: 0,
                        isSelected = selectedDate == day.date,
                        isDarkTheme = isDarkTheme,
                        onClick = {
                            if (day.position == DayPosition.MonthDate &&
                                day.date <= LocalDate.now()) {
                                selectedDate = day.date
                                showEntryDialog = true
                            }
                        }
                    )
                }
            )
        }
    }

    // Entry Dialog
    if (showEntryDialog && selectedDate != null) {
        CigEntryDialog(
            date = selectedDate!!,
            viewModel = viewModel,
            onDismiss = { showEntryDialog = false }
        )
    }

    // Target Dialog (when opened from homepage nudge)
    if (showTargetDialog) {
        TargetDialog(
            currentTarget = target,
            onSave = {
                viewModel.saveTarget(it)
                showTargetDialog = false
            },
            onDismiss = { showTargetDialog = false }
        )
    }
}

@Composable
fun TargetSelector(
    currentTarget: Int?,
    onSaveTarget: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var isEditMode by remember { mutableStateOf(currentTarget == null) }
    var sliderValue by remember(currentTarget) {
        mutableFloatStateOf(currentTarget?.toFloat() ?: 5f)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = "Daily Target",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isEditMode) {
            // Edit Mode - Show slider
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 0f..10f,
                        steps = 9,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "10",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${sliderValue.toInt()}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cancel and Save buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        // Reset slider to current target value
                        sliderValue = currentTarget?.toFloat() ?: 5f
                        if (currentTarget != null) {
                            isEditMode = false
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    )
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        onSaveTarget(sliderValue.toInt())
                        isEditMode = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Text("Save")
                }
            }
        } else {
            // View Mode - Show current value
            if (currentTarget != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "$currentTarget",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { isEditMode = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Text("Edit")
                }
            } else {
                // No target set
                Text(
                    text = "Not set yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { isEditMode = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Text("Set Target")
                }
            }
        }
    }
}

@Composable
fun CigDay(
    day: CalendarDay,
    count: Int?,
    target: Int,
    isSelected: Boolean,
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    val isToday = day.date == LocalDate.now()
    val isFuture = day.date > LocalDate.now()

    // Determine background color based on count vs target
    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.secondaryContainer
        count != null && count <= target -> Color(0xFF4CAF50).copy(alpha = 0.3f) // Green
        count != null && count > target -> Color(0xFFF44336).copy(alpha = 0.3f) // Red
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color = backgroundColor)
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
                fontSize = 11.sp,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )

            if (count != null && day.position == DayPosition.MonthDate) {
                Text(
                    text = "$count",
                    color = when {
                        isSelected -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    },
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CigEntryDialog(
    date: LocalDate,
    viewModel: CigViewModel,
    onDismiss: () -> Unit
) {
    var count by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(date) {
        count = viewModel.getEntryForDate(
            Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
        )
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

                // Counter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilledIconButton(
                        onClick = {
                            if (count > 0) {
                                count--
                                scope.launch {
                                    viewModel.saveEntry(
                                        Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                                        count
                                    )
                                }
                            }
                        },
                        enabled = count > 0,
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Text(
                            text = "−",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(width = 80.dp, height = 56.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$count",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    FilledIconButton(
                        onClick = {
                            if (count < 99) {
                                count++
                                scope.launch {
                                    viewModel.saveEntry(
                                        Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                                        count
                                    )
                                }
                            }
                        },
                        enabled = count < 99,
                        shape = CircleShape,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
fun TargetDialog(
    currentTarget: Int?,
    onSave: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var sliderValue by remember {
        mutableFloatStateOf(currentTarget?.toFloat() ?: 5f)
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
                    text = "Set Your Daily Target",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "0",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = sliderValue,
                        onValueChange = { sliderValue = it },
                        valueRange = 0f..10f,
                        steps = 9,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp)
                    )
                    Text(
                        text = "10",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Text(
                    text = "${sliderValue.toInt()}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Button(onClick = { onSave(sliderValue.toInt()) }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
