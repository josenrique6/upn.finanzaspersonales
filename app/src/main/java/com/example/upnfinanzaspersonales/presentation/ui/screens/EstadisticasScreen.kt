package com.example.upnfinanzaspersonales.presentation.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import com.example.upnfinanzaspersonales.data.local.relations.TransaccionConDetalles
import java.time.LocalDate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.upnfinanzaspersonales.data.local.entities.CategoriaEntity
import com.example.upnfinanzaspersonales.data.local.entities.CuentaEntity
import com.example.upnfinanzaspersonales.data.local.entities.TransaccionEntity
import com.example.upnfinanzaspersonales.data.local.entities.UsuarioEntity
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun EstadisticasScreen(
    transacciones: List<TransaccionConDetalles>,
    initialFecha: LocalDate
) {
    // Estado para la fecha seleccionada
    var fechaSeleccionada by remember { mutableStateOf(initialFecha) }

    // Estado para el tipo (Ingreso/Gasto)
    var tipoSeleccionado by remember { mutableStateOf("Gasto") }

    // Contexto para el DatePickerDialog
    val context = LocalContext.current

    // Diálogo de selección de fecha
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, y: Int, m: Int, d: Int ->
                fechaSeleccionada = LocalDate.of(y, m + 1, d)
            },
            fechaSeleccionada.year,
            fechaSeleccionada.monthValue - 1,
            fechaSeleccionada.dayOfMonth
        )
    }

    // Cálculo de totales
    val totalIngreso = transacciones
        .filter { it.transaccion.tipo == "Ingreso" && it.transaccion.fecha == fechaSeleccionada }
        .sumOf { it.transaccion.monto }
    val totalGasto = transacciones
        .filter { it.transaccion.tipo == "Gasto" && it.transaccion.fecha == fechaSeleccionada }
        .sumOf { it.transaccion.monto }

    // Resumen por categoría
    val transaccionesFiltradas = transacciones.filter {
        it.transaccion.tipo == tipoSeleccionado && it.transaccion.fecha == fechaSeleccionada
    }
    val resumen = transaccionesFiltradas
        .groupBy { it.categoria.nombre }
        .mapValues { it.value.sumOf { it.transaccion.monto } }
    val totalCategorias = resumen.values.sum().takeIf { it > 0 } ?: 1.0

    // Mapa de colores y emojis
    val colores = mapOf(
        "Sueldo" to Color(0xFF81C784),
        "Ropa" to Color(0xFFEF5350),
        "Entretenimiento" to Color(0xFFFF8A65),
        "Comida" to Color(0xFFFFEB3B),
        "Transporte" to Color(0xFF6B3B94),
        "Regalo" to Color(0xFF64B5F6),
    )
    val emojiMap = mapOf(
        "Ropa" to "👗",
        "Entretenimiento" to "🎭",
        "Comida" to "🍔",
        "Transporte" to "🚗",
        "Regalo" to "🎁",
        "Sueldo" to "💰",
    )

    // Datos para el PieChart
    val pieData = resumen.entries.map { (cat, monto) ->
        Pie(
            label = cat,
            data = monto,
            color = colores[cat] ?: Color.Gray,
            selectedColor = (colores[cat] ?: Color.Gray).copy(alpha = 0.8f)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- 0) Selector de fecha ---
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                fechaSeleccionada = fechaSeleccionada.minusDays(1)
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Día anterior"
                )
            }
            Text(
                text = fechaSeleccionada.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                modifier = Modifier
                    .clickable { datePickerDialog.show() }
                    .padding(horizontal = 12.dp),
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = {
                fechaSeleccionada = fechaSeleccionada.plusDays(1)
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Día siguiente"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 1) TabRow con totales ---
        TabRow(
            selectedTabIndex = if (tipoSeleccionado == "Ingreso") 0 else 1,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.DarkGray,
            contentColor = Color.White,
            indicator = { positions ->
                TabRowDefaults.Indicator(
                    Modifier
                        .tabIndicatorOffset(positions[if (tipoSeleccionado == "Ingreso") 0 else 1])
                        .height(2.dp),
                    color = Color.Red
                )
            }
        ) {
            Tab(
                selected = tipoSeleccionado == "Ingreso",
                onClick = { tipoSeleccionado = "Ingreso" },
                text = { Text("Ingreso S/. %.2f".format(totalIngreso)) }
            )
            Tab(
                selected = tipoSeleccionado == "Gasto",
                onClick = { tipoSeleccionado = "Gasto" },
                text = { Text("Gastos S/. %.2f".format(totalGasto)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2) PieChart ---
        PieChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            data = pieData,
            onPieClick = { _ -> /* opcional: destacar sector */ },
            selectedScale = 1.1f,
            style = Pie.Style.Fill
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- 3) Lista de categorías ---
        Column {
            resumen.forEach { (categoria, monto) ->
                val porcentaje = (monto * 100 / totalCategorias).roundToInt()
                val colorCat = colores[categoria] ?: Color.Gray
                val emoji = emojiMap[categoria] ?: ""

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(colorCat, shape = RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$porcentaje%",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$emoji $categoria",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "S/ %.2f".format(monto),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEstadisticasScreen() {
    EstadisticasScreen(
        transacciones = listOf(
            TransaccionConDetalles(
                transaccion = TransaccionEntity(
                    id_transaccion = 1,
                    id_usuario = 1,
                    id_categoria = 1,
                    id_cuenta = 1,
                    monto = 150.0,
                    fecha = LocalDate.now(),
                    descripcion = "Pago de servicios",
                    tipo = "Gasto"
                ),
                categoria = CategoriaEntity(
                    id_categoria = 1,
                    nombre = "Servicios",
                    tipo = "Gasto"
                ),
                cuenta = CuentaEntity(
                    id_cuenta = 1,
                    nombre = "Cuenta Principal"
                ),
                usuario = UsuarioEntity(
                    email = "",
                    fecha_registro = LocalDate.now(),
                    id_usuario = 1,
                    nombre = "Usuario demo"
                )
            ),
            TransaccionConDetalles(
                transaccion = TransaccionEntity(
                    id_transaccion = 2,
                    id_usuario = 1,
                    id_categoria = 2,
                    id_cuenta = 1,
                    monto = 2000.0,
                    fecha = LocalDate.now(),
                    descripcion = "Salario",
                    tipo = "Ingreso"
                ),
                categoria = CategoriaEntity(
                    id_categoria = 2,
                    nombre = "Sueldo",
                    tipo = "Ingreso"
                ),
                cuenta = CuentaEntity(
                    id_cuenta = 1,
                    nombre = "Cuenta Principal"
                ),
                usuario = UsuarioEntity(
                    email = "",
                    fecha_registro = LocalDate.now(),
                    id_usuario = 1,
                    nombre = "Usuario demo"
                )
            )
        ),
        initialFecha = LocalDate.now()
    )
}