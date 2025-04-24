package com.example.upnfinanzaspersonales.data.local.dao

import androidx.room.*
import com.example.upnfinanzaspersonales.data.local.entities.CuentaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CuentaDao {

    @Query("SELECT * FROM cuentas ORDER BY nombre ASC")
    fun obtenerCuentas(): Flow<List<CuentaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(cuenta: CuentaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(cuentas: List<CuentaEntity>)
}