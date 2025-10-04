package com.example.clase7

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clase7.ui.theme.Clase7Theme
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        if (FirebaseApp.getApps(this).isEmpty()){
            FirebaseApp.initializeApp(this)
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            Clase7Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MainScreens()
                    }
                }
            }
        }
    }
}

const val SCREEN_LOGIN = "login"
const val SCREEN_REGISTER = "register"
const val SCREEN_HOME = "home"
const val SCREEN_USERS = "users"
const val SCREEN_USERS_FORM = "users_form"

@Composable
fun MainScreens() {

    val context = LocalContext.current

    val navController = rememberNavController()

    var initialScreen: String = SCREEN_LOGIN

    val auth = Firebase.auth
    val currentUser = auth.currentUser

    if (currentUser != null){
        initialScreen = SCREEN_HOME
    }

    NavHost(navController = navController, startDestination = initialScreen) {
        composable(SCREEN_LOGIN) { LoginScreen(navController) }
        composable(SCREEN_REGISTER) { RegisterScreen(navController) }
        composable(SCREEN_HOME) { HomeScreen(navController) }
        composable(SCREEN_USERS) {UserScreen(navController)}
        composable(SCREEN_USERS_FORM) {UsersFormScreen(navController)}
    }
}

