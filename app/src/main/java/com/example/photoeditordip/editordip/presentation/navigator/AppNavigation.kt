package com.example.photoeditordip.navigation

import android.net.Uri
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
import com.example.photoeditordip.editordip.presentation.preview.PreviewScreen
import com.example.photoeditordip.presentation.editing.EditScreen
import com.example.photoeditordip.presentation.home.HomeScreen

// Sealed class for screen routes
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AIToolbox : Screen("ai_toolbox")
    object Edit : Screen("edit_screen?imageUri={imageUri}&aiTool={aiTool}&origin={origin}")

    class EditParam(imageUri: String, aiTool: String? = null, origin: String = "home") :
        Screen("edit_screen?imageUri=${Uri.encode(imageUri)}&aiTool=${Uri.encode(aiTool ?: "")}&origin=$origin")


    object Preview : Screen("preview_screen/{imageUri}")

    class PreviewParam(imageUri: String) : Screen("preview_screen/${Uri.encode(imageUri)}")
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
                AIToolboxScreen(navController)
            }
            composable(
                route = Screen.Edit.route,
                arguments = listOf(
                    navArgument("imageUri") { type = NavType.StringType },
                    navArgument("aiTool") { type = NavType.StringType; nullable = true; defaultValue = null },
                    navArgument("origin") { type = NavType.StringType; nullable = true; defaultValue = "home" }
                )
            ) { backStackEntry ->
                val imageUriEncoded = backStackEntry.arguments?.getString("imageUri")
                val imageUri = imageUriEncoded?.let { Uri.parse(Uri.decode(it)) }
                val aiTool = backStackEntry.arguments?.getString("aiTool")
                val origin = backStackEntry.arguments?.getString("origin")

                EditScreen(
                    navController = navController,
                    imageUri = imageUri,
                    aiTool = aiTool,
                    origin = origin
                )
            }



            composable(
                route = "preview_screen/{imageUri}",
                arguments = listOf(navArgument("imageUri") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val encodedUri = backStackEntry.arguments?.getString("imageUri")
                val decodedUri = encodedUri?.let { Uri.parse(Uri.decode(it)) }
                PreviewScreen(navController, decodedUri)
            }


        }
    }
}



sealed class BottomBarTab(val title: String, val icon: ImageVector, val route: String) {
    object Home : BottomBarTab(title = "Home", Icons.Filled.Home, Screen.Home.route)
    object AIToolbox : BottomBarTab("ToolBox", Icons.Filled.Settings, Screen.AIToolbox.route)
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