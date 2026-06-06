package com.example.timemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Settings
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.timemanager.ui.screens.DashboardScreen
import com.example.timemanager.ui.screens.ModeGalleryScreen
import com.example.timemanager.ui.screens.PreferenceSetupScreen
import com.example.timemanager.ui.screens.StatsScreen
import com.example.timemanager.ui.screens.TasksScreen
import com.example.timemanager.ui.screens.TimerScreen
import com.example.timemanager.ui.theme.TimeManagerTheme
import com.example.timemanager.ui.viewmodel.MainViewModel
import com.example.timemanager.ui.viewmodel.ViewModelFactory

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "概覽", Icons.Default.Dashboard)
    object Tasks : Screen("tasks", "任務", Icons.AutoMirrored.Filled.List)
    object Timer : Screen("timer", "計時", Icons.Default.Timer)
    object Stats : Screen("stats", "統計", Icons.Default.History)
    object ModeGallery : Screen("mode_gallery", "探索模式", Icons.Default.AutoAwesome)
    object PreferenceSetup : Screen("preference_setup", "作息設定", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory((application as TimeManagerApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimeManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppContent(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val navController = rememberNavController()
    val items = listOf(
        Screen.Dashboard,
        Screen.Tasks,
        Screen.Timer,
        Screen.Stats
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = null) },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = Screen.Dashboard.route,
            Modifier.padding(innerPadding)
        ) {
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToTimer = { navController.navigate(Screen.Timer.route) },
                    onNavigateToTasks = { navController.navigate(Screen.Tasks.route) },
                    onNavigateToModes = { navController.navigate(Screen.ModeGallery.route) },
                    onNavigateToPreferences = { navController.navigate(Screen.PreferenceSetup.route) }
                )
            }
            composable(Screen.PreferenceSetup.route) {
                PreferenceSetupScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.ModeGallery.route) {
                ModeGalleryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onApplied = {
                        navController.popBackStack(Screen.Dashboard.route, false)
                    }
                )
            }
            composable(Screen.Tasks.route) {
                TasksScreen(
                    viewModel = viewModel,
                    onNavigateToTimer = { navController.navigate(Screen.Timer.route) }
                )
            }
            composable(Screen.Timer.route) {
                TimerScreen(
                    viewModel = viewModel,
                    onNavigateToTasks = { navController.navigate(Screen.Tasks.route) }
                )
            }
            composable(Screen.Stats.route) {
                StatsScreen(viewModel = viewModel)
            }
        }
    }
}
