package com.waystone.saitama.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun WeightGraphCard(
    weightData: List<Pair<Date, Double>>,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val isDarkTheme = isSystemInDarkTheme()

    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 10.dp else 12.dp)
        ) {
            // Title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isCompact) 6.dp else 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weight Trend",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Last 6 months",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (weightData.size >= 2) {
                // Calculate statistics
                val weights = weightData.map { it.second }
                val minWeight = weights.minOrNull() ?: 0.0
                val maxWeight = weights.maxOrNull() ?: 100.0
                val latestWeight = weightData.lastOrNull()?.second ?: 0.0
                val firstWeight = weightData.firstOrNull()?.second ?: 0.0
                val change = latestWeight - firstWeight

                // Stats row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (isCompact) 6.dp else 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    WeightStat(
                        label = "Current",
                        value = String.format("%.1f kg", latestWeight),
                        color = MaterialTheme.colorScheme.primary
                    )
                    WeightStat(
                        label = "Change",
                        value = String.format("%+.1f kg", change),
                        color = if (change <= 0) Color(0xFF4CAF50) else Color(0xFFFF6F00)
                    )
                    WeightStat(
                        label = "Range",
                        value = String.format("%.1f-%.1f", minWeight, maxWeight),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .heightIn(
                            min = if (isCompact) 80.dp else 100.dp,
                            max = if (isCompact) 140.dp else 180.dp
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    SimpleWeightChart(
                        weightData = weightData,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Date range label
                if (weightData.isNotEmpty()) {
                    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = dateFormat.format(weightData.first().first),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = dateFormat.format(weightData.last().first),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else if (weightData.size == 1) {
                // Single data point
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .heightIn(
                            min = if (isCompact) 80.dp else 100.dp,
                            max = if (isCompact) 140.dp else 180.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Only one weight entry",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = String.format("%.1f kg", weightData[0].second),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(
                                weightData[0].first
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else {
                // No data message
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .heightIn(
                            min = if (isCompact) 80.dp else 100.dp,
                            max = if (isCompact) 140.dp else 180.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Start tracking your weight to see trends",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeightStat(
    label: String,
    value: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Composable
private fun SimpleWeightChart(
    weightData: List<Pair<Date, Double>>,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val lineColor = if (isDarkTheme) Color(0xFF00E5CC) else Color(0xFF00BFA5)
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val dotColor = if (isDarkTheme) Color(0xFF00D9C4) else Color(0xFF00BFA5)

    Canvas(modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        if (weightData.size < 2) return@Canvas

        val weights = weightData.map { it.second }
        val minWeight = weights.minOrNull() ?: return@Canvas
        val maxWeight = weights.maxOrNull() ?: return@Canvas
        val weightRange = maxWeight - minWeight

        // Add some padding to the weight range
        val paddedMin = minWeight - (weightRange * 0.1).coerceAtLeast(1.0)
        val paddedMax = maxWeight + (weightRange * 0.1).coerceAtLeast(1.0)
        val paddedRange = paddedMax - paddedMin

        val width = size.width
        val height = size.height
        val xStep = width / (weightData.size - 1).coerceAtLeast(1)

        // Draw horizontal grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = height * (i.toFloat() / gridLines)
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }

        // Create the path for the line
        val path = Path()
        val points = mutableListOf<Offset>()

        weightData.forEachIndexed { index, (_, weight) ->
            val x = index * xStep
            val y = height - ((weight - paddedMin) / paddedRange * height).toFloat()
            val point = Offset(x, y)
            points.add(point)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        // Draw the gradient fill under the line
        val fillPath = Path()
        fillPath.addPath(path)
        fillPath.lineTo(width, height)
        fillPath.lineTo(0f, height)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    lineColor.copy(alpha = 0.3f),
                    lineColor.copy(alpha = 0.0f)
                ),
                startY = 0f,
                endY = height
            )
        )

        // Draw the line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw dots at data points
        points.forEach { point ->
            // White background circle for contrast
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = point
            )
            // Colored dot
            drawCircle(
                color = dotColor,
                radius = 4.dp.toPx(),
                center = point
            )
        }
    }
}