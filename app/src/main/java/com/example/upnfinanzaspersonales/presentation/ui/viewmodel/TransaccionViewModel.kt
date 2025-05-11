package com.example.upnfinanzaspersonales.presentation.ui.viewmodel

import androidx.lifecycle.*
import com.example.upnfinanzaspersonales.data.local.dao.*
import com.example.upnfinanzaspersonales.data.local.entities.TransaccionEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// ViewModel que actúa como intermediario entre la capa de datos (DAO) y la UI
class TransaccionViewModel(
    private val cuentaDao: CuentaDao, // DAO para acceder a las cuentas
    private val categoriaDao: CategoriaDao, // DAO para acceder a las categorías
    private val transaccionDao: TransaccionDao // DAO para acceder a las transacciones
) : ViewModel() {

    // Estado interno para almacenar el tipo de transacción seleccionado (Gasto o Ingreso)
    private val _tipoSeleccionado = MutableStateFlow("Gasto")

    // Flujo que obtiene la lista de cuentas desde el DAO y la mantiene actualizada
    val cuentas = cuentaDao.obtenerCuentas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flujo que obtiene las categorías filtradas por tipo desde el DAO
    val categorias = categoriaDao.obtenerCategoriasPorTipo()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Flujo que obtiene las transacciones enriquecidas con detalles desde el DAO
    val transacciones = transaccionDao
        .obtenerConDetalles(idUsuario = 1) // Filtra por el ID del usuario (puede parametrizarse)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Metodo para registrar una nueva transacción en la base de datos
    fun registrar(transaccion: TransaccionEntity) {
        viewModelScope.launch {
            transaccionDao.insertar(transaccion) // Inserta la transacción en la base de datos
        }
    }

    // Metodo para cambiar el tipo de transacción seleccionado (Gasto o Ingreso)
    fun cambiarTipo(tipo: String) {
        _tipoSeleccionado.value = tipo // Actualiza el estado interno
    }
}