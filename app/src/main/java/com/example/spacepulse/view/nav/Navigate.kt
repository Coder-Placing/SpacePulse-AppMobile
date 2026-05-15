package com.example.spacepulse.view.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.spacepulse.view.ClientHomeScreen
import com.example.spacepulse.view.CrearEspacioScreen
import com.example.spacepulse.view.DetalleEspacioScreen
import com.example.spacepulse.view.EditarPerfilScreen
import com.example.spacepulse.view.LoginScreen
import com.example.spacepulse.view.MonitoreoEspacioScreen
import com.example.spacepulse.view.RegisterScreen
import com.example.spacepulse.viewmodel.AuthViewModel
import com.example.spacepulse.viewmodel.SpaceViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val spaceViewModel: SpaceViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, viewModel = authViewModel)
        }
        composable("register") {
            RegisterScreen(navController = navController, viewModel = authViewModel)
        }
        composable("clientHome") {
            ClientHomeScreen(navController = navController, viewModel = authViewModel, spaceViewModel = spaceViewModel)
        }
        composable("editarPerfil") {
            EditarPerfilScreen(navController = navController)
        }
        composable("crearEspacio") {
            CrearEspacioScreen(navController = navController, spaceViewModel = spaceViewModel)
        }
        composable("detalleEspacio/{spaceId}") { backStackEntry ->
            val spaceId = backStackEntry.arguments?.getString("spaceId")?.toLongOrNull() ?: 0L
            DetalleEspacioScreen(navController = navController, spaceViewModel = spaceViewModel, spaceId = spaceId)
        }
        composable("monitoreoEspacio/{spaceId}") { backStackEntry ->
            val spaceId = backStackEntry.arguments?.getString("spaceId")?.toLongOrNull() ?: 0L
            MonitoreoEspacioScreen(navController = navController, spaceViewModel = spaceViewModel, spaceId = spaceId)
        }
    }
}