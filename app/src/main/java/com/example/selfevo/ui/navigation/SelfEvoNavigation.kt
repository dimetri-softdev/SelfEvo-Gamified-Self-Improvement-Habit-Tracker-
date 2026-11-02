package com.example.selfevo.ui.navigation

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.selfevo.R
import com.example.selfevo.ui.auth.AuthViewModel
import com.example.selfevo.ui.auth.LoginScreen
import com.example.selfevo.ui.auth.SignUpScreen
import com.example.selfevo.ui.dashboard.DashboardScreen
import com.example.selfevo.ui.dashboard.DashboardViewModel
import com.example.selfevo.ui.evolution.EvolutionScreen
import com.example.selfevo.ui.habits.HabitsScreen
import com.example.selfevo.ui.settings.SettingsScreen
import com.example.selfevo.ui.theme.SelfEvoTheme

sealed class Screen(val route: String, val resourceId: Int, val icon: ImageVector) {
    object Login : Screen("login", R.string.login_title, Icons.Default.Lock)
    object SignUp : Screen("signup", R.string.signup_title, Icons.Default.Person)
    object Home : Screen("home", R.string.nav_home, Icons.Default.Home)
    object Habits : Screen("habits", R.string.nav_habits, Icons.Default.List)
    object Evolutions : Screen("evolutions", R.string.nav_evolutions, Icons.Default.Star)
    object Settings : Screen("settings", R.string.nav_settings, Icons.Default.Settings)
}

@Composable
fun SelfEvoApp(
    viewModel: DashboardViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentUser by authViewModel.currentUserFlow.collectAsState()
    val customAttrs by viewModel.customAttributes.collectAsState()
    val habits by viewModel.habits.collectAsState()

    SelfEvoTheme {
        NavHost(
            navController = navController,
            startDestination = if (currentUser != null) Screen.Home.route else Screen.Login.route
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { email, password ->
                        authViewModel.login(
                            email = email,
                            pass = password,
                            onSuccess = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                                viewModel.refreshData()
                            },
                            onError = { message ->
                                Toast.makeText(context, "Login Failed: $message", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onGoogleSignIn = {
                        authViewModel.signInWithGoogle(
                            context = context,
                            onSuccess = { displayName ->
                                if (!displayName.isNullOrBlank()) {
                                    viewModel.updatePlayerName(displayName)
                                }
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                                viewModel.refreshData()
                            },
                            onError = { message ->
                                Toast.makeText(context, "Google sign-in failed: $message", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onSignUpClick = { navController.navigate(Screen.SignUp.route) }
                )
            }
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    onSignUpSuccess = { email, password, name ->
                        authViewModel.signUp(
                            email = email,
                            pass = password,
                            name = name,
                            onSuccess = { displayName ->
                                viewModel.updatePlayerName(displayName)
                                viewModel.refreshData()
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.SignUp.route) { inclusive = true }
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            onError = { message ->
                                Toast.makeText(context, "SignUp Failed: $message", Toast.LENGTH_SHORT).show()
                            }
                        )
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
                        habits = habits,
                        customAttributes = customAttrs,
                        onAddHabit = { name, primAttr, secAttr, freq, time, days, duration ->
                            viewModel.addHabit(name, primAttr, secAttr, freq, time, days, duration)
                        },
                        onEditHabit = { habitId, name, primAttr, secAttr, freq, time, days, duration ->
                            viewModel.editHabit(habitId, name, primAttr, secAttr, freq, time, days, duration)
                        },
                        onDeleteHabit = { habitId ->
                            viewModel.deleteHabit(habitId)
                        },
                        onCompleteHabit = { habitId ->
                            viewModel.completeHabit(habitId)
                        },
                        onCreateCustomAttribute = { title, abbr, color ->
                            viewModel.addCustomAttribute(title, abbr, color)
                        }
                    )
                }
            }
            composable(Screen.Evolutions.route) {
                MainScaffold(navController, currentDestination) {
                    val playerCard by viewModel.playerCard.collectAsState()
                    EvolutionScreen(
                        playerCard = playerCard,
                        completedHabitCount = habits.count { it.isCompletedToday },
                        currentStreakDays = 14,
                        onEquipTheme = { themeKey ->
                            viewModel.updateEquippedTheme(themeKey)
                            Toast.makeText(context, "Card theme equipped!", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
            composable(Screen.Settings.route) {
                MainScaffold(navController, currentDestination) {
                    val playerCard by viewModel.playerCard.collectAsState()
                    SettingsScreen(
                        viewModel = viewModel,
                        playerName = playerCard?.playerName ?: "Player",
                        email = currentUser?.email ?: "Unknown",
                        onSignOut = {
                            authViewModel.signOut()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
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
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
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
                                tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                stringResource(screen.resourceId),
                                fontSize = 10.sp,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray
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
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = Color.Gray,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
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
