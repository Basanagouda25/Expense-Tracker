package com.example.allinone.data.repository

import com.example.allinone.data.model.Expense
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ExpenseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun addExpense(expense: Expense): Result<String>{
        return try {
            val uid = auth.currentUser?.uid?: return Result.failure(Exception("User Not logged in"))

            val docref = firestore
                .collection("users")
                .document(uid)
                .collection("expenses")
                .document() //firebase auto generates
            val expenseWithId = expense.copy(id = docref.id)
            docref.set(expenseWithId).await() //coroutines wait untill writing complete
            Result.success("Expense Added")
        }catch (e: Exception){
            Result.failure(e)
        }
    }
    fun getExpenses(): Flow<List<Expense>> = callbackFlow{
        //here callbackFlow convert callback based into flow
        val uid = auth.currentUser?.uid?:return@callbackFlow

        val listener = firestore
            .collection("users")
            .document(uid)
            .collection("expenses")
            .orderBy("timestamp")
            /*addSnapshotListener means firestore real time updates
             like when a new document added,updated or deleted
             this listener triggers*/
            .addSnapshotListener { snapshot,error ->
                if (error != null){
                    close(error)
                    return@addSnapshotListener
                }
                val expenses = snapshot?.documents?.mapNotNull {
                    it.toObject(Expense::class.java)
                }?:emptyList()
                trySend(expenses) // this trySend sends updates lists of flow
            }
        // when flow is stopped listener is removed so that memory should not leak
        awaitClose{listener.remove()}
    }
    suspend fun deleteExpense(expenseId: String): Result<String> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User Not logged in"))

            firestore
                .collection("users")
                .document(uid)
                .collection("expenses")
                .document(expenseId) // Target the specific expense ID
                .delete()
                .await() // Wait for deletion to complete

            Result.success("Expense Deleted")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateExpense(expense: Expense): Result<String> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("User Not logged in"))
            firestore.collection("users").document(uid)
                .collection("expenses").document(expense.id) // Point to the existing ID
                .set(expense).await() // Overwrites with new data
            Result.success("Expense Updated")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}