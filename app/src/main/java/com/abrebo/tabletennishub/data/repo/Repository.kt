package com.abrebo.tabletennishub.data.repo

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.abrebo.tabletennishub.data.datasource.DataSource
import com.abrebo.tabletennishub.data.model.Match
import com.abrebo.tabletennishub.data.model.User

class Repository(var dataSource: DataSource) {
    fun uploadUser(): MutableLiveData<List<User>> = dataSource.uploadUser()
    fun search(word:String): MutableLiveData<List<User>> = dataSource.search(word)
    fun saveUser(user:User) = dataSource.saveUser(user)
    fun deleteUser(userId:String) = dataSource.deleteUser(userId)
    fun updateUser(user: User) = dataSource.updateUser(user)
    suspend fun checkUserNameAvailability(userName: String): Boolean = dataSource.checkUserNameAvailability(userName)
    fun sendFriendRequest(context: Context, currentUserName: String, friendUserName: String)=
        dataSource.sendFriendRequest(context, currentUserName, friendUserName)
    fun getReceivedRequests(currentUserName: String, callback: (List<String>) -> Unit) =
        dataSource.getReceivedRequests(currentUserName, callback)
    fun getSentRequests(currentUserName: String, callback: (List<String>) -> Unit)=
        dataSource.getSentRequests(currentUserName, callback)
    suspend fun getUserNameByEmail(userEmail: String): String? = dataSource.getUserNameByEmail(userEmail)
    fun withdrawFriendRequest(context: Context, currentUserName: String, receiverUserName: String) =
        dataSource.withdrawFriendRequest(context,currentUserName, receiverUserName)
    fun acceptFriendRequest(context: Context, currentUserName: String, senderUserName: String) =
        dataSource.acceptFriendRequest(context, currentUserName, senderUserName)
    fun declineFriendRequest(context: Context, currentUserName: String, senderUserName: String) =
        dataSource.declineFriendRequest(context, currentUserName, senderUserName)
    fun getfriends(currentUserName: String, callback: (List<String>) -> Unit)=
        dataSource.getfriends(currentUserName, callback)
    fun removeFriend(context: Context, currentUserName: String, friendUserName: String)=
        dataSource.removeFriend(context, currentUserName, friendUserName)
    suspend fun getUserInfo(userEmail: String): Map<String,Any>? =
        dataSource.getUserInfo(userEmail)

    fun saveMatch(match: Match, onComplete: (Boolean) -> Unit) =
        dataSource.saveMatch(match, onComplete)
    fun getMatchesByUserName(currentUserName: String, onResult: (List<Match>) -> Unit)=
        dataSource.getMatchesByUserName(currentUserName, onResult)
    fun updateMatch(match: Match, onComplete: (Boolean) -> Unit) =
        dataSource.updateMatch(match, onComplete)
    fun getAverageScorePerMatch(currentUserName: String, onResult: (Double) -> Unit) =
        dataSource.getAverageScorePerMatch(currentUserName, onResult)
    fun getSetWinRates(currentUserName: String, onResult: (Map<Int, Double>) -> Unit)=
        dataSource.getSetWinRates(currentUserName,onResult)
    fun getTotalMatchesByUser(currentUserName: String, onResult: (Int) -> Unit) =
        dataSource.getTotalMatchesByUser(currentUserName, onResult)
    fun getMatchResultsByUser(currentUserName: String, onResult: (Int, Int) -> Unit) =
        dataSource.getMatchResultsByUser(currentUserName, onResult)
    fun getSetAverageScores(currentUserName: String, onResult: (Map<Int, Double>) -> Unit)=
        dataSource.getSetAverageScores(currentUserName, onResult)
    fun getMatchAverageScore(currentUserName: String, onResult: (Double) -> Unit)=
        dataSource.getMatchAverageScore(currentUserName, onResult)
    fun getTotalSetsPlayed(currentUserName: String, onResult: (Int) -> Unit)=
        dataSource.getTotalSetsPlayed(currentUserName,onResult)
    fun getTotalPointsWon(currentUserName: String, onResult: (Int) -> Unit)=
        dataSource.getTotalPointsWon(currentUserName, onResult)
    fun deleteMatchByField(id: String, onComplete: (Boolean) -> Unit)=
        dataSource.deleteMatchByField(id, onComplete)
    fun getTotalMatchesAgainstOpponent(currentUserName: String, opponentUserName: String, onResult: (Int) -> Unit) =
        dataSource.getTotalMatchesAgainstOpponent(currentUserName, opponentUserName, onResult)
    fun getTotalWinsAgainstOpponent(currentUserName: String, opponentUserName: String, onResult: (Int) -> Unit)=
        dataSource.getTotalWinsAgainstOpponent(currentUserName, opponentUserName, onResult)
    fun getAverageScorePerMatch(currentUserName: String,opponentUserName:String, onResult: (Double,Double) -> Unit)=
        dataSource.getAverageScorePerMatch(currentUserName, opponentUserName, onResult)
    fun getCurrentUserSetWinRates(currentUserName: String, opponentUserName: String, onResult: (Map<Int, Double>) -> Unit)=
        dataSource.getCurrentUserSetWinRates(currentUserName, opponentUserName, onResult)
    fun getOpponentSetWinRates(currentUserName: String, opponentUserName: String, onResult: (Map<Int, Double>) -> Unit) =
        dataSource.getOpponentSetWinRates(currentUserName, opponentUserName, onResult)
    fun getSetAverageScoresAgainstOpponent(currentUserName: String, opponentUserName: String, onResult: (Map<Int, Double>) -> Unit) =
        dataSource.getSetAverageScoresAgainstOpponent(currentUserName,opponentUserName,onResult)
    fun getOpponentSetAverageScores(currentUserName: String, opponentUserName: String, onResult: (Map<Int, Double>) -> Unit)=
        dataSource.getOpponentSetAverageScores(currentUserName, opponentUserName, onResult)
    fun getTotalSetsPlayedWithOpponent(currentUserName: String, opponentUserName: String, onResult: (Int) -> Unit)=
        dataSource.getTotalSetsPlayedWithOpponent(currentUserName, opponentUserName, onResult)
    fun getTotalPointsWonAgainstOpponent(currentUserName: String, opponentUserName: String, onResult: (Int,Int) -> Unit) =
        dataSource.getTotalPointsWonAgainstOpponent(currentUserName, opponentUserName, onResult)
}