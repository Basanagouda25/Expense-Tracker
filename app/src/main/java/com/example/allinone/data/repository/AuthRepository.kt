package com.example.allinone.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // this is call back based function because the firebase is asynchronous

    // now we use coroutines here and the suspend function is used because it can pause and resume the firebase calls
    suspend fun login(
        email : String,
        password : String,
    ): Result<String>{
        return try {
            auth.signInWithEmailAndPassword(email,password).await() //await is used now coroutines will wait until firebase finishes it's call
            Result.success("Login Sucessfull")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun register(
        name : String,
        phoneNumber : String,
        email : String,
        password : String,
    ):Result<String>{
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email,password).await()
            val uid = authResult.user?.uid?:return Result.failure(Exception("Id is null"))

            //user profile
            val userMap = hashMapOf(
                "uid" to uid,
                "name" to name,
                "phoneNumber" to phoneNumber,
                "email" to email,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("users")
                .document(uid)
                .set(userMap)
                .await()
            Result.success("Registrstion Successfull")
        } catch (e : Exception){
            Result.failure(e)
        }
    }
}