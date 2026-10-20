package com.aura.player

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.aura.player.data.DefaultDataRepository
import com.aura.player.ui.main.MainShellScreen
import com.aura.player.ui.player.PlayerScreen

@Composable
fun MainNavigation() {
    val context = LocalContext.current.applicationContext
    val repository = remember { DefaultDataRepository(context) }
    val backStack = rememberNavBackStack(HomeNavKey)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<HomeNavKey> {
                MainShellScreen(
                    repository = repository,
                    onNavigateToPlayer = { backStack.add(PlayerNavKey) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            entry<PlayerNavKey> {
                PlayerScreen(
                    repository = repository,
                    onNavigateBack = { backStack.removeLastOrNull() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    )
}
