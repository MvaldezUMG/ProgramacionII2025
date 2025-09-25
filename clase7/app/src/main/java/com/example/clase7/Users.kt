package com.example.clase7

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

import com.example.clase7.models.User

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


const val USERS_COLLECTION = "users"

suspend fun getUsers (db: FirebaseFirestore) : List<User> {
    val snapshot = db.collection(USERS_COLLECTION)
        .get()
        .await()

    val usersList = snapshot.documents.map{doc ->
        doc.toObject<User>()?.copy(id = doc.id, email = doc.get("email").toString(), doc.get("roles").toString())
          ?: User()
    }
    return usersList
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(navController: NavController) {

    val db = Firebase.firestore
    var users by remember {mutableStateOf(emptyList<User>())}
    var isLoading by remember {mutableStateOf(false)}

    LaunchedEffect(Unit) {
        isLoading = true
        users = getUsers(db)
        isLoading = false
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription= "icon register"
                        )
                    }
                }
            )
        }
    ){ innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        )
        {
            Button(onClick = {navController.navigate("users_form")}){
                Text("Nuevo usuario")
            }
            Spacer(modifier = Modifier.padding(3.dp))
            if (isLoading){
                CircularProgressIndicator()
            }
            else{

                LazyColumn{
                    for (user in users){
                        item(user.id){
                            //Box(modifier = Modifier.border(4.dp, Color.Blue).padding(2.dp)){
                                Text("Email: " + user.email + " Roles:" + user.roles)
                            //}
                        }
                    }
                }
            }
        }
    }
}