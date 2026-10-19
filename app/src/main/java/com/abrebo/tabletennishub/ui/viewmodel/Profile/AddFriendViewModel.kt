package com.abrebo.tabletennishub.ui.viewmodel.Profile

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrebo.tabletennishub.data.model.User
import com.abrebo.tabletennishub.data.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddFriendViewModel  @Inject constructor (var repository: Repository): ViewModel() {

    private val _userList = MutableLiveData<List<User>>(emptyList())
    val userList: LiveData<List<User>> = _userList
    private var searchJob: Job? = null
    private var searchGeneration = 0

    fun search(word:String){
        val prefix = word.trim().lowercase()
        searchJob?.cancel()
        searchGeneration += 1
        if (prefix.isEmpty()) {
            _userList.value = emptyList()
            return
        }
        val generation = searchGeneration
        searchJob = viewModelScope.launch {
            delay(300)
            val matches = runCatching {
                withContext(Dispatchers.IO) { repository.searchUsersByUsernamePrefix(prefix) }
            }.getOrDefault(emptyList())
            if (generation == searchGeneration) _userList.value = matches
        }
    }
    fun sendFriendRequest(context: Context, currentUserName: String, friendUserName: String){
        repository.sendFriendRequest(context, currentUserName, friendUserName)
    }
    fun clearUserList(){
        searchJob?.cancel()
        searchGeneration += 1
        _userList.value = emptyList()
    }
    fun getUserNameByEmail(userEmail: String, onResult: (String?) -> Unit){
        viewModelScope.launch {
            onResult(repository.getUserNameByEmail(userEmail))
        }
    }



}
