package com.example.upnfinanzaspersonales.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.upnfinanzaspersonales.data.local.dao.*
import com.example.upnfinanzaspersonales.data.local.entities.CategoriaEntity
import com.example.upnfinanzaspersonales.data.local.entities.CuentaEntity
import com.example.upnfinanzaspersonales.data.local.entities.UsuarioEntity
import com.example.upnfinanzaspersonales.data.local.entities.TransaccionEntity

@Database(
    entities = [
        UsuarioEntity::class,
        CuentaEntity::class,
        CategoriaEntity::class,
        TransaccionEntity::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transaccionDao(): TransaccionDao
    abstract fun cuentaDao(): CuentaDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun usuarioDao(): UsuarioDao
}