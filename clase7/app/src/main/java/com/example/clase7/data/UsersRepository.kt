package com.example.clase7.data
import android.content.res.Resources
import androidx.navigation.NavController
import com.example.clase7.R
import com.example.clase7.SCREEN_USERS
import com.example.clase7.models.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

const val USERS_COLLECTION = "users"

class UsersRepository(private val resources: Resources) {

    private val db = Firebase.firestore

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

    suspend fun getUserById (id: String) : User?{
        val docRef = db.collection(USERS_COLLECTION).document(id)
        val snapshot = docRef.get().await()

        if (snapshot == null){
            return null
        }
        val user = User(snapshot.get("id").toString(), snapshot.get("email").toString(), snapshot.get("roles").toString())
        return user
    }

    suspend fun getUserByEmail (email: String) : User?{
        val snapshot = db.collection(USERS_COLLECTION)
            .whereEqualTo("email", email)
            .get()
            .await()
        if (snapshot.documents.count() == 0){
            return null
        }
        val document = snapshot.documents[0]
        val user = User(document.get("id").toString(), email, document.get("roles").toString())
        return user
    }

    fun saveUser(user: User, navController: NavController) {
        if (user.id != ""){
            //Update
            val documentRef = db.collection(USERS_COLLECTION).document(user.id)
            documentRef.set(user)
                .addOnSuccessListener { documentRef ->
                    navController.navigate(SCREEN_USERS)
                }
        }
        else{
            db.collection(USERS_COLLECTION)
                .add(user)
                .addOnSuccessListener { documentReference ->
                    navController.navigate(SCREEN_USERS)
                }
        }
    }

}