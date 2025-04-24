package com.example.upnfinanzaspersonales.data.local.dao

import androidx.room.*
import com.example.upnfinanzaspersonales.data.local.entities.TransaccionEntity
import com.example.upnfinanzaspersonales.data.local.relations.TransaccionConDetalles
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaccionDao {

    @Query("SELECT * FROM transacciones ORDER BY fecha DESC")
    fun obtenerTransacciones(): Flow<List<TransaccionEntity>>

    @Query("SELECT * FROM transacciones WHERE id_usuario = :idUsuario ORDER BY fecha DESC")
    fun obtenerPorUsuario(idUsuario: Int): Flow<List<TransaccionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(transaccion: TransaccionEntity)

    @Delete
    suspend fun eliminar(transaccion: TransaccionEntity)

    @Query("DELETE FROM transacciones WHERE id_usuario = :idUsuario")
    suspend fun eliminarTodasPorUsuario(idUsuario: Int)

    @Query("""
    SELECT * FROM transacciones 
    WHERE id_usuario = :idUsuario 
    ORDER BY fecha DESC
""")
    fun obtenerConDetalles(idUsuario: Int): Flow<List<TransaccionConDetalles>>
}