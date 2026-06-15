package com.example.spacepulse.view

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spacepulse.model.beans.TaskResponse
import com.example.spacepulse.viewmodel.SpaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TareasEspacioScreen(
    spaceId: Long,
    viewModel: SpaceViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("SpacePulsePrefs", Context.MODE_PRIVATE)
    val token = sharedPref.getString("USER_TOKEN", "") ?: ""

    val tasks by viewModel.tasksList.collectAsState()
    val isLoading by viewModel.isLoadingTasks.collectAsState()

    LaunchedEffect(spaceId) {
        viewModel.getTasksForSpace(token, spaceId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monitoreo de Tareas", fontWeight = FontWeight.Bold, color = Color(0xFF2C3E50)) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color(0xFF2C3E50))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF2C3E50))
            } else if (tasks.isEmpty()) {
                Text(
                    text = "Aún no hay tareas para este espacio.",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tasks) { task ->
                        TaskCard(task = task, viewModel = viewModel, token = token, spaceId = spaceId)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: TaskResponse, viewModel: SpaceViewModel, token: String, spaceId: Long) {
    var showEditDialog by remember { mutableStateOf(false) }

    val (statusColor, statusText) = when (task.status?.uppercase()) {
        "PENDING" -> Color(0xFFFFF9C4) to "Pendiente"
        "IN_PROGRESS" -> Color(0xFFBBDEFB) to "En Proceso"
        "COMPLETED" -> Color(0xFFC8E6C9) to "Completado"
        else -> Color(0xFFEEEEEE) to (task.status ?: "Desconocido")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ID: ${task.id}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Box(
                    modifier = Modifier
                        .background(statusColor, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusText,
                        color = Color.DarkGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = task.title ?: "Sin título",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF2C3E50)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.description ?: "Sin descripción",
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.Gray)
                }
                IconButton(onClick = {
                    // Ejecuta el DELETE pasando el id numérico de la tarea
                    viewModel.deleteModelTask(token, task.id, spaceId)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.6f))
                }
            }
        }
    }

    // Cuadro de diálogo emergente para editar los textos de la solicitud
    if (showEditDialog) {
        var editTitle by remember { mutableStateOf(task.title ?: "") }
        var editDescription by remember { mutableStateOf(task.description ?: "") }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar Solicitud", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateModelTask(token, task.id, editTitle, editDescription, spaceId)
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3E50))
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }
}