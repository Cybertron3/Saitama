package com.ankitesh.saitama.ui.cig

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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CigGraphCard(
    cigData: List<Pair<Date, Int>>,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val isDarkTheme = isSystemInDarkTheme()

    Card(
        modifier = modifier.fillMaxWidth(),
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
                    text = "Cigarette Trend",
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

            if (cigData.size >= 2) {
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
                    SimpleCigChart(
                        cigData = cigData,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Date range label
                if (cigData.isNotEmpty()) {
                    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = dateFormat.format(cigData.first().first),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = dateFormat.format(cigData.last().first),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else if (cigData.size == 1) {
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
                            text = "Only one entry",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${cigData[0].second} cigarettes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(
                                cigData[0].first
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
                        text = "Start tracking to see trends",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SimpleCigChart(
    cigData: List<Pair<Date, Int>>,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val lineColor = if (isDarkTheme) Color(0xFFFFB300) else Color(0xFFFF6F00)
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    val dotColor = if (isDarkTheme) Color(0xFFFFD700) else Color(0xFFFF6F00)

    Canvas(modifier = modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        if (cigData.size < 2) return@Canvas

        val counts = cigData.map { it.second }
        val minCount = (counts.minOrNull() ?: 0).coerceAtLeast(0)
        val maxCount = (counts.maxOrNull() ?: 10).coerceAtLeast(minCount + 1)
        val countRange = maxCount - minCount

        // Add some padding to the range
        val paddedMin = (minCount - (countRange * 0.1).toInt()).coerceAtLeast(0)
        val paddedMax = maxCount + (countRange * 0.1).toInt().coerceAtLeast(1)
        val paddedRange = (paddedMax - paddedMin).coerceAtLeast(1)

        val width = size.width
        val height = size.height
        val xStep = width / (cigData.size - 1).coerceAtLeast(1)

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

        cigData.forEachIndexed { index, (_, count) ->
            val x = index * xStep
            val y = height - ((count - paddedMin).toFloat() / paddedRange * height)
            val point = Offset(x, y.coerceIn(0f, height))
            points.add(point)

            if (index == 0) {
                path.moveTo(x, point.y)
            } else {
                path.lineTo(x, point.y)
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
