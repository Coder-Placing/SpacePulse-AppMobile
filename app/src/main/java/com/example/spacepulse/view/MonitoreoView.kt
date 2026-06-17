package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Router
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spacepulse.viewmodel.SpaceViewModel

@Composable
fun MonitoreoView(navController: NavController, spaceViewModel: SpaceViewModel) {
    val darkBlue = Color(0xFF2C3E50)
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""

    val notifications by spaceViewModel.notifications.collectAsState()
    val myDevices by spaceViewModel.myIoTDevices.collectAsState()

    LaunchedEffect(Unit) {
        if (token.isNotEmpty()) {
            spaceViewModel.fetchNotifications(token)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(text = "Centro de Control", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = darkBlue)
        Text(text = "Tus notificaciones", fontSize = 16.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(24.dp))

        if (notifications.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Filled.NotificationsNone, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "No tienes alertas activas", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = darkBlue)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Aquí aparecerán tus notificaciones recientes", fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                if (notifications.isNotEmpty()) {
                    item {
                        Text(text = "Alertas recientes", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue, modifier = Modifier.padding(vertical = 12.dp))
                    }
                    // Implement notification items if needed
                }

            }
        }
    }
}
