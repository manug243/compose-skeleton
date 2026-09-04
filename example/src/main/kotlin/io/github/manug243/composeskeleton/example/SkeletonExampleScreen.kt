package io.github.manug243.composeskeleton.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.manug243.composeskeleton.SkeletonDefaults
import io.github.manug243.composeskeleton.SkeletonDirection
import io.github.manug243.composeskeleton.SkeletonHost
import io.github.manug243.composeskeleton.SkeletonMode
import io.github.manug243.composeskeleton.skeleton
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkeletonExampleScreen() {
    var loading by rememberSaveable { mutableStateOf(true) }
    var animationEnabled by rememberSaveable { mutableStateOf(true) }
    var direction by rememberSaveable { mutableStateOf(SkeletonDirection.LeftToRight) }
    var animationDurationMillis by rememberSaveable { mutableStateOf(SkeletonDefaults.AnimationDurationMillis) }
    var speedSliderPosition by rememberSaveable {
        mutableStateOf(durationToSpeedPosition(SkeletonDefaults.AnimationDurationMillis))
    }
    var paletteIndex by rememberSaveable { mutableStateOf(0) }
    val palette = SkeletonPalette.entries[paletteIndex]
    val style = SkeletonDefaults.style(
        baseColor = palette.baseColor,
        highlightColor = palette.highlightColor,
        direction = direction,
        animationDurationMillis = animationDurationMillis,
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Compose Skeleton") })
        },
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            ControlRow(
                title = "Loading",
                checked = loading,
                onCheckedChange = { loading = it },
            )
            ControlRow(
                title = "Animate shimmer",
                checked = animationEnabled,
                onCheckedChange = { animationEnabled = it },
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val selectedDuration = durationFromSpeedPosition(speedSliderPosition)
                Text("Speed", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${selectedDuration} ms per sweep",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Slider(
                    value = speedSliderPosition,
                    onValueChange = { speedSliderPosition = it },
                    onValueChangeFinished = {
                        animationDurationMillis = durationFromSpeedPosition(speedSliderPosition)
                    },
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Slower", style = MaterialTheme.typography.labelMedium)
                    Text("Faster", style = MaterialTheme.typography.labelMedium)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SpeedPreset.entries.forEach { preset ->
                        val selected = selectedDuration == preset.durationMillis
                        val selectPreset = {
                            animationDurationMillis = preset.durationMillis
                            speedSliderPosition = durationToSpeedPosition(preset.durationMillis)
                        }
                        if (selected) {
                            FilledTonalButton(onClick = selectPreset) { Text(preset.label) }
                        } else {
                            TextButton(onClick = selectPreset) {
                                Text(preset.label)
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Colors", style = MaterialTheme.typography.titleMedium)
                TextButton(
                    onClick = { paletteIndex = (paletteIndex + 1) % SkeletonPalette.entries.size },
                ) {
                    Text(palette.label)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Direction", style = MaterialTheme.typography.titleMedium)
                TextButton(
                    onClick = {
                        direction = when (direction) {
                            SkeletonDirection.LeftToRight -> SkeletonDirection.RightToLeft
                            SkeletonDirection.RightToLeft -> SkeletonDirection.TopToBottom
                            SkeletonDirection.TopToBottom -> SkeletonDirection.BottomToTop
                            SkeletonDirection.BottomToTop -> SkeletonDirection.LeftToRight
                        }
                    },
                ) {
                    Text(direction.name)
                }
            }

            SkeletonHost(
                enabled = loading,
                animationEnabled = animationEnabled,
                style = style,
                modifier = Modifier.fillMaxWidth(),
            ) {
                ArticleCard()
            }

            Text(
                text = "The card outline and status stay visible. The image and text rows keep " +
                    "their measured size while drawing shimmer or solid placeholders.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private const val SlowestDurationMillis: Int = 3_000
private const val FastestDurationMillis: Int = 300

private enum class SpeedPreset(
    val label: String,
    val durationMillis: Int,
) {
    Slow("Slow", 2_000),
    Normal("Normal", SkeletonDefaults.AnimationDurationMillis),
    Fast("Fast", 700),
}

private fun durationToSpeedPosition(durationMillis: Int): Float =
    (SlowestDurationMillis - durationMillis).toFloat() /
        (SlowestDurationMillis - FastestDurationMillis)

private fun durationFromSpeedPosition(speedPosition: Float): Int =
    (SlowestDurationMillis -
        (SlowestDurationMillis - FastestDurationMillis) * speedPosition.coerceIn(0f, 1f)).roundToInt()

private enum class SkeletonPalette(
    val label: String,
    val baseColor: Color,
    val highlightColor: Color,
) {
    Theme("Theme default", Color.Unspecified, Color.Unspecified),
    Ocean("Ocean", Color(0xFF1F3A4D), Color(0xFF75D5F0)),
    Violet("Violet", Color(0xFF3F315C), Color(0xFFCBB5FF)),
    Warm("Warm", Color(0xFF5A3D31), Color(0xFFFFC8A5)),
}

@Composable
private fun ControlRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ArticleCard() {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .skeleton(SkeletonMode.Keep),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF315DA8), Color(0xFF8C5BB5)),
                    ),
                )
                .skeleton(
                    mode = SkeletonMode.Shimmer,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Compose",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }

        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                modifier = Modifier.skeleton(SkeletonMode.Keep),
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                Text(
                    text = "CARD SHELL: KEEP",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Text(
                text = "A skeleton that follows your real Compose layout",
                modifier = Modifier
                    .fillMaxWidth(0.86f)
                    .skeleton(SkeletonMode.Shimmer),
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
            )
            Text(
                text = "Solid placeholders work well for secondary content.",
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .skeleton(SkeletonMode.Solid),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
            )
            Text(
                text = "Each row independently chooses whether it should animate.",
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .skeleton(SkeletonMode.Shimmer),
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 420)
@Composable
private fun SkeletonExamplePreview() {
    ComposeSkeletonTheme {
        SkeletonExampleScreen()
    }
}
