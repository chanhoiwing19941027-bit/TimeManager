package com.example.timemanager.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timemanager.ui.theme.Indigo500
import com.example.timemanager.ui.theme.Slate800

@Composable
fun CircularTimer(
    modifier: Modifier = Modifier,
    progress: Float, // 0.0 to 1.0
    timeString: String,
    statusLabel: String,
    accentColor: Color = Indigo500,
    size: Dp = 240.dp,
    strokeWidth: Dp = 12.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "timerProgress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val strokePx = strokeWidth.toPx()
            val radius = (size.toPx() - strokePx) / 2

            // 1. Background Ring
            drawCircle(
                color = Slate800,
                radius = radius,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // 2. Animated Progress Ring with Gradient
            if (animatedProgress > 0f) {
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.6f),
                            accentColor
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = animatedProgress * 360f,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        // Inside layout (Time display)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = statusLabel.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = accentColor.copy(alpha = 0.9f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Text(
                text = timeString,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
