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
    cuentas: List<CuentaEntity>,
    categorias: List<CategoriaEntity>,
    onGuardarClick: (TransaccionEntity) -> Unit,
    idUsuario: Int = 1 // Asumiendo usuario por defecto por ahora
) {
    var tipo by remember { mutableStateOf("Gasto") }
    var monto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(LocalDate.now()) }

    var cuentaSeleccionada by remember { mutableStateOf<CuentaEntity?>(null) }
    var categoriaSeleccionada by remember { mutableStateOf<CategoriaEntity?>(null) }

    var expandedCuenta by remember { mutableStateOf(false) }
    var expandedCategoria by remember { mutableStateOf(false) }

    val tipos = listOf("Gasto", "Ingreso")

    val context = LocalContext.current

    val datePickerDialog = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                fecha = LocalDate.of(year, month + 1, dayOfMonth)
            },
            fecha.year,
            fecha.monthValue - 1,
            fecha.dayOfMonth
        )
    }

    // Filtrar categorías según el tipo seleccionado
    val categoriasFiltradas = remember(tipo) {
        categorias.filter { it.tipo == tipo }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Registrar Transacción") }) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Selector de Fecha
            Button(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fecha: $fecha")
            }

            Spacer(Modifier.height(16.dp))
            // Tipo
            Text("Tipo:")
            Row {
                tipos.forEach { item ->
                    Row(
                        Modifier
                            .padding(end = 8.dp)
                            .clickable {
                                tipo = item
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = tipo == item, onClick = { tipo = item })
                        Text(item)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Monto
            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.height(8.dp))

            // Cuenta Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedCuenta,
                onExpandedChange = { expandedCuenta = !expandedCuenta }) {
                OutlinedTextField(
                    value = cuentaSeleccionada?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cuenta") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCuenta) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedCuenta,
                    onDismissRequest = { expandedCuenta = false }) {
                    cuentas.forEach {
                        DropdownMenuItem(
                            text = { Text(it.nombre) },
                            onClick = {
                                cuentaSeleccionada = it
                                expandedCuenta = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Categoría Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedCategoria,
                onExpandedChange = { expandedCategoria = !expandedCategoria }) {
                OutlinedTextField(
                    value = categoriaSeleccionada?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoria) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategoria,
                    onDismissRequest = { expandedCategoria = false }) {
                    categoriasFiltradas.forEach {
                        DropdownMenuItem(
                            text = { Text(it.nombre) },
                            onClick = {
                                categoriaSeleccionada = it
                                expandedCategoria = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Descripción
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") }
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull()
                    if (montoDouble != null && cuentaSeleccionada != null && categoriaSeleccionada != null) {
                        onGuardarClick(
                            TransaccionEntity(
                                id_usuario = idUsuario,
                                id_categoria = categoriaSeleccionada!!.id_categoria,
                                id_cuenta = cuentaSeleccionada!!.id_cuenta,
                                monto = montoDouble,
                                fecha = fecha,
                                descripcion = descripcion.takeIf { it.isNotBlank() },
                                tipo = tipo
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}