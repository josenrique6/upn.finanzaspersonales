package com.example.upnfinanzaspersonales

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
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
import com.example.upnfinanzaspersonales.presentation.ui.viewmodel.TransaccionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Habilita el diseño de borde a borde en la actividad.

        // Inicializa la base de datos Room.
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_finanzas_db" // Nombre de la base de datos.
        ).build()

        // Inserta datos por defecto en la base de datos.
        DatabaseInitializer.insertarDatosPorDefecto(
            db.cuentaDao(),
            db.categoriaDao(),
            db.usuarioDao(),
            CoroutineScope(Dispatchers.IO) // Ejecuta en un hilo de IO.
        )

        setContent {
            UpnfinanzaspersonalesTheme {
                // Configura el diseño principal de la aplicación.
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { padding ->
                        // Contenedor principal de navegación.
                        AppFinanzasNavHost(db)
                    }
                )
            }
        }
    }
}

@Composable
fun AppFinanzasNavHost(db: AppDatabase) {
    val navController = rememberNavController() // Controlador de navegación.

    // Inicializa el ViewModel manualmente.
    val viewModel = TransaccionViewModel(
        cuentaDao = db.cuentaDao(),
        categoriaDao = db.categoriaDao(),
        transaccionDao = db.transaccionDao()
    )

    // Configura las rutas de navegación.
    NavHost(
        navController = navController,
        startDestination = "login" // Pantalla inicial.
    ) {
        // Pantalla de inicio de sesión.
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("lista") // Navega a la lista de transacciones al iniciar sesión.
            })
        }

        // Pantalla de lista de transacciones.
        composable("lista") {
            val transacciones by viewModel.transacciones.collectAsState() // Observa las transacciones.
            ListaTransaccionesScreen(
                transacciones = transacciones,
                onAgregarClick = {
                    navController.navigate("registro") // Navega a la pantalla de registro.
                },
                onVerEstadisticas = {
                    navController.navigate("estadisticas") // Navega a la pantalla de estadísticas.
                }
            )
        }

        // Pantalla de registro de transacciones.
        composable("registro") {
            val cuentas by viewModel.cuentas.collectAsState() // Observa las cuentas.
            val categorias by viewModel.categorias.collectAsState() // Observa las categorías.

            RegistroTransaccionScreen(
                cuentas = cuentas,
                categorias = categorias,
                onGuardarClick = { nuevaTransaccion ->
                    viewModel.registrar(nuevaTransaccion) // Registra la nueva transacción.
                    navController.popBackStack() // Vuelve a la pantalla anterior.
                }
            )
        }

        // Pantalla de estadísticas.
        composable("estadisticas") {
            val transacciones by viewModel.transacciones.collectAsState() // Observa las transacciones.

            EstadisticasScreen(
                transacciones = transacciones,
                initialFecha = LocalDate.now() // Fecha inicial para las estadísticas.
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    // Componente simple que muestra un saludo.
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    // Vista previa del componente Greeting.
    UpnfinanzaspersonalesTheme {
        Greeting("Android")
    }
}