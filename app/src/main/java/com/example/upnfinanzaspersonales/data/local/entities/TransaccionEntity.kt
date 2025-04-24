package com.example.upnfinanzaspersonales.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "transacciones",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id_usuario"],
            childColumns = ["id_usuario"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CuentaEntity::class,
            parentColumns = ["id_cuenta"],
            childColumns = ["id_cuenta"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id_categoria"],
            childColumns = ["id_categoria"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["id_usuario"]),
        Index(value = ["id_cuenta"]),
        Index(value = ["id_categoria"])
    ]
)
data class TransaccionEntity(
    @PrimaryKey(autoGenerate = true) val id_transaccion: Int = 0,
    val id_usuario: Int,
    val id_categoria: Int,
    val id_cuenta: Int,
    val monto: Double,
    val fecha: LocalDate,
    val descripcion: String?,
    val tipo: String // "Ingreso" o "Gasto"
)