package com.abrebo.tabletennishub.ui.viewmodel.Profile

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrebo.tabletennishub.data.model.User
import com.abrebo.tabletennishub.data.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
    fun sendFriendRequest(context: Context, currentUserName: String, friendUserName: String){
        repository.sendFriendRequest(context, currentUserName, friendUserName)
    }
    fun clearUserList(){
        userList.postValue(emptyList())
    }
    fun getUserNameByEmail(userEmail: String, onResult: (String?) -> Unit){
        viewModelScope.launch {
            onResult(repository.getUserNameByEmail(userEmail))
        }
    }



}