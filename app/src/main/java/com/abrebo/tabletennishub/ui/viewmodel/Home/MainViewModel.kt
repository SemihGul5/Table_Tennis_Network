package com.abrebo.tabletennishub.ui.viewmodel.Home

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abrebo.tabletennishub.data.model.Match
import com.abrebo.tabletennishub.data.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(var repository: Repository,application: Application):AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    var matches = MutableLiveData<List<Match>>()
    var friends=MutableLiveData<List<String>>()
    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected


    fun getMatchesByUserName(currentUserName: String){
        repository.getMatchesByUserName(currentUserName) { matchList ->
            matches.postValue(matchList)
        }
    }
    fun getMatchesByUserNameWithFilter(currentUserName: String, opponent: String) {
        repository.getMatchesByUserName(currentUserName) { matchList ->
            val filteredMatches = mutableListOf<Match>()

            matchList.forEach { match ->
                if (match.userHome.equals(opponent, ignoreCase = true) || match.userAway.equals(opponent, ignoreCase = true)) {
                    filteredMatches.add(match)
                }
            }

            matches.postValue(filteredMatches)
        }
    }


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
    fun updateMatchConfirm(isHome:Boolean,b:Boolean,id:String){
        viewModelScope.launch {
            repository.updateMatchConfirm(isHome, b, id)
        }
    }

}
