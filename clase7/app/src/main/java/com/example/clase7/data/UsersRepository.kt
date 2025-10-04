package com.example.clase7.data

import android.content.res.Resources
import androidx.navigation.NavController
import com.example.clase7.R
import com.example.clase7.SCREEN_USERS
import com.example.clase7.models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

const val USERS_COLLECTION = "users"

class UsersRepository(private val resources: Resources) {

    private val db = Firebase.firestore

    fun saveUser(user: User, navController: NavController) {
        db.collection(USERS_COLLECTION)
            .add(user)
            .addOnSuccessListener { documentReference ->
                navController.navigate(SCREEN_USERS)
            }
    }

    suspend fun getUsers () : List<User> {
        val snapshot = db.collection(USERS_COLLECTION)
            .get()
            .await()

        val usersList = snapshot.documents.map{doc ->
            doc.toObject<User>()?.copy(id = doc.id, email = doc.get("email").toString(), doc.get("roles").toString())
                ?: User()
        }
        return usersList
    }
}