package com.example.upnfinanzaspersonales.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cuentas")
data class CuentaEntity(
    @PrimaryKey(autoGenerate = true) val id_cuenta: Int = 0,
    val nombre: String
)