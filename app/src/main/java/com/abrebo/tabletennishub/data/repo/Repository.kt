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
    fun sendFriendRequest(context: Context, currentUserName: String, friendUserName: String)=
        dataSource.sendFriendRequest(context, currentUserName, friendUserName)
    fun getReceivedRequests(currentUserName: String, callback: (List<String>) -> Unit) =
        dataSource.getReceivedRequests(currentUserName, callback)
    fun getSentRequests(currentUserName: String, callback: (List<String>) -> Unit)=
        dataSource.getSentRequests(currentUserName, callback)
    suspend fun getUserNameByEmail(userEmail: String): String? = dataSource.getUserNameByEmail(userEmail)
}