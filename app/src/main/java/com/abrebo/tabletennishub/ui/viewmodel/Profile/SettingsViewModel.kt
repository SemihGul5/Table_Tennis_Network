package com.abrebo.tabletennishub.ui.viewmodel.Profile

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abrebo.tabletennishub.data.model.Message
import com.abrebo.tabletennishub.data.model.User
import com.abrebo.tabletennishub.data.repo.Repository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
@HiltViewModel
class SettingsViewModel@Inject constructor (var repository: Repository,
                                            application: Application): AndroidViewModel(application){
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    var map= MutableLiveData<HashMap<String,Any>>()
    var userNameAvailability=MutableLiveData<Boolean>()
    var updateUserNameInMatches=MutableLiveData<Boolean>()
    var updateUserNameInDocuments=MutableLiveData<Boolean>()
    var updateUserDocumentId=MutableLiveData<Boolean>()
    fun getUserInfo(email:String){
        viewModelScope.launch {
            map.value=repository.getUserInfo(email) as HashMap<String, Any>
        }
    }
    fun updateUserData(user:User) {
       repository.updateUser(user)
    }
    fun checkUserNameAvailability(userName:String){
        viewModelScope.launch {
            userNameAvailability.value=repository.checkUserNameAvailability(userName)
        }
    }
    fun updateWinnerUserName(oldUserName: String, newUserName: String){
        viewModelScope.launch {
            repository.updateWinnerUserName(oldUserName, newUserName)
        }
    }
    fun updateUserHomeUserName(oldUserName: String, newUserName: String){
        viewModelScope.launch {
            repository.updateUserHomeUserName(oldUserName, newUserName)
        }
    }
    fun updateUserAwayUserName(oldUserName: String, newUserName: String){
        viewModelScope.launch {
            repository.updateUserAwayUserName(oldUserName, newUserName)
        }
    }
    fun updateUserNameInDocuments(oldUserName: String, newUserName: String){
        viewModelScope.launch {
            updateUserNameInDocuments.value=repository.updateUserNameInDocuments(oldUserName, newUserName)
        }
    }
    fun updateUserDocumentId(oldUserName: String, newUserName: String){
        viewModelScope.launch {
            updateUserDocumentId.value=repository.updateUserDocumentId(oldUserName, newUserName)
        }
    }
    fun updateUserNameInFriendRequests(oldUserName: String, newUserName: String){
        viewModelScope.launch {
            repository.updateUserNameInFriendRequests(oldUserName, newUserName)
        }
    }
    fun sendMessage(message: Message){
        repository.sendMessage(message)
    }
    fun deleteUserAccount(auth:FirebaseAuth): Boolean {
        return try {
            val user = auth.currentUser
            user?.delete()
            true
        } catch (e: Exception) {
            false
        }
    }


}