package com.example.selfevo.ui.navigation

import androidx.compose.foundation.layout.Box
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
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.selfevo.R
import com.example.selfevo.data.auth.AuthRepository
import com.example.selfevo.ui.auth.LoginScreen
import com.example.selfevo.ui.auth.SignUpScreen
import com.example.selfevo.ui.dashboard.DashboardScreen
import com.example.selfevo.ui.dashboard.DashboardViewModel
import com.example.selfevo.ui.evolution.EvolutionScreen
import com.example.selfevo.ui.habits.HabitsScreen
import com.example.selfevo.ui.settings.SettingsScreen
import com.example.selfevo.ui.theme.Black
import com.example.selfevo.ui.theme.Gold
import com.example.selfevo.ui.theme.SelfEvoTheme
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val resourceId: Int, val icon: ImageVector) {
    object Login : Screen("login", R.string.login_title, Icons.Default.Lock)
    object SignUp : Screen("signup", R.string.signup_title, Icons.Default.Person)
    object Home : Screen("home", R.string.nav_home, Icons.Default.Home)
    object Habits : Screen("habits", R.string.nav_habits, Icons.Default.List)
    object Evolutions : Screen("evolutions", R.string.nav_evolutions, Icons.Default.Star)
    object Settings : Screen("settings", R.string.nav_settings, Icons.Default.Settings)
}

@Composable
fun SelfEvoApp(viewModel: DashboardViewModel, authRepository: AuthRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val coroutineScope = rememberCoroutineScope()

    var isLoggedIn by remember { mutableStateOf(authRepository.currentUser != null) }

    SelfEvoTheme {
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { email, password ->
                        coroutineScope.launch {
                            val result = authRepository.login(email, password)
                            if (result.isSuccess) {
                                isLoggedIn = true
                                viewModel.refreshData()
                            } else {
                                // Show error (e.g. via Snackbar or Toast)
                                // For now, just logging or relying on validation
                            }
                        }
                    },
                    onSignUpClick = { navController.navigate(Screen.SignUp.route) }
                )
            }
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    onSignUpSuccess = { email, password, name ->
                        coroutineScope.launch {
                            val result = authRepository.signUp(email, password)
                            if (result.isSuccess) {
                                // Potentially save player name to firestore or local db
                                isLoggedIn = true
                                viewModel.refreshData()
                            }
                        }
                    },
                    onLoginClick = { navController.navigate(Screen.Login.route) }
                )
            }
            composable(Screen.Home.route) {
                MainScaffold(navController, currentDestination) {
                    DashboardScreen(viewModel)
                }
            }
            composable(Screen.Habits.route) {
                MainScaffold(navController, currentDestination) {
                    HabitsScreen(
                        onAddHabit = { name, attr, freq, time ->
                            viewModel.addHabit(name, attr, freq, time)
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }
            }
            composable(Screen.Evolutions.route) {
                MainScaffold(navController, currentDestination) {
                    val playerCard by viewModel.playerCard.collectAsState()
                    EvolutionScreen(currentOvr = playerCard?.ovr ?: 50)
                }
            }
            composable(Screen.Settings.route) {
                MainScaffold(navController, currentDestination) {
                    SettingsScreen(
                        viewModel = viewModel,
                        onSignOut = {
                            authRepository.signOut()
                            isLoggedIn = false
                        },
                        onSyncClick = { viewModel.refreshData() }
                    )
                }
            }
        }
    }
}

@Composable
fun MainScaffold(
    navController: NavHostController,
    currentDestination: NavDestination?,
    content: @Composable () -> Unit
) {
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
        Box(modifier = Modifier.padding(innerPadding)) {
            content()
        }
    }
}
