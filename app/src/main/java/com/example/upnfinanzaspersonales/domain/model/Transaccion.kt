package com.example.upnfinanzaspersonales.domain.model

import java.time.LocalDate

data class Transaccion(
    val tipo: String, // "Gasto" o "Ingreso"
    val monto: Double,
    val categoria: String,
    val descripcion: String?,
    val fecha: LocalDate
)
