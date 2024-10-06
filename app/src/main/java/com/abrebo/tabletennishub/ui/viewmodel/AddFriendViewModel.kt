package com.abrebo.tabletennishub.ui.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.abrebo.tabletennishub.data.model.User
import com.abrebo.tabletennishub.data.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddFriendViewModel  @Inject constructor (var repository: Repository): ViewModel() {

    var userList=MutableLiveData<List<User>>()

    init {
        uploadUser()
    }
    fun uploadUser(){
        userList=repository.uploadUser()
    }

    fun search(word:String){
        userList = repository.search(word)
    }
    fun sendFriendRequest(context: Context, currentUserEmail: String, friendUserEmail: String){
        repository.sendFriendRequest(context, currentUserEmail, friendUserEmail)
    }
    fun clearUserList(){
        userList.postValue(emptyList())
    }


}