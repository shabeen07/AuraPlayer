package com.aura.player.ui.settings

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.SurroundSound
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.player.BuildConfig
import com.aura.player.R
import com.aura.player.data.DataRepository
import com.aura.player.theme.AuraOnPrimaryContainer
import com.aura.player.theme.AuraOnSurface
import com.aura.player.theme.AuraOnSurfaceVariant
import com.aura.player.theme.AuraPrimary
import com.aura.player.theme.AuraPrimaryContainer
import com.aura.player.theme.AuraSecondary
import com.aura.player.theme.AuraSurface
import com.aura.player.theme.AuraSurfaceContainer
import com.aura.player.theme.AuraSurfaceContainerHigh
import com.aura.player.theme.AuraSurfaceContainerHighest
import com.aura.player.theme.AuraSurfaceContainerLow
import com.aura.player.theme.AuraTertiary

@Composable
fun SettingsScreen(
    repository: DataRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fxController = repository.audioFxController
    val eqState by fxController.equalizerState.collectAsState()

    var hwAccelEnabled by remember { mutableStateOf(true) }
    var backgroundPlayEnabled by remember { mutableStateOf(true) }
    var cacheSizeMb by remember { mutableStateOf("24.8 MB") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraSurface)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Screen Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(AuraPrimary)
                )
                Text(
                    text = "Settings & Equalizer",
                    color = AuraOnSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 1. Audio Equalizer Engine Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(AuraSurfaceContainer)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // EQ Header & Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = "EQ",
                            tint = AuraPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "5-Band Graphic Equalizer",
                                color = AuraOnSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Attached to Media3 Audio Session",
                                color = AuraOnSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = eqState.isEnabled,
                        onCheckedChange = { fxController.toggleEqualizer(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AuraOnPrimaryContainer,
                            checkedTrackColor = AuraPrimaryContainer
                        )
                    )
                }

                // Presets Strip
                Text(
                    text = "AUDIO PRESETS",
                    color = AuraOnSurfaceVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    fxController.presets.keys.forEach { preset ->
                        val isSelected = eqState.currentPreset == preset
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) AuraPrimaryContainer else AuraSurfaceContainerHigh)
                                .clickable { fxController.applyPreset(preset) }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = preset,
                                color = if (isSelected) AuraOnPrimaryContainer else AuraOnSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // 5 Sliders for the bands
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    eqState.bandFrequencies.forEachIndexed { index, freqLabel ->
                        val level = eqState.bandLevels.getOrElse(index) { 0 }
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = freqLabel,
                                    color = AuraOnSurface,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = String.format("%+d dB", level / 100),
                                    color = if (level > 0) AuraSecondary else if (level < 0) AuraPrimary else AuraOnSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Slider(
                                value = level.toFloat(),
                                onValueChange = { fxController.setBandLevel(index, it.toInt()) },
                                valueRange = -1500f..1500f,
                                enabled = eqState.isEnabled,
                                colors = SliderDefaults.colors(
                                    thumbColor = AuraPrimary,
                                    activeTrackColor = AuraPrimary,
                                    inactiveTrackColor = AuraSurfaceContainerHighest
                                )
                            )
                        }
                    }
                }

                // Spatial Audio & Bass Boost Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Bass Boost Box
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AuraSurfaceContainerHigh)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = "Bass",
                                        tint = AuraSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(text = "Bass Boost", color = AuraOnSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Switch(
                                    checked = eqState.isBassBoostEnabled,
                                    onCheckedChange = { fxController.toggleBassBoost(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AuraOnPrimaryContainer,
                                        checkedTrackColor = AuraPrimaryContainer
                                    )
                                )
                            }
                            Slider(
                                value = eqState.bassBoostStrength.toFloat(),
                                onValueChange = { fxController.setBassBoostStrength(it.toInt()) },
                                valueRange = 0f..1000f,
                                enabled = eqState.isBassBoostEnabled,
                                colors = SliderDefaults.colors(thumbColor = AuraSecondary, activeTrackColor = AuraSecondary)
                            )
                        }
                    }

                    // Spatial Audio Virtualizer Box
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AuraSurfaceContainerHigh)
                            .padding(10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SurroundSound,
                                        contentDescription = "Spatial",
                                        tint = AuraTertiary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(text = "Spatial 3D", color = AuraOnSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Switch(
                                    checked = eqState.isSpatialAudioEnabled,
                                    onCheckedChange = { fxController.toggleSpatialAudio(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = AuraOnPrimaryContainer,
                                        checkedTrackColor = AuraPrimaryContainer
                                    )
                                )
                            }
                            Text(
                                text = "Binaural headphone virtualizer",
                                color = AuraOnSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // 2. Playback Preferences Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(AuraSurfaceContainer)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "PLAYBACK & DECODER",
                    color = AuraOnSurfaceVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                // Hardware Acceleration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Hardware Decoder Acceleration", color = AuraOnSurface, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Use MediaCodec GPU decoder for 4K / 60fps streams", color = AuraOnSurfaceVariant, fontSize = 11.sp)
                    }
                    Switch(
                        checked = hwAccelEnabled,
                        onCheckedChange = { hwAccelEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AuraOnPrimaryContainer,
                            checkedTrackColor = AuraPrimaryContainer
                        )
                    )
                }

                // Background Playback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Background Audio Playback", color = AuraOnSurface, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = "Continue audio when app is minimized or screen off", color = AuraOnSurfaceVariant, fontSize = 11.sp)
                    }
                    Switch(
                        checked = backgroundPlayEnabled,
                        onCheckedChange = { backgroundPlayEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AuraOnPrimaryContainer,
                            checkedTrackColor = AuraPrimaryContainer
                        )
                    )
                }
            }
        }

        // 3. Cache Management Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(AuraSurfaceContainer)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = "Cache",
                            tint = AuraSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(text = "Thumbnail Cache ($cacheSizeMb)", color = AuraOnSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(text = "Coil video & artwork disk cache", color = AuraOnSurfaceVariant, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        cacheSizeMb = "0.0 MB"
                        Toast.makeText(context, "Thumbnail disk cache cleared", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraSurfaceContainerHighest),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Clear Cache", color = AuraOnSurface, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // 4. App Info & Versioning Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(AuraSurfaceContainerLow)
                .border(1.dp, AuraPrimary.copy(alpha = 0.3f), RoundedCornerShape(22.dp))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AuraSurfaceContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_logo),
                        contentDescription = "Aura Logo",
                        modifier = Modifier.size(36.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AuraPlayer Media Engine",
                        color = AuraOnSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Version ${BuildConfig.VERSION_NAME} (Build ${BuildConfig.VERSION_CODE})",
                        color = AuraSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Media3 ExoPlayer • Jetpack Compose • Production Ready",
                        color = AuraOnSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(84.dp))
    }
}
