package com.example.upnfinanzaspersonales.presentation.ui.viewmodel

import androidx.lifecycle.*
import com.example.upnfinanzaspersonales.data.local.dao.*
import com.example.upnfinanzaspersonales.data.local.entities.TransaccionEntity
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

//actúa como intermediaria entre la capa de datos (DAO) y la interfaz de usuario (UI)
class TransaccionViewModel(
    private val cuentaDao: CuentaDao,
    private val categoriaDao: CategoriaDao,
    private val transaccionDao: TransaccionDao
) : ViewModel() {

    private val _tipoSeleccionado = MutableStateFlow("Gasto")

    val cuentas = cuentaDao.obtenerCuentas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categorias = categoriaDao.obtenerCategoriasPorTipo()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ✅ Transacciones con detalles enriquecidos
    val transacciones = transaccionDao
        .obtenerConDetalles(idUsuario = 1) // puedes parametrizar si luego hay más usuarios
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun registrar(transaccion: TransaccionEntity) {
        viewModelScope.launch {
            transaccionDao.insertar(transaccion)
        }
    }
    fun cambiarTipo(tipo: String) {
        _tipoSeleccionado.value = tipo
    }
}