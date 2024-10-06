package com.abrebo.tabletennishub.data.repo

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.abrebo.tabletennishub.data.datasource.DataSource
import com.abrebo.tabletennishub.data.model.User

class Repository(var dataSource: DataSource) {
    fun uploadUser(): MutableLiveData<List<User>> = dataSource.uploadUser()
    fun search(word:String): MutableLiveData<List<User>> = dataSource.search(word)
    fun saveUser(user:User) = dataSource.saveUser(user)
    fun deleteUser(userId:String) = dataSource.deleteUser(userId)
    fun updateUser(user:User) = dataSource.updateUser(user)
    suspend fun checkUserNameAvailability(userName: String): Boolean = dataSource.checkUserNameAvailability(userName)
    fun sendFriendRequest(context: Context, currentUserEmail: String, friendUserEmail: String)
    =dataSource.sendFriendRequest(context, currentUserEmail, friendUserEmail)


}