package com.ankitesh.saitama.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CigTile(
    todayCount: Int,
    target: Int?,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onNavigateToDetail: () -> Unit,
    onSetTarget: () -> Unit,
    modifier: Modifier = Modifier
) {
    val severityMessage = remember(todayCount, target) {
        getSeverityMessage(todayCount, target)
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
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
                        text = "CIGARETTES",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Go to details",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Divider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.2f)
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Set target nudge (if no target set)
                    if (target == null) {
                        Row(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { onSetTarget() },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Set your daily target →",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Today's count
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (target != null) "$todayCount / $target" else "$todayCount",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    // Severity message
                    if (target != null || todayCount == 0) {
                        Text(
                            text = "\"$severityMessage\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Counter buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { /* Prevent navigation when tapping counter area */ }
                    ) {
                        // Decrement button
                        FilledIconButton(
                            onClick = onDecrement,
                            enabled = todayCount > 0,
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Text(
                                text = "−",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Count display
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(width = 64.dp, height = 48.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$todayCount",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Increment button
                        FilledIconButton(
                            onClick = onIncrement,
                            enabled = todayCount < 99,
                            shape = CircleShape,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                                disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase"
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getSeverityMessage(count: Int, target: Int?): String {
    val effectiveTarget = target ?: 0

    // Zero-Zero case
    if (effectiveTarget == 0 && count == 0) {
        return listOf(
            "Stay strong, you can do it!",
            "You're crushing it!",
            "Keep going!",
            "One day at a time!",
            "You've got this!"
        ).random()
    }

    // Over limit
    if (count > effectiveTarget) {
        return listOf(
            "Resist! You're stronger than you know",
            "Stay strong, tomorrow is new",
            "You've got this, pause and breathe",
            "Take a moment, you can do this",
            "Every moment is a fresh start"
        ).random()
    }

    // At limit
    if (count == effectiveTarget) {
        return listOf(
            "You've hit your limit!",
            "Hold steady!",
            "That's your quota for today",
            "Stay strong from here!",
            "Great discipline!"
        ).random()
    }

    // Calculate percentage
    val percentage = if (effectiveTarget > 0) (count.toFloat() / effectiveTarget) * 100 else 0f

    // Caution (50-99%)
    if (percentage >= 50) {
        return listOf(
            "You can light one, stay mindful",
            "Going well, be aware",
            "Pace yourself",
            "Stay conscious of your choices",
            "You're doing okay, stay alert"
        ).random()
    }

    // Safe (< 50%)
    return listOf(
        "Great start! Keep it up!",
        "You're doing amazing!",
        "Stay on track!",
        "Excellent progress!",
        "Keep up the good work!"
    ).random()
}
