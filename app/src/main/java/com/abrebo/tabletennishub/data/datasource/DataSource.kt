package com.abrebo.tabletennishub.data.datasource

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.abrebo.tabletennishub.data.model.User
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await

class DataSource(var collectionReference: CollectionReference) {
    var userList=MutableLiveData<List<User>>()

    fun uploadUser():MutableLiveData<List<User>>{
        collectionReference.addSnapshotListener { value, error ->
            if (value != null) {
                val list=ArrayList<User>()

                for (d in value.documents){
                    val user=d.toObject(User::class.java)
                    if (user!=null){
                        user.id=d.id
                        list.add(user)
                    }
                }
                userList.value=list
            }
        }
        return userList
    }
    fun search(word:String): MutableLiveData<List<User>> {
        collectionReference.addSnapshotListener { value, error ->
            if(value != null){
                val list = ArrayList<User>()

                for(d in value.documents){
                    val user = d.toObject(User::class.java)
                    if(user != null){
                        if(user.userName!!.lowercase().contains(word.lowercase())){
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

    fun saveUser(user:User){
        collectionReference.document().set(user)
    }
    fun deleteUser(userId:String){
        collectionReference.document(userId).delete()
    }

    fun updateUser(user:User){
        val newUser=HashMap<String,Any>()
        newUser["AdSoyad"]=user.nameFamily!!
        newUser["KullanıcıAdı"]=user.userName!!
        newUser["Email"]=user.email!!
        collectionReference.document(user.id!!).update(newUser)
    }

    suspend fun checkUserNameAvailability(userName: String): Boolean {
        return try {
            val querySnapshot = collectionReference
                .whereEqualTo("KullanıcıAdı", userName)
                .get()
                .await() // Asenkron işlemi bekler

            querySnapshot.isEmpty // Sonuç boşsa, kullanıcı adı yoktur (kullanılabilir)
        } catch (e: Exception) {
            // Hata durumunda false döndür
            false
        }
    }

}