package com.example.upnfinanzaspersonales.data.local.dao

import androidx.room.*
import com.example.upnfinanzaspersonales.data.local.entities.CategoriaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    @Query("SELECT * FROM categorias WHERE tipo = :tipo ORDER BY nombre ASC")
    fun obtenerCategoriasPorTipo(tipo: String): Flow<List<CategoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(categoria: CategoriaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(categorias: List<CategoriaEntity>)
}