package com.example.clase7

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun LoginScreen(navController: NavController){

    val auth = Firebase.auth

    var stateEmail by remember {mutableStateOf("")}
    var statePassword by remember {mutableStateOf("")}

    var stateMessage by remember {mutableStateOf("")}

    val activity = LocalView.current.context as Activity

    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =Arrangement.Center
    ){
        //TODO: Buscar como agregar imagenes como recursos
        Image(
            imageVector = Icons.Filled.Person,
            contentDescription = "User icon",
            modifier = Modifier.size(150.dp)
        )
        Text(
            text = stringResource(R.string.login_screen_text),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = stateEmail,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "email icon"
                )
            },
            onValueChange = {stateEmail = it},
            label = {Text(stringResource(R.string.fields_email))},
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = statePassword,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "password icon"
                )
            },
            onValueChange = {statePassword = it},
            label = {Text(stringResource(R.string.fields_password))}
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                auth.signInWithEmailAndPassword(stateEmail, statePassword)
                    .addOnCompleteListener (activity) {
                        task ->
                        stateMessage = if (task.isSuccessful){
                            navController.navigate("logSuccess")
                            ""
                        } else {
                            "Fallo el inicio de sesion"
                        }
                    }
            },
        ){
            Text(stringResource(R.string.login_screen_login_button) )
        }
        Text(
            text = stateMessage
        )
        Button(
            onClick = {navController.navigate("register")},
            colors =ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Blue
            )
        ){
            Text(stringResource(R.string.login_screen_register_button))
        }
    }
}