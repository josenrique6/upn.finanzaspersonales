package com.example.upnfinanzaspersonales

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.upnfinanzaspersonales.data.local.database.AppDatabase
import com.example.upnfinanzaspersonales.data.local.database.DatabaseInitializer
import com.example.upnfinanzaspersonales.ui.theme.UpnfinanzaspersonalesTheme
import com.example.upnfinanzaspersonales.presentation.ui.screens.*
import com.example.upnfinanzaspersonales.domain.model.Transaccion
import com.example.upnfinanzaspersonales.presentation.ui.viewmodel.TransaccionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //inicializar base de datos
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_finanzas_db"
        ).build()

        DatabaseInitializer.insertarDatosPorDefecto(
            db.cuentaDao(),
            db.categoriaDao(),
            db.usuarioDao(),
            CoroutineScope(Dispatchers.IO)
        )

        setContent {
            UpnfinanzaspersonalesTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { padding ->
                        // Contenido de la aplicación
                        AppFinanzasNavHost(db)
                    }
                )
            }

        }
    }
}

@Composable
fun AppFinanzasNavHost(db: AppDatabase) {
    val navController = rememberNavController()

    // Inicializar ViewModel (de forma manual aquí)
    val viewModel = TransaccionViewModel(
        cuentaDao = db.cuentaDao(),
        categoriaDao = db.categoriaDao(),
        transaccionDao = db.transaccionDao()
    )

    NavHost(
        navController = navController,
        startDestination = "lista"
    ) {
        composable("lista") {
            val transacciones by viewModel.transacciones.collectAsState()
            ListaTransaccionesScreen(
                transacciones = transacciones,
                onAgregarClick = {
                    navController.navigate("registro")
                },
                onVerEstadisticas = {
                    navController.navigate("estadisticas")
                }
            )
        }

        composable("registro") {
            val cuentas by viewModel.cuentas.collectAsState()
            val categorias by viewModel.categorias.collectAsState()

            RegistroTransaccionScreen(
                cuentas = cuentas,
                categorias = categorias,
                onGuardarClick = { nuevaTransaccion ->
                    viewModel.registrar(nuevaTransaccion)
                    navController.popBackStack()
                }
            )
        }

        composable("estadisticas") {
            val transacciones by viewModel.transacciones.collectAsState()

            EstadisticasScreen(
                transacciones = transacciones,
                initialFecha = LocalDate.now()
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    UpnfinanzaspersonalesTheme {
        Greeting("Android")
    }
}