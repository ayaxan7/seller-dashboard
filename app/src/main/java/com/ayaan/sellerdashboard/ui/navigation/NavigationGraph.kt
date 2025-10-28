package com.ayaan.sellerdashboard.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ayaan.sellerdashboard.data.repository.AuthRepository
import com.ayaan.sellerdashboard.ui.screens.addproduct.AddProductScreen
import com.ayaan.sellerdashboard.ui.screens.editproduct.EditProductScreen
import com.ayaan.sellerdashboard.ui.screens.login.LoginScreen
import com.ayaan.sellerdashboard.ui.screens.productlist.ProductListScreen
import com.ayaan.sellerdashboard.ui.screens.register.RegisterScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    authRepository: AuthRepository
) {
    val startDestination = if (authRepository.isUserLoggedIn()) {
        Screen.ProductList.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.ProductList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.ProductList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ProductList.route) {
            ProductListScreen(
                onNavigateToAddProduct = {
                    navController.navigate(Screen.AddProduct.route)
                },
                onNavigateToEditProduct = { productId ->
                    navController.navigate(Screen.EditProduct.createRoute(productId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AddProduct.route) {
            AddProductScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onProductAdded = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            EditProductScreen(
                productId = productId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onProductUpdated = {
                    navController.popBackStack()
                }
            )
        }
    }
}

