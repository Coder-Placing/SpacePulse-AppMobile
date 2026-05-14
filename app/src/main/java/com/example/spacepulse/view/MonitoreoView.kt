package com.example.spacepulse.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MonitoreoView() {
    val darkBlue = Color(0xFF2C3E50)

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(text = "Monitoreo", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = darkBlue)
        Text(text = "Estado del espacio", fontSize = 16.sp, color = Color.Gray)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Filled.WifiTethering, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color.LightGray)
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Monitoreo no disponible", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = darkBlue)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Este espacio aún no tiene sensores\nactivos", fontSize = 16.sp, color = Color.Gray, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD3A076))) {
                Text("Solicitar monitoreo", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}