package com.example.selfevo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.selfevo.R
import com.example.selfevo.ui.auth.LoginScreen
import com.example.selfevo.ui.dashboard.DashboardScreen
import com.example.selfevo.ui.dashboard.DashboardViewModel
import com.example.selfevo.ui.evolution.EvolutionScreen
import com.example.selfevo.ui.habits.HabitsScreen
import com.example.selfevo.ui.settings.SettingsScreen
import com.example.selfevo.ui.theme.Black
import com.example.selfevo.ui.theme.Gold
import com.example.selfevo.ui.theme.SelfEvoTheme

sealed class Screen(val route: String, val resourceId: Int, val icon: ImageVector) {
    object Login : Screen("login", R.string.login_title, Icons.Default.Lock)
    object Home : Screen("home", R.string.nav_home, Icons.Default.Home)
    object Habits : Screen("habits", R.string.nav_habits, Icons.Default.List)
    object Evolutions : Screen("evolutions", R.string.nav_evolutions, Icons.Default.Star)
    object Settings : Screen("settings", R.string.nav_settings, Icons.Default.Settings)
}

@Composable
fun SelfEvoApp(viewModel: DashboardViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var isLoggedIn by remember { mutableStateOf(false) }

    SelfEvoTheme {
        if (!isLoggedIn) {
            LoginScreen(onLoginSuccess = { isLoggedIn = true })
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = Black,
                        contentColor = Gold,
                        tonalElevation = 8.dp
                    ) {
                        val items = listOf(
                            Screen.Home,
                            Screen.Habits,
                            Screen.Evolutions,
                            Screen.Settings
                        )
                        items.forEach { screen ->
                            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        screen.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(24.dp),
                                        tint = if (selected) Gold else Color.Gray
                                    )
                                },
                                label = {
                                    Text(
                                        stringResource(screen.resourceId),
                                        fontSize = 10.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) Gold else Color.Gray
                                    )
                                },
                                selected = selected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Gold,
                                    unselectedIconColor = Color.Gray,
                                    indicatorColor = Black
                                )
                            )
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController,
                    startDestination = Screen.Home.route,
                    Modifier.padding(innerPadding)
                ) {
                    composable(Screen.Home.route) { DashboardScreen(viewModel) }
                    composable(Screen.Habits.route) { HabitsScreen(onAddHabit = { _, _, _, _ -> }) }
                    composable(Screen.Evolutions.route) {
                        val playerCard by viewModel.playerCard.collectAsState()
                        EvolutionScreen(currentOvr = playerCard?.ovr ?: 50)
                    }
                    composable(Screen.Settings.route) { SettingsScreen(onSignOut = { isLoggedIn = false }) }
                }
            }
        }
    }
}
