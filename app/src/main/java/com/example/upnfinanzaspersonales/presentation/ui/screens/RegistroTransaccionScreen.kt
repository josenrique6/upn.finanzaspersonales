package com.example.upnfinanzaspersonales.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.upnfinanzaspersonales.domain.model.Transaccion
import java.time.LocalDate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.upnfinanzaspersonales.data.local.entities.CategoriaEntity
import com.example.upnfinanzaspersonales.data.local.entities.CuentaEntity
import com.example.upnfinanzaspersonales.data.local.entities.TransaccionEntity

/*
* pantalla de la interfaz de usuario en Jetpack Compose que permite al
* usuario registrar una nueva transacción financiera.
* */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroTransaccionScreen(
    cuentas: List<CuentaEntity>, // Lista de cuentas disponibles para seleccionar
    categorias: List<CategoriaEntity>, // Lista de categorías disponibles para seleccionar
    onGuardarClick: (TransaccionEntity) -> Unit, // Callback para guardar la transacción
    idUsuario: Int = 1 // ID del usuario por defecto
) {
    // Estados para los campos del formulario
    var tipo by remember { mutableStateOf("Gasto") } // Tipo de transacción (Gasto o Ingreso)
    var monto by remember { mutableStateOf("") } // Monto de la transacción
    var descripcion by remember { mutableStateOf("") } // Descripción de la transacción
    var fecha by remember { mutableStateOf(LocalDate.now()) } // Fecha de la transacción

    var cuentaSeleccionada by remember { mutableStateOf<CuentaEntity?>(null) } // Cuenta seleccionada
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaEntity?>(null) } // Categoría seleccionada

    var expandedCuenta by remember { mutableStateOf(false) } // Estado del menú desplegable de cuentas
    var expandedCategoria by remember { mutableStateOf(false) } // Estado del menú desplegable de categorías

    val tipos = listOf("Gasto", "Ingreso") // Tipos de transacción disponibles

    val context = LocalContext.current // Contexto para mostrar el selector de fecha

    // Diálogo para seleccionar la fecha
    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                fecha = LocalDate.of(year, month + 1, dayOfMonth) // Actualiza la fecha seleccionada
            },
            fecha.year,
            fecha.monthValue - 1, // El mes es 0-indexado
            fecha.dayOfMonth
        )
    }

    // Filtra las categorías según el tipo seleccionado
    val categoriasFiltradas = remember(tipo) {
        categorias.filter { it.tipo == tipo }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Registrar Transacción") }) } // Barra superior con el título
    ) { padding ->
        Column(
            Modifier
                .padding(padding) // Respeta el padding del Scaffold
                .padding(16.dp) // Margen interno
                .verticalScroll(rememberScrollState()) // Habilita el scroll vertical
        ) {
            // Selector de Fecha
            Button(
                onClick = { datePickerDialog.show() }, // Muestra el selector de fecha
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fecha: $fecha") // Muestra la fecha seleccionada
            }

            Spacer(Modifier.height(16.dp))

            // Selector de Tipo de Transacción
            Text("Tipo:")
            Row {
                tipos.forEach { item ->
                    Row(
                        Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                tipo = item // Cambia el tipo seleccionado
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = tipo == item, onClick = { tipo = item }) // Botón de selección
                        Text(item) // Texto del tipo
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Campo para ingresar el monto
            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it }, // Actualiza el monto ingresado
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Solo permite números
            )

            Spacer(Modifier.height(8.dp))

            // Menú desplegable para seleccionar la cuenta
            ExposedDropdownMenuBox(
                expanded = expandedCuenta,
                onExpandedChange = { expandedCuenta = !expandedCuenta }) {
                OutlinedTextField(
                    value = cuentaSeleccionada?.nombre ?: "", // Muestra el nombre de la cuenta seleccionada
                    onValueChange = {},
                    readOnly = true, // Campo de solo lectura
                    label = { Text("Cuenta") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCuenta) }, // Icono del menú desplegable
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedCuenta,
                    onDismissRequest = { expandedCuenta = false }) {
                    cuentas.forEach {
                        DropdownMenuItem(
                            text = { Text(it.nombre) }, // Muestra el nombre de cada cuenta
                            onClick = {
                                cuentaSeleccionada = it // Actualiza la cuenta seleccionada
                                expandedCuenta = false // Cierra el menú
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Menú desplegable para seleccionar la categoría
            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }) {
                OutlinedTextField(
                    value = categoriaSeleccionada?.nombre ?: "", // Muestra el nombre de la categoría seleccionada
                    onValueChange = {},
                    readOnly = true, // Campo de solo lectura
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) }, // Icono del menú desplegable
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }) {
                    categoriasFiltradas.forEach {
                        DropdownMenuItem(
                            text = { Text(it.nombre) }, // Muestra el nombre de cada categoría
                            onClick = {
                                categoriaSeleccionada = it // Actualiza la categoría seleccionada
                                expandedCategoria = false // Cierra el menú
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Campo para ingresar la descripción
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it }, // Actualiza la descripción ingresada
                label = { Text("Descripción") }
            )

            Spacer(Modifier.height(16.dp))

            // Botón para guardar la transacción
            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() // Convierte el monto a Double
                    if (montoDouble != null && cuentaSeleccionada != null && categoriaSeleccionada != null) {
                        // Llama al callback con los datos de la transacción
                        onGuardarClick(
                            TransaccionEntity(
                                id_usuario = idUsuario,
                                id_categoria = categoriaSeleccionada!!.id_categoria,
                                id_cuenta = cuentaSeleccionada!!.id_cuenta,
                                monto = montoDouble,
                                fecha = fecha,
                                descripcion = descripcion.takeIf { it.isNotBlank() }, // Solo guarda la descripción si no está vacía
                                tipo = tipo
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar") // Texto del botón
            }
        }
    }
}