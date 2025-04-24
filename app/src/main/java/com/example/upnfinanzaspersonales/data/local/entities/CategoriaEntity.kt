package com.example.upnfinanzaspersonales.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey(autoGenerate = true) val id_categoria: Int = 0,
    val nombre: String,
    val tipo: String // Ej: "Ingreso", "Gasto"
)