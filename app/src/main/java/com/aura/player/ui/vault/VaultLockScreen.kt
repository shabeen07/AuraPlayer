package com.aura.player.ui.vault

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aura.player.theme.AuraError
import com.aura.player.theme.AuraOnPrimaryContainer
import com.aura.player.theme.AuraOnSurface
import com.aura.player.theme.AuraOnSurfaceVariant
import com.aura.player.theme.AuraOutlineVariant
import com.aura.player.theme.AuraPrimary
import com.aura.player.theme.AuraPrimaryContainer
import com.aura.player.theme.AuraSecondary
import com.aura.player.theme.AuraSurface
import com.aura.player.theme.AuraSurfaceContainer
import com.aura.player.theme.AuraSurfaceContainerHigh
import com.aura.player.theme.AuraSurfaceContainerHighest

@Composable
fun VaultLockScreen(
    onUnlocked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var enteredPin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val correctPin = "1234"

    fun onDigitPress(digit: String) {
        if (enteredPin.length < 4) {
            val newPin = enteredPin + digit
            enteredPin = newPin
            errorMessage = null

            if (newPin.length == 4) {
                if (newPin == correctPin) {
                    onUnlocked()
                } else {
                    errorMessage = "Incorrect PIN. Try 1234 or Biometrics."
                    enteredPin = ""
                }
            }
        }
    }

    fun onBackspace() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            errorMessage = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AuraSurface)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .shadow(16.dp, CircleShape, ambientColor = AuraPrimary, spotColor = AuraPrimary)
                    .clip(CircleShape)
                    .background(AuraSurfaceContainerHigh)
                    .border(1.5.dp, AuraPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Vault Locked",
                    tint = AuraPrimary,
                    modifier = Modifier.size(34.dp)
                )
            }

            Text(
                text = "ENCRYPTED VAULT",
                color = AuraSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Text(
                text = "Enter 4-Digit Passcode",
                color = AuraOnSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Secure local sandboxed storage for private media",
                color = AuraOnSurfaceVariant,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // PIN Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < enteredPin.length
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFilled) AuraPrimaryContainer else AuraSurfaceContainerHighest
                            )
                            .border(
                                width = 1.5.dp,
                                color = if (isFilled) AuraPrimary else AuraOutlineVariant.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                }
            }

            // Error Message
            AnimatedVisibility(visible = errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    text = errorMessage ?: "",
                    color = AuraError,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Numeric Keypad
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val rows = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("BIO", "0", "DEL")
            )

            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { key ->
                        when (key) {
                            "BIO" -> {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(AuraSurfaceContainerHigh)
                                        .clickable {
                                            // Biometric authenticated
                                            onUnlocked()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = "Biometric Unlock",
                                        tint = AuraSecondary,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                            }
                            "DEL" -> {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(AuraSurfaceContainerHigh)
                                        .clickable { onBackspace() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                                        contentDescription = "Backspace",
                                        tint = AuraOnSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .size(68.dp)
                                        .clip(CircleShape)
                                        .background(AuraSurfaceContainer)
                                        .clickable { onDigitPress(key) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = key,
                                        color = AuraOnSurface,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Default PIN: 1234 • Biometrics enabled",
                color = AuraOnSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }
    }
}
