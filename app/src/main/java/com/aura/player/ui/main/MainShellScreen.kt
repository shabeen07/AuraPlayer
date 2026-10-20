package com.aura.player.ui.main

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
import com.aura.player.ui.home.HomeScreen
import com.aura.player.ui.vault.VaultScreen

@Composable
fun MainShellScreen(
    repository: DataRepository,
    onNavigateToPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AuraTab.HOME) }

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
                onSearchClick = { },
                onCastClick = { },
                onProfileClick = { }
            )

            // Content Area based on selected tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (selectedTab) {
                    AuraTab.HOME -> {
                        HomeScreen(
                            repository = repository,
                            onNavigateToPlayer = onNavigateToPlayer,
                            onNavigateToVault = { selectedTab = AuraTab.VAULT },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    AuraTab.VAULT, AuraTab.LIBRARY -> {
                        VaultScreen(
                            repository = repository,
                            onPlayItem = onNavigateToPlayer,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    AuraTab.SETTINGS -> {
                        HomeScreen(
                            repository = repository,
                            onNavigateToPlayer = onNavigateToPlayer,
                            onNavigateToVault = { selectedTab = AuraTab.VAULT },
                            modifier = Modifier.fillMaxSize()
                        )
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
    }
}
