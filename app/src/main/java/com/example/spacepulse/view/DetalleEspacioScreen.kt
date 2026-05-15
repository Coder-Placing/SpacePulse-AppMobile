package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spacepulse.viewmodel.SpaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEspacioScreen(navController: NavController, spaceViewModel: SpaceViewModel, spaceId: Long) {
    val darkBlue = Color(0xFF2C3E50)
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""
    val userId = sharedPref.getString("USER_ID", "") ?: ""

    val spaces by spaceViewModel.spaces.collectAsState()
    val space = spaces.find { it.id == spaceId }
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val deleteState by spaceViewModel.deleteSpaceState.collectAsState()

    LaunchedEffect(deleteState) {
        if (deleteState?.isSuccess == true) {
            spaceViewModel.resetStates()
            navController.popBackStack()
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar espacio", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = darkBlue) },
            text = { Text("Esta acción quitará el espacio de tu\nlista", color = Color.Gray, fontSize = 16.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        spaceViewModel.deleteSpace(token, userId, spaceId)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = darkBlue)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de espacio", fontWeight = FontWeight.Bold, color = darkBlue, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = darkBlue)
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Opciones", tint = Color.Gray)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Eliminar", color = Color.Red) },
                                onClick = {
                                    showMenu = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = space?.title ?: "Cargando...", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = darkBlue)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = space?.location ?: "", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Estado: ${space?.status ?: ""}", color = Color.Gray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Presupuesto: ${space?.currency ?: ""} ${space?.estimatedBudget ?: ""}", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Resumen de tareas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {}, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Color(0xFF27AE60)), contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Text("0 completadas", color = Color.DarkGray, fontSize = 12.sp)
                }
                OutlinedButton(onClick = {}, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, darkBlue), contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Text("1 en proceso", color = Color.DarkGray, fontSize = 12.sp)
                }
                OutlinedButton(onClick = {}, shape = RoundedCornerShape(20.dp), border = BorderStroke(1.dp, Color(0xFFD68910)), contentPadding = PaddingValues(horizontal = 12.dp)) {
                    Text("1 pendiente", color = Color.DarkGray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Opciones", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = darkBlue)
            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { }, modifier = Modifier.weight(1f).height(80.dp), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE0E0E0))) {
                    Text("Avance", color = darkBlue, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = { navController.navigate("monitoreoEspacio/$spaceId") }, modifier = Modifier.weight(1f).height(80.dp), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE0E0E0))) {
                    Text("Monitoreo", color = darkBlue, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = { }, modifier = Modifier.weight(1f).height(80.dp), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE0E0E0))) {
                    Text("Pagos", color = darkBlue, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = { }, modifier = Modifier.weight(1f).height(80.dp), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFE0E0E0))) {
                    Text("Reportes", color = darkBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}