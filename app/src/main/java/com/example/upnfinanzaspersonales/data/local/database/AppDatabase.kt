package com.example.upnfinanzaspersonales.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.upnfinanzaspersonales.data.local.dao.*
import com.example.upnfinanzaspersonales.data.local.entities.CategoriaEntity
import com.example.upnfinanzaspersonales.data.local.entities.CuentaEntity
import com.example.upnfinanzaspersonales.data.local.entities.UsuarioEntity
import com.example.upnfinanzaspersonales.data.local.entities.TransaccionEntity

/*
* La clase AppDatabase es la implementación principal de la base de datos en Room,
* que actúa como el punto de acceso a las operaciones de la base de datos en la aplicación.
* Su propósito es definir las entidades y los DAOs (Data Access Objects) que se utilizarán
* para interactuar con las tablas de la base de datos.
Funcionalidades principales:
Definición de entidades:
Especifica las tablas de la base de datos mediante las clases UsuarioEntity, CuentaEntity, CategoriaEntity y TransaccionEntity.
Definición de DAOs:
Proporciona métodos abstractos para obtener instancias de los DAOs (TransaccionDao, CuentaDao, CategoriaDao, UsuarioDao), que contienen las operaciones CRUD para cada tabla.
Conversión de tipos:
Utiliza la clase Converters para manejar la conversión de tipos personalizados, como LocalDate, que no son compatibles de forma nativa con Room.
Gestión de versiones:
Define la versión de la base de datos (version = 1) para manejar futuras migraciones.
En resumen, esta clase centraliza la configuración y el acceso a la base de datos, facilitando la interacción con las tablas y asegurando la consistencia de los datos.
* */

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