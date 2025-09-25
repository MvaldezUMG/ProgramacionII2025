package com.example.clase7

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.IconButton
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.example.clase7.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

fun saveUser(db: FirebaseFirestore, user: User, navController: NavController) {
    db.collection(USERS_COLLECTION)
        .add(user)
        .addOnSuccessListener { documentReference ->
            navController.navigate("users")
        }
}

@Composable
fun UsersFormScreen(navController: NavController){

    var stateEmail by remember {mutableStateOf("")}
    var stateRoles by remember {mutableStateOf("")}

    var stateMessage by remember {mutableStateOf("")}

    val db = Firebase.firestore


    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =Arrangement.Center
    ){


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
//            supportingText = {
//                if (emailMessage.isNotEmpty()){
//                    Text(
//                        text=emailMessage,
//                        color=Color.Red
//                    )
//                }
//            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = stateRoles,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "email icon"
                )
            },
            onValueChange = {stateRoles = it},
            label = {Text("Roles")},
//            supportingText = {
//                if (emailMessage.isNotEmpty()){
//                    Text(
//                        text=emailMessage,
//                        color=Color.Red
//                    )
//                }
//            }
        )

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                val user = User("", stateEmail, stateRoles)
                saveUser(db, user, navController)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC9252B),
                contentColor = Color.White
            )
        ){
            Text("Guardar")
        }

        IconButton(onClick = {navController.popBackStack()}){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription= "icon register"
            )
        }

        Text(
            text = stateMessage
        )
    }
}