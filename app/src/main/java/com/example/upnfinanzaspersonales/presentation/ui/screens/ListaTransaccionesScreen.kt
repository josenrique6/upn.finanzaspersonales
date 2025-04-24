package com.example.upnfinanzaspersonales.presentation.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.upnfinanzaspersonales.data.local.relations.TransaccionConDetalles
import java.time.LocalDate

@Composable
fun ListaTransaccionesScreen(
    transacciones: List<TransaccionConDetalles>,
    onAgregarClick: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFFDF9FF),
        topBar = {
            Text(
                text = "Resumen de Finanzas",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth().padding(top = 20.dp),
                textAlign = TextAlign.Center
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregarClick,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Filled.AddCircle, contentDescription = "Agregar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            var fechaSeleccionada by remember { mutableStateOf(LocalDate.now()) }

            FiltroPorFecha(
                fechaSeleccionada = fechaSeleccionada,
                onFechaSeleccionada = { fechaSeleccionada = it }
            )

            val transaccionesFiltradas = transacciones.filter {
                it.transaccion.fecha == fechaSeleccionada
            }

            LazyColumn(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxSize()
            ) {
                items(transaccionesFiltradas) { item ->
                    TransaccionItem(item)
                }
            }
        }
    }
}

@Composable
fun TransaccionItem(item: TransaccionConDetalles) {
    val trans = item.transaccion
    val colorFondo = if (trans.tipo == "Ingreso") Color(0xFFDFF5E1) else Color(0xFFFFEAEA)
    val colorTexto = if (trans.tipo == "Ingreso") Color(0xFF1B5E20) else Color(0xFFB71C1C)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Primera fila: tipo y monto alineado a la derecha
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Badge(
                    containerColor = if (trans.tipo == "Ingreso") Color(0xFF81C784) else Color(0xFFE57373),
                    contentColor = Color.White
                ) {
                    Text(trans.tipo)
                }
                Text(
                    text = "S/ %.2f".format(trans.monto),
                    style = MaterialTheme.typography.titleMedium.copy(color = colorTexto)
                )
            }

            Spacer(Modifier.height(6.dp))

            // Segunda fila: Cuenta y Categoría con íconos
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Cuenta: ${item.cuenta.nombre}", style = MaterialTheme.typography.bodyMedium)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Category, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Categoría: ${item.categoria.nombre}", style = MaterialTheme.typography.bodyMedium)
            }

            // Nota (opcional)
            item.transaccion.descripcion?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Notes, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Nota: $it", style = MaterialTheme.typography.bodySmall)
                }
            }

            // Fecha alineada a la derecha
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    text = item.transaccion.fecha.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun FiltroPorFecha(
    fechaSeleccionada: LocalDate,
    onFechaSeleccionada: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val year = fechaSeleccionada.year
    val month = fechaSeleccionada.monthValue - 1 // DatePicker usa 0-indexed
    val day = fechaSeleccionada.dayOfMonth

    val datePickerDialog = remember {
        DatePickerDialog(context, { _, y, m, d ->
            onFechaSeleccionada(LocalDate.of(y, m + 1, d))
        }, year, month, day)
    }

    Button(
        onClick = { datePickerDialog.show() },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text("Filtrar por: $fechaSeleccionada")
    }
}