package com.example.timemanager.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.timemanager.data.database.Category
import com.example.timemanager.ui.theme.Indigo500
import com.example.timemanager.ui.theme.Slate700
import com.example.timemanager.ui.theme.Slate800

data class ChartSegment(
    val category: Category,
    val value: Float,       // Duration (minutes or seconds)
    val percentage: Float   // 0.0 to 1.0
)

@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    segments: List<ChartSegment>,
    size: Dp = 180.dp,
    strokeWidth: Dp = 22.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val canvasSize = size.toPx()
            val radius = (canvasSize - strokePx) / 2
            val centerOffset = Offset(canvasSize / 2, canvasSize / 2)

            if (segments.isEmpty() || segments.all { it.value == 0f }) {
                // Empty state ring
                drawArc(
                    color = Slate800,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokePx),
                    topLeft = Offset(strokePx / 2, strokePx / 2),
                    size = Size(canvasSize - strokePx, canvasSize - strokePx)
                )
                return@Canvas
            }

            var startAngle = -90f
            segments.forEach { segment ->
                if (segment.percentage > 0f) {
                    val sweepAngle = segment.percentage * 360f
                    val color = Color(android.graphics.Color.parseColor(segment.category.colorHex))
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokePx),
                        topLeft = Offset(strokePx / 2, strokePx / 2),
                        size = Size(canvasSize - strokePx, canvasSize - strokePx)
                    )
                    startAngle += sweepAngle
                }
            }
        }
    }
}

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    data: List<Float>, // Values in minutes
    labels: List<String>,
    barColor: Color = Indigo500,
    height: Dp = 150.dp
) {
    Box(
        modifier = modifier
            .size(width = 300.dp, height = height),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val paddingLeftRight = 16f
            val textHeight = 40f
            val chartHeight = canvasHeight - textHeight
            val chartWidth = canvasWidth - (paddingLeftRight * 2)

            val maxVal = (data.maxOrNull() ?: 0f).coerceAtLeast(10f) // Minimum scale ceiling of 10m
            val barCount = data.size
            val barSpacing = chartWidth / (barCount * 1.5f)
            val barWidth = (chartWidth - (barSpacing * (barCount - 1))) / barCount

            // Paint for text label drawing via Native Canvas
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#94A3B8") // Slate400 hex
                textSize = 28f
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }

            data.forEachIndexed { index, value ->
                // Bar Height ratio
                val valRatio = value / maxVal
                val currentBarHeight = chartHeight * valRatio
                val xOffset = paddingLeftRight + (index * (barWidth + barSpacing))
                val yOffset = chartHeight - currentBarHeight

                // Draw Bar Background slot
                drawRoundRect(
                    color = Slate800,
                    topLeft = Offset(xOffset, 0f),
                    size = Size(barWidth, chartHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // Draw Active Bar
                if (currentBarHeight > 0) {
                    drawRoundRect(
                        color = barColor,
                        topLeft = Offset(xOffset, yOffset),
                        size = Size(barWidth, currentBarHeight),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                }

                // Draw Label underneath
                val label = labels.getOrNull(index) ?: ""
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    xOffset + (barWidth / 2),
                    canvasHeight - 10f,
                    paint
                )
            }
        }
    }
}
