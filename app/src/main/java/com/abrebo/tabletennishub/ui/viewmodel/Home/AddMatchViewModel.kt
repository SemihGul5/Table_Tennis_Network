package com.abrebo.tabletennishub.ui.viewmodel.Home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.data.model.Match
import com.abrebo.tabletennishub.data.model.SetScore
import com.abrebo.tabletennishub.data.repo.Repository
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMatchViewModel @Inject constructor (var repository: Repository,application: Application): AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    var friends=MutableLiveData<List<String>>()
    var setCount = MutableLiveData(1)
    private var interstitialAd: InterstitialAd? = null
    fun saveMatch(currentUserName: String, opponentUserName: String, setScores: List<Pair<Int, Int>>) {
        val user1Score = setScores.count { it.first > it.second }
        val user2Score = setScores.count { it.second > it.first }
        val winner = when {
            user1Score == 3 || user2Score == 3 -> {
                when {
                    user1Score > user2Score -> currentUserName
                    user1Score < user2Score -> opponentUserName
                    else -> "Berabere"
                }
            }
            else -> "Maç daha bitmedi"
        }


        val match = Match(
            userHome = currentUserName,
            userAway = opponentUserName,
            setScores = setScores.map { SetScore(it.first, it.second) },
            userHomeScore = user1Score,
            userAwayScore = user2Score,
            timestamp = Timestamp.now(),
            winner = winner,
            confirmStatusHome = false,
            confirmStatusAway = false,
            confirmDeleteStatusHome = false,
            confirmDeleteStatusAway = false
        )

        repository.saveMatch(match) { success ->
            if (success) {
                Toast.makeText(context,
                    context.getString(R.string.Matchhasbeensuccessfullysaved)
                    ,Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context,
                    context.getString(R.string.Thematchcouldnotbesaved)
                    ,Toast.LENGTH_SHORT).show()
            }
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
    fun updateMatch(existingMatch: Match, currentUserName:String, opponentUserName: String, setScores: List<SetScore>) {
        val user1Score = setScores.count { it.userScore > it.opponentScore }
        val user2Score = setScores.count { it.opponentScore > it.userScore }
        val winner = when {
            user1Score == 3 || user2Score == 3 -> {
                when {
                    user1Score > user2Score -> currentUserName
                    user1Score < user2Score -> opponentUserName
                    else -> "Berabere"
                }
            }
            else -> "Maç daha bitmedi"
        }
        val updatedMatch = existingMatch.copy(
            userAway = opponentUserName,
            setScores = setScores,
            userHomeScore = user1Score,
            userAwayScore = user2Score,
            winner = winner,
            timestamp = Timestamp.now()

        )

        repository.updateMatch(updatedMatch) { success ->
            if (success) {
                Toast.makeText(context,
                    context.getString(R.string.Matchhasbeensuccessfullyupdated),
                    Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context,
                    context.getString(R.string.Thematchcouldnotbeupdated),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun deleteMatchByField(id: String){
        repository.deleteMatchByField(id){
            if (it){
                Toast.makeText(context,
                    context.getString(R.string.Matchhasbeensuccessfullydeleted),
                    Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,
                    context.getString(R.string.Anerroroccurredwhiledeletingthematch),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, "ca-app-pub-4667560937795938/7337852044", adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }
            })
    }
    fun showInterstitialAd(activity: Activity) {
        if (interstitialAd!=null){
            interstitialAd?.show(activity)
        }
    }
    fun updateMatchConfirmDelete(isHome:Boolean,b:Boolean,id:String){
        viewModelScope.launch {
            repository.updateMatchConfirmDelete(isHome, b, id)
        }
    }

}