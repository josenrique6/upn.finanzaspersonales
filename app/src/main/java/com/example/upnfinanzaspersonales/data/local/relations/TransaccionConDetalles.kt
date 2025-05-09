package com.example.upnfinanzaspersonales.data.local.relations


import androidx.room.Embedded
import androidx.room.Relation
import com.example.upnfinanzaspersonales.data.local.entities.*
/*
* La clase TransaccionConDetalles es una clase de relación en Room
* que combina datos de varias entidades relacionadas para representar
* una transacción con todos sus detalles asociados. Su propósito
* principal es facilitar la obtención de datos completos de una
* transacción, incluyendo la cuenta, la categoría y el usuario
* relacionados.
* */
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