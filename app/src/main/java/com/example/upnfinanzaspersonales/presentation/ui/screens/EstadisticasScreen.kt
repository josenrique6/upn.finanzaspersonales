package com.example.upnfinanzaspersonales.presentation.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.upnfinanzaspersonales.data.local.entities.CategoriaEntity
import com.example.upnfinanzaspersonales.data.local.entities.CuentaEntity
import com.example.upnfinanzaspersonales.data.local.entities.TransaccionEntity
import com.example.upnfinanzaspersonales.data.local.entities.UsuarioEntity
// Importa el tema de tu aplicación si es necesario para la Preview
// import com.example.upnfinanzaspersonales.ui.theme.UPNFinanzasPersonalesTheme
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import kotlin.math.roundToInt

// Enum para el tipo de filtro de temporalidad
enum class FiltroTemporalidad {
    DIA, SEMANA, MES
}

@Composable
fun EstadisticasScreen(
    transacciones: List<TransaccionConDetalles>,
    initialFecha: LocalDate
) {
    var fechaReferencia by remember { mutableStateOf(initialFecha) }
    var filtroTemporalidadSeleccionado by remember { mutableStateOf(FiltroTemporalidad.DIA) }
    var tipoTransaccionSeleccionado by remember { mutableStateOf("Gasto") } // "Gasto" o "Ingreso"
    val context = LocalContext.current

    val formatoDia = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("es", "ES"))
    val formatoMesAno = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))

    val (fechaInicioPeriodo, fechaFinPeriodo) = remember(fechaReferencia, filtroTemporalidadSeleccionado) {
        when (filtroTemporalidadSeleccionado) {
            FiltroTemporalidad.DIA -> fechaReferencia to fechaReferencia
            FiltroTemporalidad.SEMANA -> {
                val inicioSemana = fechaReferencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val finSemana = fechaReferencia.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                inicioSemana to finSemana
            }
            FiltroTemporalidad.MES -> {
                val inicioMes = fechaReferencia.withDayOfMonth(1)
                val finMes = fechaReferencia.withDayOfMonth(fechaReferencia.lengthOfMonth())
                inicioMes to finMes
            }
        }
    }

    val datePickerDialog = remember(fechaReferencia.year, fechaReferencia.monthValue, fechaReferencia.dayOfMonth) {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                fechaReferencia = LocalDate.of(year, month + 1, dayOfMonth)
                filtroTemporalidadSeleccionado = FiltroTemporalidad.DIA
            },
            fechaReferencia.year,
            fechaReferencia.monthValue - 1,
            fechaReferencia.dayOfMonth
        )
    }

    val transaccionesEnPeriodo = transacciones.filter {
        !it.transaccion.fecha.isBefore(fechaInicioPeriodo) && !it.transaccion.fecha.isAfter(fechaFinPeriodo)
    }

    val totalIngreso = transaccionesEnPeriodo
        .filter { it.transaccion.tipo == "Ingreso" }
        .sumOf { it.transaccion.monto }
    val totalGasto = transaccionesEnPeriodo
        .filter { it.transaccion.tipo == "Gasto" }
        .sumOf { it.transaccion.monto }
    val balancePeriodo = totalIngreso - totalGasto

    val transaccionesFiltradasPorTipoYPeriodo = transaccionesEnPeriodo.filter {
        it.transaccion.tipo == tipoTransaccionSeleccionado
    }
    val resumen = transaccionesFiltradasPorTipoYPeriodo
        .groupBy { it.categoria.nombre }
        .mapValues { it.value.sumOf { t -> t.transaccion.monto } }
    val totalCategoriasEnResumen = resumen.values.sum().takeIf { it > 0 } ?: 1.0

    val colores = mapOf(
        "Sueldo" to Color(0xFF81C784), "Ropa" to Color(0xFFEF5350),
        "Entretenimiento" to Color(0xFFFF8A65), "Comida" to Color(0xFFFFEB3B),
        "Transporte" to Color(0xFF6B3B94), "Regalo" to Color(0xFF64B5F6),
        "Servicios" to Color(0xFF26A69A), "Salud" to Color(0xFFEC407A),
        "Educación" to Color(0xFF5C6BC0), "Otros Ingresos" to Color(0xFFA1887F),
        "Otros Gastos" to Color(0xFF78909C)
    ).withDefault { Color.Gray }

    val emojiMap = mapOf(
        "Ropa" to "👗", "Entretenimiento" to "🎭", "Comida" to "🍔",
        "Transporte" to "🚗", "Regalo" to "🎁", "Sueldo" to "💰",
        "Servicios" to "💡", "Salud" to "⚕️", "Educación" to "📚",
        "Otros Ingresos" to "📈", "Otros Gastos" to "💸"
    ).withDefault { "❔" }

    val pieData = resumen.entries.map { (cat, monto) ->
        Pie(
            label = cat, // La etiqueta se usa en la leyenda, no dentro del pie en esta configuración
            data = monto,
            color = colores.getValue(cat),
            selectedColor = colores.getValue(cat).copy(alpha = 0.8f)
        )
    }

    fun cambiarFecha(incremento: Long) {
        fechaReferencia = when (filtroTemporalidadSeleccionado) {
            FiltroTemporalidad.DIA -> fechaReferencia.plusDays(incremento)
            FiltroTemporalidad.SEMANA -> fechaReferencia.plusWeeks(incremento)
            FiltroTemporalidad.MES -> fechaReferencia.plusMonths(incremento)
        }
    }

    val textoRangoFechas = when (filtroTemporalidadSeleccionado) {
        FiltroTemporalidad.DIA -> fechaReferencia.format(formatoDia)
        FiltroTemporalidad.SEMANA -> "${fechaInicioPeriodo.format(formatoDia)} - ${fechaFinPeriodo.format(formatoDia)}"
        FiltroTemporalidad.MES -> fechaReferencia.format(formatoMesAno).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Estadísticas",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), // Reducido padding superior, añadido inferior
            textAlign = TextAlign.Center
        )

        Text(
            text = "Balance del Período: S/ %.2f".format(balancePeriodo),
            style = MaterialTheme.typography.titleLarge, // Estilo ajustado
            color = if (balancePeriodo >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), // Aumentado padding inferior
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FiltroTemporalidad.values().forEach { filtro ->
                OutlinedButton(
                    onClick = { filtroTemporalidadSeleccionado = filtro },
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (filtroTemporalidadSeleccionado == filtro) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                        contentColor = if (filtroTemporalidadSeleccionado == filtro) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(filtro.name.lowercase().replaceFirstChar { it.titlecase() })
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { cambiarFecha(-1) }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Periodo anterior")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { datePickerDialog.show() }.padding(horizontal = 4.dp) // Padding para mejorar área de click
            ) {
                Text(
                    text = textoRangoFechas,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = "Seleccionar fecha",
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = { cambiarFecha(1) }) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Periodo siguiente")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = if (tipoTransaccionSeleccionado == "Ingreso") 0 else 1,
            modifier = Modifier.fillMaxWidth(),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[if (tipoTransaccionSeleccionado == "Ingreso") 0 else 1]),
                    height = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            Tab(
                selected = tipoTransaccionSeleccionado == "Ingreso",
                onClick = { tipoTransaccionSeleccionado = "Ingreso" },
                text = { Text("Ingresos S/. %.2f".format(totalIngreso), fontWeight = if(tipoTransaccionSeleccionado == "Ingreso") FontWeight.Bold else FontWeight.Normal) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Tab(
                selected = tipoTransaccionSeleccionado == "Gasto",
                onClick = { tipoTransaccionSeleccionado = "Gasto" },
                text = { Text("Gastos S/. %.2f".format(totalGasto), fontWeight = if(tipoTransaccionSeleccionado == "Gasto") FontWeight.Bold else FontWeight.Normal) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(20.dp)) // Aumentado espacio antes del gráfico

        // --- PieChart
        if (pieData.isNotEmpty()) {
            Box( // Contenedor para el PieChart y el texto central
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp), // Altura para el donut y el texto
                contentAlignment = Alignment.Center
            ) {
                PieChart(
                    modifier = Modifier.fillMaxSize(), // El PieChart ocupa todo el Box
                    data = pieData,
                    onPieClick = { /* TODO: Podrías implementar algo aquí, como mostrar detalles de la categoría clickeada */ },
                    selectedScale = 1.05f, // Escala de la sección al ser seleccionada (si onPieClick se usa para seleccionar)
                )
                // Texto en el centro del Donut Chart
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (tipoTransaccionSeleccionado == "Ingreso") "Total Ingresos" else "Total Gastos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Color sutil para la etiqueta
                    )
                    Text(
                        text = "S/ %.2f".format(if (tipoTransaccionSeleccionado == "Ingreso") totalIngreso else totalGasto),
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface // Color principal para el monto
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay ${tipoTransaccionSeleccionado.lowercase()}s para mostrar en este período.")
            }
        }

        Spacer(modifier = Modifier.height(20.dp)) // Aumentado espacio después del gráfico

        if (resumen.isNotEmpty()) {
            Column {
                Text(
                    text = "Detalle de ${tipoTransaccionSeleccionado.lowercase()}s por categoría:",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                resumen.entries.sortedByDescending { it.value }.forEach { (categoria, monto) ->
                    val porcentaje = (monto * 100 / totalCategoriasEnResumen).roundToInt()
                    val colorCat = colores.getValue(categoria)
                    val emoji = emojiMap.getValue(categoria)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(colorCat, shape = RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp)) // Aumentado espacio
                        Text(
                            text = "$emoji $categoria",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1.5f)
                        )
                        Text(
                            text = "$porcentaje%",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.weight(0.6f), // Ajustado peso
                            textAlign = TextAlign.End
                        )
                        Spacer(modifier = Modifier.width(10.dp)) // Aumentado espacio
                        Text(
                            text = "S/ %.2f".format(monto),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        } else if (pieData.isEmpty() && transaccionesEnPeriodo.isNotEmpty()) {
            Box( // Este caso es cuando hay transacciones en el período, pero no del tipo seleccionado (Ingreso/Gasto)
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                // El mensaje "No hay ${tipoTransaccionSeleccionado.lowercase()}s para mostrar..." ya cubre esto arriba
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
