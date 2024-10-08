package com.abrebo.tabletennishub.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abrebo.tabletennishub.data.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMatchViewModel @Inject constructor (var repository: Repository): ViewModel() {
    var friends=MutableLiveData<List<String>>()


    fun getUserNameByEmail(userEmail: String, onResult: (String?) -> Unit){
        viewModelScope.launch {
            onResult(repository.getUserNameByEmail(userEmail))
        }
    }

    fun getfriends(currentUserName: String){
        repository.getfriends(currentUserName){
            friends.postValue(it)
        }
    }
}