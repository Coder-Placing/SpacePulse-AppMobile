package com.example.spacepulse.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.spacepulse.model.beans.RegisterRequest
import com.example.spacepulse.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val roles = listOf("Homeowner", "Remodeler")
    var selectedRole by remember { mutableStateOf(roles[0]) }

    val registerState by viewModel.registerState.collectAsState()
    val darkBlue = Color(0xFF2C3E50)

    LaunchedEffect(registerState) {
        if (registerState != null) {
            isLoading = false
        }
        if (registerState?.isSuccess == true) {
            viewModel.resetStates()
            navController.navigate("login") {
                popUpTo("register") { inclusive = true }
            }
        }
    }

    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Este es el "lanzador" que abre la galería de Android
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Crear cuenta", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = darkBlue)
        Spacer(modifier = Modifier.height(24.dp))

        // Botón de subir foto
        Button(
            onClick = {
                galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            colors = ButtonDefaults.buttonColors(containerColor = if (selectedImageUri != null) Color(0xFF4CAF50) else darkBlue)
        ) {
            Text(if (selectedImageUri != null) "¡Foto seleccionada! ✓" else "Seleccionar Foto de Perfil")
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = fullName, onValueChange = { fullName = it },
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone, onValueChange = { phone = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword, onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("¿Cómo usarás SpacePulse?", fontWeight = FontWeight.Medium, color = darkBlue)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            roles.forEach { role ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = (role == selectedRole),
                        onClick = { selectedRole = role },
                        colors = RadioButtonDefaults.colors(selectedColor = darkBlue)
                    )
                    Text(text = if(role == "Homeowner") "Cliente" else "Remodelador")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // BOTÓN FINAL ACTUALIZADO
        Button(
            onClick = {
                if (password == confirmPassword) {
                    isLoading = true
                    // Usamos la nueva función conectada a ImgBB
                    viewModel.registerUser(
                        context = context,
                        imageUri = selectedImageUri,
                        email = email,
                        pass = password,
                        name = fullName,
                        phone = phone,
                        role = selectedRole
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = darkBlue),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                Text("Registrarme", fontSize = 18.sp, color = Color.White)
            }
        }

        if (registerState?.isFailure == true) {
            Text(text = "Error al registrar", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TextButton(onClick = { navController.navigate("login") }) {
                Text("Ya tengo una cuenta", color = Color.Gray)
            }
        }
    }
}