package com.example.upnfinanzaspersonales.data.local.database

import com.example.upnfinanzaspersonales.data.local.dao.*
import com.example.upnfinanzaspersonales.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate

object DatabaseInitializer {
    fun insertarDatosPorDefecto(
        cuentaDao: CuentaDao,
        categoriaDao: CategoriaDao,
        usuarioDao: UsuarioDao,
        scope: CoroutineScope
    ) {
        scope.launch {
            // Verificar si ya hay un usuario
            val usuarios = usuarioDao.obtenerUsuarios().firstOrNull()
            if (usuarios.isNullOrEmpty()) {
                usuarioDao.insertar(
                    UsuarioEntity(
                        id_usuario = 1,
                        nombre = "Usuario demo",
                        email = "usuario@demo.com",
                        fecha_registro = LocalDate.now()
                    )
                )
            }

            // Verificar cuentas
            val cuentasExistentes = cuentaDao.obtenerCuentas().firstOrNull()
            if (cuentasExistentes.isNullOrEmpty()) {
                val cuentas = listOf("Efectivo", "Tarjeta de crédito", "Cuenta bancaria")
                cuentaDao.insertarTodas(cuentas.map { CuentaEntity(nombre = it) })
            }

            // Verificar categorías
            val categoriasExistentes = categoriaDao.obtenerCategoriasPorTipo("Gasto").firstOrNull()
            if (categoriasExistentes.isNullOrEmpty()) {
                val categorias = listOf(
                    CategoriaEntity(nombre = "Sueldo", tipo = "Ingreso"),
                    CategoriaEntity(nombre = "Regalo", tipo = "Ingreso"),
                    CategoriaEntity(nombre = "Comida", tipo = "Gasto"),
                    CategoriaEntity(nombre = "Transporte", tipo = "Gasto"),
                    CategoriaEntity(nombre = "Entretenimiento", tipo = "Gasto")
                )
                categoriaDao.insertarTodas(categorias)
            }
        }
    }
}