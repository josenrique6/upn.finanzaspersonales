package com.example.upnfinanzaspersonales.presentation.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.upnfinanzaspersonales.data.local.entities.CategoriaEntity
import com.example.upnfinanzaspersonales.data.local.entities.CuentaEntity
import com.example.upnfinanzaspersonales.data.local.entities.TransaccionEntity
import java.time.LocalDate
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit = {}) { // Callback para cuando el login es exitoso
    var username by remember { mutableStateOf("") } // Estado para almacenar el nombre de usuario o email
    var password by remember { mutableStateOf("") } // Estado para almacenar la contraseña
    val context = LocalContext.current // Contexto para mostrar mensajes Toast

    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo el tamaño disponible
            .padding(16.dp), // Margen interno de 16dp
        horizontalAlignment = Alignment.CenterHorizontally, // Alineación horizontal centrada
        verticalArrangement = Arrangement.Center // Alineación vertical centrada
    ) {
        // Título de la pantalla
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium, // Estilo del texto
            modifier = Modifier.padding(bottom = 24.dp) // Espaciado inferior
        )

        // Campo de texto para el nombre de usuario o email
        OutlinedTextField(
            value = username, // Valor actual del campo
            onValueChange = { username = it }, // Actualiza el estado al cambiar el texto
            label = { Text("Usuario o Email") }, // Etiqueta del campo
            singleLine = true, // Limita el campo a una sola línea
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Icono de usuario") }, // Icono al inicio
            modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho disponible
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre elementos

        // Campo de texto para la contraseña
        OutlinedTextField(
            value = password, // Valor actual del campo
            onValueChange = { password = it }, // Actualiza el estado al cambiar el texto
            label = { Text("Contraseña") }, // Etiqueta del campo
            singleLine = true, // Limita el campo a una sola línea
            visualTransformation = PasswordVisualTransformation(), // Oculta el texto ingresado
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), // Configuración del teclado
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Icono de candado") }, // Icono al inicio
            modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho disponible
        )

        Spacer(modifier = Modifier.height(24.dp)) // Espaciado entre elementos

        Button(
            onClick = {
                // Lógica de autenticación simple
                if (username.isNotBlank() && password.isNotBlank()) { // Verifica que los campos no estén vacíos
                    Log.d("LoginScreen", "Usuario: $username, Contraseña: $password") // Registra los datos en el log
                    Toast.makeText(context, "Login Exitoso (Simulado)", Toast.LENGTH_SHORT).show() // Muestra un mensaje de éxito
                    onLoginSuccess() // Llama al callback de éxito
                } else {
                    Toast.makeText(context, "Por favor, ingresa usuario y contraseña", Toast.LENGTH_SHORT).show() // Muestra un mensaje de error
                }
            },
            modifier = Modifier.fillMaxWidth() // Ocupa todo el ancho disponible
        ) {
            Text("Entrar") // Texto del botón
        }

        Spacer(modifier = Modifier.height(16.dp)) // Espaciado entre elementos

        // Botón de texto para la funcionalidad de "Olvidé mi contraseña"
        TextButton(onClick = {
            Toast.makeText(context, "Funcionalidad 'Olvidé contraseña' no implementada", Toast.LENGTH_SHORT).show() // Muestra un mensaje indicando que no está implementado
        }) {
            Text("¿Olvidaste tu contraseña?") // Texto del botón
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MaterialTheme { // Aplica el tema de la aplicación
        LoginScreen() // Vista previa de la pantalla de inicio de sesión
    }
}