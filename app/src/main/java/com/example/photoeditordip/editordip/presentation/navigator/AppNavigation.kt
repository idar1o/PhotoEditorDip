package com.example.photoeditordip.navigation

import android.net.Uri
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bottombar.AnimatedBottomBar
import com.example.bottombar.components.BottomBarItem
import com.example.bottombar.model.IndicatorStyle
import com.example.photoeditordip.editordip.presentation.ai_tools.AIToolboxScreen
import com.example.photoeditordip.presentation.editing.EditScreen
import com.example.photoeditordip.presentation.home.HomeScreen

// Sealed class for screen routes
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AIToolbox : Screen("ai_toolbox")
    object Edit : Screen("edit_screen/{imageUri}")

    // Helper method to create routes with arguments
    fun createRoute(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                route.replace("{$arg}", arg)
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        BottomBarTab.Home,
        BottomBarTab.AIToolbox
    )

    val selectedItem = items.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 0

    val bottomBarScreens = items.map { it.route }

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarScreens) {
                AnimatedBottomBar(
                    modifier = Modifier.height(56.dp),
                    selectedItem = selectedItem,
                    itemSize = items.size,
                    containerColor = Color(0x33FFFFFF),
                    indicatorStyle = IndicatorStyle.LINE
                ) {
                    BottomBarItem(
                        selected = currentRoute == Screen.Home.route,
                        onClick = {
                            if (currentRoute != Screen.Home.route) {
                                navController.navigate(Screen.Home.route) {
                                    // Pop up to the home route to avoid stacking navigation entries
                                    popUpTo(Screen.AIToolbox.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        },
                        imageVector = Icons.Filled.Home,
                        label = "Home",
                        containerColor = Color.Transparent
                    )
                    BottomBarItem(
                        selected = currentRoute == Screen.AIToolbox.route,
                        onClick = {
                            if (currentRoute != Screen.AIToolbox.route) {
                                navController.navigate(Screen.AIToolbox.route) {
                                    // Pop up to the home route to avoid stacking navigation entries
                                    popUpTo(Screen.Home.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        },
                        imageVector = Icons.Filled.Settings,
                        label = "AI Toolbox",
                        containerColor = Color.Transparent
                    )


                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(navController)
            }
            composable(Screen.AIToolbox.route) {
                AIToolboxScreen()
            }
            composable(
                route = Screen.Edit.route,
                arguments = listOf(navArgument("imageUri") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val imageUriString = backStackEntry.arguments?.getString("imageUri") ?: ""
                val imageUri = imageUriString.takeIf { it.isNotEmpty() }?.let { Uri.parse(it) }
                EditScreen(navController, imageUri)
            }
        }
    }
}

@Composable
fun GlassmorphicBottomNavigation(navController: NavController) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

}


sealed class BottomBarTab(val title: String, val icon: ImageVector, val route: String) {
    object Home : BottomBarTab("Home", Icons.Filled.Home, Screen.Home.route)
    object AIToolbox : BottomBarTab("ToolBox", Icons.Filled.Settings, Screen.AIToolbox.route)
}

@Composable
fun TabNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val tabIndex = when (currentRoute) {
        Screen.Home.route -> 0
        Screen.AIToolbox.route -> 1
        else -> 0
    }

    TabRow(
        selectedTabIndex = tabIndex,
        modifier = Modifier.height(48.dp)
    ) {
        Tab(
            selected = tabIndex == 0,
            onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            },
            text = { Text("Home") }
        )
        Tab(
            selected = tabIndex == 1,
            onClick = {
                if (currentRoute != Screen.AIToolbox.route) {
                    navController.navigate(Screen.AIToolbox.route) {
                        popUpTo(Screen.Home.route) { inclusive = false }
                    }
                }
            },
            text = { Text("AI Toolbox") }
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == Screen.Home.route,
            onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        // Pop up to the home route to avoid stacking navigation entries
                        popUpTo(Screen.AIToolbox.route) {
                            inclusive = true
                        }
                    }
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = "AI Toolbox") },
            label = { Text("AI Toolbox") },
            selected = currentRoute == Screen.AIToolbox.route,
            onClick = {
                if (currentRoute != Screen.AIToolbox.route) {
                    navController.navigate(Screen.AIToolbox.route) {
                        // Pop up to the home route to avoid stacking navigation entries
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            }
        )
    }
}