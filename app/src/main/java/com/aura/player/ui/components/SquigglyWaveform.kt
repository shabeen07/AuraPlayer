package com.aura.player.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.aura.player.theme.AuraSecondary
import com.aura.player.theme.AuraSecondaryContainer
import com.aura.player.theme.AuraSurfaceVariant
import kotlin.math.sin

@Composable
fun SquigglyWaveform(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var widthPx by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
                    onProgressChange(newProgress)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val newProgress = (change.position.x / size.width).coerceIn(0f, 1f)
                    onProgressChange(newProgress)
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        ) {
            widthPx = size.width
            val midY = size.height / 2f
            val activeWidth = size.width * progress

            // Inactive trailing flat track
            if (activeWidth < size.width) {
                drawLine(
                    color = AuraSurfaceVariant,
                    start = Offset(activeWidth, midY),
                    end = Offset(size.width, midY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Active leading squiggly wave track
            if (activeWidth > 0) {
                val wavePath = Path()
                wavePath.moveTo(0f, midY)
                val wavelength = 24.dp.toPx()
                val amplitude = 5.dp.toPx()

                var x = 0f
                val step = 2f
                while (x <= activeWidth) {
                    val y = midY + amplitude * sin((x / wavelength) * 2 * Math.PI.toFloat())
                    wavePath.lineTo(x, y)
                    x += step
                }

                drawPath(
                    path = wavePath,
                    color = AuraSecondaryContainer,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        // Neon scrubber capsule thumb head
        Box(
            modifier = Modifier
                .offset {
                    val thumbX = (widthPx * progress - 7.dp.toPx()).coerceAtLeast(0f).toInt()
                    IntOffset(thumbX, 0)
                }
                .size(width = 14.dp, height = 24.dp)
                .shadow(8.dp, RoundedCornerShape(7.dp), ambientColor = AuraSecondary, spotColor = AuraSecondary)
                .clip(RoundedCornerShape(7.dp))
                .background(AuraSecondary)
        )
    }
}
