package com.example.spacepulse.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spacepulse.viewmodel.AuthViewModel
import com.example.spacepulse.viewmodel.SpaceViewModel

@Composable
fun ClientHomeScreen(navController: NavController, viewModel: AuthViewModel, spaceViewModel: SpaceViewModel) {
    val darkBlue = Color(0xFF2C3E50)
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    var isDashboard by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            SpacePulseBottomNavigation(darkBlue, selectedItem) {
                selectedItem = it
                isDashboard = false
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedItem) {
                0 -> if (isDashboard) {
                    DashboardView(navController, spaceViewModel) {
                        selectedItem = it
                        isDashboard = false
                    }
                } else {
                    EspaciosView(navController, spaceViewModel)
                }
                1 -> MonitoreoView(navController, spaceViewModel)
                2 -> PagosView()
                3 -> ReportesView()
                4 -> PerfilView(navController, viewModel)
            }
        }
    }
}

@Composable
fun SpacePulseBottomNavigation(selectedColor: Color, selectedItem: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf("Espacios", "Alertas", "Pagos", "Reportes", "Perfil")
    val icons = listOf(Icons.Filled.Home, Icons.Filled.Notifications, Icons.Filled.ShoppingCart, Icons.Filled.Description, Icons.Filled.Person)

    NavigationBar(
        containerColor = Color.White,
        contentColor = Color.Gray
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item, fontSize = 10.sp) },
                selected = selectedItem == index,
                onClick = { onItemSelected(index) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedColor,
                    selectedTextColor = selectedColor,
                    unselectedIconColor = Color.LightGray,
                    unselectedTextColor = Color.LightGray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}