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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@OptIn(ExperimentalMaterial3Api::class)
//@Preview
@Composable
fun RegisterScreen(navController: NavController){
    val context = LocalContext.current
    val auth = Firebase.auth

    var stateEmail by remember {mutableStateOf("")}
    var statePassword by remember {mutableStateOf("")}
    var stateConfirmPassword by remember {mutableStateOf("")}

    val activity = LocalView.current.context as Activity

    Scaffold (
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}){
                         Icon(
                             imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                             contentDescription= stringResource(R.string.content_description_icon_exit)
                         )
                    }
                }
            )
        }
    ){ paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Image(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.content_description_icon_person),
                modifier = Modifier.size(200.dp)
            )
            Text(
                text = stringResource(R.string.register_screen_text),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0066B3)
            )
            Spacer(
                modifier = Modifier.height(20.dp)
            )
            OutlinedTextField(
                value = stateEmail,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = stringResource(R.string.content_description_icon_email)
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
                        contentDescription = stringResource(R.string.content_description_icon_password)
                    )
                },
                onValueChange = {statePassword = it},
                label = {Text(stringResource(R.string.fields_password))}
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = stateConfirmPassword,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = stringResource(R.string.content_description_icon_confirm_password)
                    )
                },
                onValueChange = {stateConfirmPassword = it},
                label = {Text(stringResource(R.string.fields_confirm_password))}
            )
            Button(
                onClick = {
                    if (statePassword == stateConfirmPassword) {
                        auth.createUserWithEmailAndPassword(stateEmail, statePassword)
                            .addOnCompleteListener(activity) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(activity, context.getString(R.string.register_screen_success), Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(
                                        activity,
                                        "Error: ${task.exception?.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }else{
                        Toast.makeText(activity, context.getString(R.string.register_screen_password_error), Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFC9252B),
                    contentColor = Color.White
                )

            ){
                Text(stringResource(R.string.register_screen_register_button) )
            }
        }
    }
}