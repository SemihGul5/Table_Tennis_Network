package com.abrebo.tabletennishub.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abrebo.tabletennishub.data.model.User
import com.abrebo.tabletennishub.data.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class RequestsViewModel @Inject constructor (var repository: Repository,
                                             application: Application): AndroidViewModel(application){
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    val receivedRequests = MutableLiveData<List<String>>()
    val sentRequests = MutableLiveData<List<String>>()

    fun fetchReceivedRequests(currentUserName: String) {
        repository.getReceivedRequests(currentUserName) { requests ->
            receivedRequests.postValue(requests)
        }
    }

    fun fetchSentRequests(currentUserName: String) {
        repository.getSentRequests(currentUserName) { requests ->
            sentRequests.postValue(requests)
        }
    }
    fun getUserNameByEmail(userEmail: String, onResult: (String?) -> Unit){
        viewModelScope.launch {
            onResult(repository.getUserNameByEmail(userEmail))
        }
    }
}