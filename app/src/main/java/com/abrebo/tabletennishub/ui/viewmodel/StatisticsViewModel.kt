package com.abrebo.tabletennishub.ui.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abrebo.tabletennishub.data.repo.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor (var repository: Repository,
                                              application: Application): AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    var friends= MutableLiveData<List<String>>()
    var averageScorePerMatch=MutableLiveData<Double>()
    var averageSetScoreMatch=MutableLiveData<Double>()
    var setWinRates = MutableLiveData<Map<Int, Double>>()
    var setAvgScores = MutableLiveData<Map<Int, Double>>()
    var totalMatches = MutableLiveData<Int>()
    var matchResults = MutableLiveData<Pair<Int, Int>>()
    var totalSet=MutableLiveData<Int>()


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
    fun getSetWinRates(currentUserName: String){
        repository.getSetWinRates(currentUserName){
            setWinRates.value=it
        }
    }

    fun getAverageScorePerMatch(currentUserName: String){
        repository.getAverageScorePerMatch(currentUserName){
            averageScorePerMatch.value=it
        }
    }
    fun getTotalMatchesByUser(currentUserName: String){
        repository.getTotalMatchesByUser(currentUserName){
            totalMatches.value = it
        }
    }
    fun loadMatchResults(currentUserName: String) {
        repository.getMatchResultsByUser(currentUserName) { wins, losses ->
            matchResults.value = Pair(wins, losses)
        }
    }
    fun getSetAverageScores(currentUserName: String){
        repository.getSetAverageScores(currentUserName){
            setAvgScores.value=it
        }
    }
    fun getMatchAverageScore(currentUserName: String){
        repository.getMatchAverageScore(currentUserName){
            averageSetScoreMatch.value=it
        }
    }
    fun getTotalSetsPlayed(currentUserName: String){
        repository.getTotalSetsPlayed(currentUserName){
            totalSet.value=it
        }
    }
}