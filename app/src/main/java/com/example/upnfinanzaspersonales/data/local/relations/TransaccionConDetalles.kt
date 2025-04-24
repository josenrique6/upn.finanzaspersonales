package com.example.upnfinanzaspersonales.data.local.relations


import androidx.room.Embedded
import androidx.room.Relation
import com.example.upnfinanzaspersonales.data.local.entities.*

data class TransaccionConDetalles(
    @Embedded val transaccion: TransaccionEntity,

    @Relation(
        parentColumn = "id_cuenta",
        entityColumn = "id_cuenta"
    )
    val cuenta: CuentaEntity,

    @Relation(
        parentColumn = "id_categoria",
        entityColumn = "id_categoria"
    )
    val categoria: CategoriaEntity,

    @Relation(
        parentColumn = "id_usuario",
        entityColumn = "id_usuario"
    )
    val usuario: UsuarioEntity
)