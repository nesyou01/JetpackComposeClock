package com.nesyou.clock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.nesyou.clock.ui.theme.ClockTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.PI
private val PI2 = Math.toDegrees(2 * PI).toFloat()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockTheme {
                Scaffold {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Clock()
                    }
                }
            }
        }
    }
}



@Composable
fun Clock(
    width: Dp = 40.dp,
    color: Color = MaterialTheme.colors.background
) {
    check(width.value > 0)
    var s by remember { mutableStateOf(15) }
    var m by remember { mutableStateOf(0) }
    var h by remember { mutableStateOf(4) }
    LaunchedEffect(Unit) {
        joinAll(
            launch {
                do {
                    delay(1000L)
                    s += 1
                } while (true)
            }, launch {
                do {
                    delay(1000L * 60)
                    m += 1
                } while (true)
            }, launch {
                do {
                    delay(1000L * 60 * 60)
                    h += 1
                } while (true)
            }
        )
    }
    val v = (1..12)
    BoxWithConstraints(
        modifier = Modifier
            .size(240.dp)
            .coloredShadow(Color.Black)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .width(maxWidth - width * 2)
                .aspectRatio(1F)
                .shadow(2.dp, CircleShape)
                .clip(CircleShape)
                .background(color)
        )
        SecondsHand(angle = (PI2 / 60) * s)
        MinutesHand((PI2 / 60) * m)
        HoursHand(angle = (PI2 / 12) * h)

        v.map {
            val angle = ((PI2 / v.toList().size) * it) + Math.toDegrees(PI / 2).toFloat()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        rotationZ = angle
                    }
            ) {
                Text(
                    "$it",
                    modifier = Modifier
                        .rotate(PI2 - angle)
                        .width(width),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun HandBody(
    modifier: Modifier,
    angle: Float,
    w: Dp = 45.dp
) {
    Row(
        Modifier
            .fillMaxWidth()
            .rotate(angle)
    ) {
        Spacer(modifier = Modifier.width(w))
        Box(
            modifier = modifier,
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.MinutesHand(
    angle: Float,
) {
    HandBody(
        angle = angle,
        modifier = Modifier
            .height(3.dp)
            .clip(CircleShape)
            .width((maxWidth / 2) - 50.dp)
            .background(MaterialTheme.colors.onBackground),
        w = 50.dp
    )
}

@Composable
private fun BoxWithConstraintsScope.HoursHand(
    angle: Float,
) {
    HandBody(
        angle = angle,
        modifier = Modifier
            .height(4.dp)
            .clip(CircleShape)
            .width((maxWidth / 2) - 65.dp)
            .background(MaterialTheme.colors.onBackground),
        w = 65.dp
    )
}

@Composable
private fun BoxWithConstraintsScope.SecondsHand(
    angle: Float,
) {
    HandBody(
        angle = angle,
        modifier = Modifier
            .height(2.dp)
            .clip(CircleShape)
            .width((maxWidth / 2) - 45.dp)
            .background(MaterialTheme.colors.primary)
            .zIndex(Float.MAX_VALUE)
    )
}

private fun Modifier.coloredShadow(
    color: Color,
    alpha: Float = .09f,
): Modifier {
    val shadowColor = color.copy(alpha = alpha).toArgb()
    val transparent = color.copy(alpha = 0f).toArgb()
    return this.drawBehind {
        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.color = transparent

            frameworkPaint.setShadowLayer(
                60F,
                5F,
                5F,
                shadowColor
            )
            it.drawRoundRect(
                0f,
                0f,
                this.size.width,
                this.size.height,
                1101F,
                1115F,
                paint
            )
        }
    }
}