package com.abrebo.tabletennishub.ui.viewmodel.Statistics

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
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
    var totalScore=MutableLiveData<Int>()
    var totalMatchesAgainstOpponent=MutableLiveData<Int>()
    var totalWinAgainstOpponent=MutableLiveData<Int>()
    var averageScorePerMatchWithOpponen=MutableLiveData<Pair<Double, Double>>()
    var setWinRatesCurrentWithOpponent = MutableLiveData<Map<Int, Double>>()
    var setWinRatesOpponent = MutableLiveData<Map<Int, Double>>()
    var setAvgScoresWithOpponent = MutableLiveData<Map<Int, Double>>()
    var setAvgScoresOpponent = MutableLiveData<Map<Int, Double>>()
    var totalSetWithOpponent=MutableLiveData<Int>()
    var totalScoreCurrrentAndOpponent=MutableLiveData<Pair<Int, Int>>()


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
    fun getTotalPointsWon(currentUserName: String){
        repository.getTotalPointsWon(currentUserName){
            totalScore.value=it
        }
    }
    fun getTotalMatchesAgainstOpponent(currentUserName: String, opponentUserName: String){
        repository.getTotalMatchesAgainstOpponent(currentUserName,opponentUserName){
            totalMatchesAgainstOpponent.value=it
        }
    }
    fun getTotalWinsAgainstOpponent(currentUserName: String, opponentUserName: String){
        repository.getTotalWinsAgainstOpponent(currentUserName,opponentUserName){
            totalWinAgainstOpponent.value=it
        }
    }

    fun getAverageScorePerMatch(currentUserName: String,opponentUserName:String){
        repository.getAverageScorePerMatch(currentUserName,opponentUserName){user,opponent->
            averageScorePerMatchWithOpponen.value = Pair(user,opponent)
        }
    }
    fun getCurrentUserSetWinRates(currentUserName: String, opponentUserName: String){
        repository.getCurrentUserSetWinRates(currentUserName,opponentUserName){
            setWinRatesCurrentWithOpponent.value=it
        }
    }
    fun getOpponentSetWinRates(currentUserName: String, opponentUserName: String){
        repository.getOpponentSetWinRates(currentUserName,opponentUserName){
            setWinRatesOpponent.value=it
        }
    }
    fun getSetAverageScoresAgainstOpponent(currentUserName: String, opponentUserName: String){
        repository.getSetAverageScoresAgainstOpponent(currentUserName,opponentUserName){
            setAvgScoresWithOpponent.value=it
        }
    }
    fun getOpponentSetAverageScores(currentUserName: String, opponentUserName: String){
        repository.getOpponentSetAverageScores(currentUserName,opponentUserName){
            setAvgScoresOpponent.value=it
        }
    }
    fun getTotalSetsPlayedWithOpponent(currentUserName: String, opponentUserName: String){
        repository.getTotalSetsPlayedWithOpponent(currentUserName,opponentUserName){
            totalSetWithOpponent.value=it
        }
    }
    fun getTotalPointsWonAgainstOpponent(currentUserName: String, opponentUserName: String){
        repository.getTotalPointsWonAgainstOpponent(currentUserName,opponentUserName){user,opponent->
            totalScoreCurrrentAndOpponent.value=Pair(user,opponent)
        }
    }
}