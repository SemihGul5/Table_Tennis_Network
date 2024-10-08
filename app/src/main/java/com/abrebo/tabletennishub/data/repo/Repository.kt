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
    fun withdrawFriendRequest(context: Context, currentUserName: String, receiverUserName: String) =
        dataSource.withdrawFriendRequest(context,currentUserName, receiverUserName)
    fun acceptFriendRequest(context: Context, currentUserName: String, senderUserName: String) =
        dataSource.acceptFriendRequest(context, currentUserName, senderUserName)
    fun declineFriendRequest(context: Context, currentUserName: String, senderUserName: String) =
        dataSource.declineFriendRequest(context, currentUserName, senderUserName)
    fun getfriends(currentUserName: String, callback: (List<String>) -> Unit)=
        dataSource.getfriends(currentUserName, callback)
    fun removeFriend(context: Context, currentUserName: String, friendUserName: String)=
        dataSource.removeFriend(context, currentUserName, friendUserName)
    suspend fun getUserInfo(userEmail: String): Map<String,Any>? =
        dataSource.getUserInfo(userEmail)
}