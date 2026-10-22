package com.aura.player

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aura.player.data.DefaultDataRepository
import com.aura.player.ui.main.MainShellScreen
import com.aura.player.ui.player.PlayerScreen
import com.aura.player.ui.splash.SplashScreen

@Composable
fun MainNavigation(
    appViewModel: AppViewModel = viewModel()
) {
    val repository = appViewModel.repository
    val backStack = rememberNavBackStack(SplashNavKey)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<SplashNavKey> {
                SplashScreen(
                    onSplashFinished = {
                        backStack.remove(SplashNavKey)
                        backStack.add(HomeNavKey)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
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
        },
        transitionSpec = {
            if (targetState.key == PlayerNavKey) {
                // Expanding player: slide up from bottom with subtle fade
                (slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(350)
                ) + fadeIn(animationSpec = tween(300))) togetherWith
                        fadeOut(animationSpec = tween(250))
            } else {
                // Screen to screen crossfade (e.g. splash to home)
                fadeIn(animationSpec = tween(350)) togetherWith
                        fadeOut(animationSpec = tween(300))
            }
        },
        popTransitionSpec = {
            if (initialState.key == PlayerNavKey) {
                // Collapsing player: slide down to bottom with fade
                fadeIn(animationSpec = tween(250)) togetherWith (
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(250))
                )
            } else {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            }
        },
        predictivePopTransitionSpec = { _ ->
            if (initialState.key == PlayerNavKey) {
                fadeIn(animationSpec = tween(250)) togetherWith (
                    slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(300)
                    ) + fadeOut(animationSpec = tween(250))
                )
            } else {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            }
        }
    )
}
