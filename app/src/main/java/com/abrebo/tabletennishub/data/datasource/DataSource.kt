package com.abrebo.tabletennishub.data.datasource

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.abrebo.tabletennishub.data.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class DataSource(var collectionReference: CollectionReference,
                 var collectionReferenceUserFriends: CollectionReference) {
    var userList = MutableLiveData<List<User>>()
    var userFriendsList = MutableLiveData<List<User>>()

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

    //userFriend
    fun sendFriendRequest(context: Context, currentUserEmail: String, friendUserEmail: String) {
        val firestore = FirebaseFirestore.getInstance()
        val usersCollection = firestore.collection("KullanıcıArkadaşları")

        val currentUserDocRef = usersCollection.document(currentUserEmail)
        val friendUserDocRef = usersCollection.document(friendUserEmail)

        // Arkadaşlık isteği gönderme işlemi
        firestore.runBatch { batch ->
            // Mevcut kullanıcının 'gönderilen' listesine ekle
            batch.set(
                currentUserDocRef,
                mapOf("istekler.gönderilen" to FieldValue.arrayUnion(friendUserEmail)),
                SetOptions.merge()
            )

            // Arkadaşın 'alınan' listesine ekle
            batch.set(
                friendUserDocRef,
                mapOf("istekler.alınan" to FieldValue.arrayUnion(currentUserEmail)),
                SetOptions.merge()
            )
        }.addOnSuccessListener {
            Toast.makeText(context, "Arkadaşlık isteği gönderildi.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(context, "İstek gönderilirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }









}