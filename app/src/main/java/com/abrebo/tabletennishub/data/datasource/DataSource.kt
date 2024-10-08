package com.abrebo.tabletennishub.data.datasource

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.abrebo.tabletennishub.data.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import kotlin.math.log

class DataSource(var collectionReference: CollectionReference,
                 var collectionReferenceUserFriends: CollectionReference) {
    var userList = MutableLiveData<List<User>>()
    var userFriendsList = MutableLiveData<List<User>>()
    val firestore = FirebaseFirestore.getInstance()
    fun uploadUser(): MutableLiveData<List<User>> {
        collectionReference.addSnapshotListener { value, error ->
            if (value != null) {
                val list = ArrayList<User>()

                for (d in value.documents) {
                    val user = d.toObject(User::class.java)
                    if (user != null) {
                        user.id = d.id
                        list.add(user)
                    }
                }
                userList.value = list
            }
        }
        return userList
    }

    fun search(word: String): MutableLiveData<List<User>> {
        collectionReference.addSnapshotListener { value, error ->
            if (value != null) {
                val list = ArrayList<User>()

                for (d in value.documents) {
                    val user = d.toObject(User::class.java)
                    if (user != null) {
                        if (user.userName!!.lowercase().contains(word.lowercase())) {
                            user.id = d.id
                            list.add(user)
                        }
                    }
                }
                userList.value = list
            }
        }
        return userList
    }

    fun saveUser(user: User) {
        collectionReference.document().set(user)
    }

    fun deleteUser(userId: String) {
        collectionReference.document(userId).delete()
    }

    fun updateUser(user: User) {
        val newUser = HashMap<String, Any>()
        newUser["AdSoyad"] = user.nameFamily!!
        newUser["KullanıcıAdı"] = user.userName!!
        newUser["Email"] = user.email!!
        collectionReference.document(user.id!!).update(newUser)
    }

    suspend fun checkUserNameAvailability(userName: String): Boolean {
        return try {
            val querySnapshot = collectionReference
                .whereEqualTo("KullanıcıAdı", userName)
                .get()
                .await()

            querySnapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserNameByEmail(userEmail: String): String? {
        return try {
            val querySnapshot = firestore.collection("Kullanıcılar")
                .whereEqualTo("email", userEmail)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                document.getString("userName")
            } else {
                Log.e("User", "Kullanıcı belgesi bulunamadı")
                null
            }
        } catch (e: Exception) {
            Log.e("User", "Error getting displayName: ${e.message}")
            null
        }
    }

    //userFriend
    fun sendFriendRequest(context: Context, currentUserName: String, friendUserName: String) {
        val currentUserDocRef = collectionReferenceUserFriends.document(currentUserName)
        val friendUserDocRef = collectionReferenceUserFriends.document(friendUserName)

        firestore.runBatch { batch ->
            batch.set(
                currentUserDocRef,
                mapOf("istekler.gönderilen" to FieldValue.arrayUnion(friendUserName)),
                SetOptions.merge()
            )
            batch.set(
                friendUserDocRef,
                mapOf("istekler.alınan" to FieldValue.arrayUnion(currentUserName)),
                SetOptions.merge()
            )
        }.addOnSuccessListener {
            Toast.makeText(context, "Arkadaşlık isteği gönderildi.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(context, "İstek gönderilirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun getReceivedRequests(currentUserName: String, callback: (List<String>) -> Unit) {
        getRequests(currentUserName, "istekler.alınan", callback)
    }

    fun getSentRequests(currentUserName: String, callback: (List<String>) -> Unit) {
        getRequests(currentUserName, "istekler.gönderilen", callback)
    }

    private fun getRequests(currentUserName: String, field: String, callback: (List<String>) -> Unit) {
        val documentRef: DocumentReference = collectionReferenceUserFriends.document(currentUserName)
        documentRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentSnapshot = task.result
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Belge verisini Map olarak al
                        val data = documentSnapshot.data
                        // Belirtilen alana erişim
                        val requests = (data?.get(field) as? List<String>) ?: emptyList()
                        callback(requests)
                    } else {
                        Log.e("Firestore Error", "Belge mevcut değil")
                        callback(emptyList())
                    }
                } else {
                    Log.e("Request Error", task.exception?.message.toString())
                    callback(emptyList())
                }
            }
    }

}