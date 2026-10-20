package com.aura.player.ui.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aura.player.data.DataRepository
import com.aura.player.theme.AuraSurface
import com.aura.player.ui.components.AuraBottomNav
import com.aura.player.ui.components.AuraHeader
import com.aura.player.ui.components.AuraTab
import com.aura.player.ui.components.MiniPlayerDock
import com.aura.player.ui.components.modals.CastPickerSheet
import com.aura.player.ui.components.modals.ProfileDiagnosticsSheet
import com.aura.player.ui.components.modals.QuickSearchSheet
import com.aura.player.ui.home.HomeScreen
import com.aura.player.ui.library.LibraryScreen
import com.aura.player.ui.settings.SettingsScreen
import com.aura.player.ui.vault.VaultScreen

@Composable
fun MainShellScreen(
    repository: DataRepository,
    onNavigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AuraTab.HOME) }
    var showSearchSheet by remember { mutableStateOf(false) }
    var showCastSheet by remember { mutableStateOf(false) }
    var showProfileSheet by remember { mutableStateOf(false) }

    val subtitle = when (selectedTab) {
        AuraTab.HOME -> "Home"
        AuraTab.VAULT -> "Vault"
        AuraTab.LIBRARY -> "Library"
        AuraTab.SETTINGS -> "Settings & EQ"
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AuraSurface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar Header
            AuraHeader(
                subtitle = subtitle,
                modifier = Modifier.statusBarsPadding(),
                onSearchClick = { showSearchSheet = true },
                onCastClick = { showCastSheet = true },
                onProfileClick = { showProfileSheet = true }
            )

            // Content Area based on selected tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220)) togetherWith
                                fadeOut(animationSpec = tween(180))
                    },
                    label = "tabTransition",
                    modifier = Modifier.fillMaxSize()
                ) { targetTab ->
                    when (targetTab) {
                        AuraTab.HOME -> {
                            HomeScreen(
                                repository = repository,
                                onNavigateToPlayer = onNavigateToPlayer,
                                onNavigateToVault = { selectedTab = AuraTab.VAULT },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        AuraTab.LIBRARY -> {
                            LibraryScreen(
                                repository = repository,
                                onPlayItem = onNavigateToPlayer,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        AuraTab.VAULT -> {
                            VaultScreen(
                                repository = repository,
                                onPlayItem = onNavigateToPlayer,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        AuraTab.SETTINGS -> {
                            SettingsScreen(
                                repository = repository,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                // Persistent Floating Mini-Player Dock
                MiniPlayerDock(
                    repository = repository,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 4.dp),
                    onPlayerClick = onNavigateToPlayer
                )
            }

            // Fixed Bottom Navigation Bar
            AuraBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        // Header Action Sheets
        if (showSearchSheet) {
            QuickSearchSheet(
                repository = repository,
                onMediaSelected = {
                    repository.playMedia(it)
                    onNavigateToPlayer()
                },
                onDismiss = { showSearchSheet = false }
            )
        }

        if (showCastSheet) {
            CastPickerSheet(onDismiss = { showCastSheet = false })
        }

        if (showProfileSheet) {
            ProfileDiagnosticsSheet(onDismiss = { showProfileSheet = false })
        }
    }
}
