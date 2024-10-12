package com.abrebo.tabletennishub.data.datasource

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.abrebo.tabletennishub.data.model.Match
import com.abrebo.tabletennishub.data.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.log

class DataSource(var collectionReference: CollectionReference,
                 var collectionReferenceUserFriends: CollectionReference,
                 var collectionReferenceMatches: CollectionReference) {
    var userList = MutableLiveData<List<User>>()
    val firestore = FirebaseFirestore.getInstance()

    fun uploadUser(): MutableLiveData<List<User>> {
        collectionReference.addSnapshotListener { value, error ->
            if (value != null) {
                val list = ArrayList<User>()

                for (d in value.documents) {
                    val user = d.toObject(User::class.java)
                    if (user != null) {
                        user.id = d.id
                        list.add(user)
                    }
                }
                userList.value = list
            }
        }
        return userList
    }

    fun search(word: String): MutableLiveData<List<User>> {
        collectionReference.addSnapshotListener { value, error ->
            if (value != null) {
                val list = ArrayList<User>()

                for (d in value.documents) {
                    val user = d.toObject(User::class.java)
                    if (user != null) {
                        if (user.userName!!.lowercase().contains(word.lowercase())) {
                            user.id = d.id
                            list.add(user)
                        }
                    }
                }
                userList.value = list
            }
        }
        return userList
    }

    fun saveUser(user: User) {
        collectionReference.document().set(user)
    }

    fun deleteUser(userId: String) {
        collectionReference.document(userId).delete()
    }

    fun updateUser(user: User) {
        val newUser = HashMap<String, Any>()
        newUser["AdSoyad"] = user.nameFamily!!
        newUser["KullanıcıAdı"] = user.userName!!
        newUser["Email"] = user.email!!
        collectionReference.document(user.id!!).update(newUser)
    }

    suspend fun checkUserNameAvailability(userName: String): Boolean {
        return try {
            val querySnapshot = collectionReference
                .whereEqualTo("KullanıcıAdı", userName)
                .get()
                .await()

            querySnapshot.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserNameByEmail(userEmail: String): String? {
        return try {
            val querySnapshot = firestore.collection("Kullanıcılar")
                .whereEqualTo("email", userEmail)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                document.getString("userName")
            } else {
                Log.e("User", "Kullanıcı belgesi bulunamadı")
                null
            }
        } catch (e: Exception) {
            Log.e("User", "Error getting displayName: ${e.message}")
            null
        }
    }
    suspend fun getUserInfo(userEmail: String): Map<String, Any>? {
        return try {
            val querySnapshot = firestore.collection("Kullanıcılar")
                .whereEqualTo("email", userEmail)
                .get()
                .await()

            if (!querySnapshot.isEmpty) {
                val document = querySnapshot.documents.first()
                val map = HashMap<String, Any>()

                val email = document.getString("email")
                val nameFamily = document.getString("nameFamily")
                val userName = document.getString("userName")

                if (email != null && nameFamily != null && userName != null) {
                    map["id"] = document.id
                    map["email"] = email
                    map["nameFamily"] = nameFamily
                    map["userName"] = userName
                    map
                } else {
                    Log.e("User", "Kullanıcı bilgileri eksik.")
                    null
                }
            } else {
                Log.e("User", "Kullanıcı belgesi bulunamadı")
                null
            }
        } catch (e: Exception) {
            Log.e("User", "Kullanıcı bilgileri alınırken hata oluştu: ${e.message}")
            null
        }
    }


    //userFriend
    fun sendFriendRequest(context: Context, currentUserName: String, friendUserName: String) {
        val currentUserDocRef = collectionReferenceUserFriends.document(currentUserName)
        val friendUserDocRef = collectionReferenceUserFriends.document(friendUserName)

        firestore.runBatch { batch ->
            batch.set(
                currentUserDocRef,
                mapOf("istekler.gönderilen" to FieldValue.arrayUnion(friendUserName)),
                SetOptions.merge()
            )
            batch.set(
                friendUserDocRef,
                mapOf("istekler.alınan" to FieldValue.arrayUnion(currentUserName)),
                SetOptions.merge()
            )
        }.addOnSuccessListener {
            Toast.makeText(context, "Arkadaşlık isteği gönderildi.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(context, "İstek gönderilirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun getReceivedRequests(currentUserName: String, callback: (List<String>) -> Unit) {
        getRequests(currentUserName, "istekler.alınan", callback)
    }

    fun getSentRequests(currentUserName: String, callback: (List<String>) -> Unit) {
        getRequests(currentUserName, "istekler.gönderilen", callback)
    }
    fun getfriends(currentUserName: String, callback: (List<String>) -> Unit) {
        getRequests(currentUserName, "arkadaşlar", callback)
    }

    private fun getRequests(currentUserName: String, field: String, callback: (List<String>) -> Unit) {
        val documentRef: DocumentReference = collectionReferenceUserFriends.document(currentUserName)
        documentRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentSnapshot = task.result
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Belge verisini Map olarak al
                        val data = documentSnapshot.data
                        // Belirtilen alana erişim
                        val requests = (data?.get(field) as? List<String>) ?: emptyList()
                        callback(requests)
                    } else {
                        Log.e("Firestore Error", "Belge mevcut değil")
                        callback(emptyList())
                    }
                } else {
                    Log.e("Request Error", task.exception?.message.toString())
                    callback(emptyList())
                }
            }
    }
    fun withdrawFriendRequest(context: Context, currentUserName: String, receiverUserName: String) {
        val currentUserDocRef = collectionReferenceUserFriends.document(currentUserName)
        val receiverUserDocRef = collectionReferenceUserFriends.document(receiverUserName)

        firestore.runBatch { batch ->
            batch.set(
                currentUserDocRef,
                mapOf("istekler.gönderilen" to FieldValue.arrayRemove(receiverUserName)),
                SetOptions.merge()
            )
            batch.set(
                receiverUserDocRef,
                mapOf("istekler.alınan" to FieldValue.arrayRemove(currentUserName)),
                SetOptions.merge()
            )
        }.addOnSuccessListener {
            Toast.makeText(context, "Arkadaşlık isteği geri çekildi.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(context, "İstek geri çekilirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    fun removeFriend(context: Context, currentUserName: String, friendUserName: String) {
        val currentUserDocRef = collectionReferenceUserFriends.document(currentUserName)
        val friendUserDocRef = collectionReferenceUserFriends.document(friendUserName)

        firestore.runBatch { batch ->
            batch.set(
                currentUserDocRef,
                mapOf("arkadaşlar" to FieldValue.arrayRemove(friendUserName)),
                SetOptions.merge()
            )
            batch.set(
                friendUserDocRef,
                mapOf("arkadaşlar" to FieldValue.arrayRemove(currentUserName)),
                SetOptions.merge()
            )
        }.addOnSuccessListener {
            Toast.makeText(context, "Arkadaş başarıyla silindi.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(context, "Arkadaş silinirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun acceptFriendRequest(context: Context, currentUserName: String, senderUserName: String) {
        val currentUserDocRef = collectionReferenceUserFriends.document(currentUserName)
        val senderUserDocRef = collectionReferenceUserFriends.document(senderUserName)

        firestore.runBatch { batch ->
            batch.set(
                currentUserDocRef,
                mapOf(
                    "istekler.alınan" to FieldValue.arrayRemove(senderUserName),
                    "arkadaşlar" to FieldValue.arrayUnion(senderUserName)
                ),
                SetOptions.merge()
            )

            batch.set(
                senderUserDocRef,
                mapOf(
                    "istekler.gönderilen" to FieldValue.arrayRemove(currentUserName),
                    "arkadaşlar" to FieldValue.arrayUnion(currentUserName)
                ),
                SetOptions.merge()
            )
        }.addOnSuccessListener {
            Toast.makeText(context, "Arkadaşlık isteği kabul edildi.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(context, "İstek kabul edilirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    fun declineFriendRequest(context: Context, currentUserName: String, senderUserName: String) {
        val currentUserDocRef = collectionReferenceUserFriends.document(currentUserName)
        val senderUserDocRef = collectionReferenceUserFriends.document(senderUserName)

        firestore.runBatch { batch ->
            batch.set(
                currentUserDocRef,
                mapOf("istekler.alınan" to FieldValue.arrayRemove(senderUserName)),
                SetOptions.merge()
            )

            batch.set(
                senderUserDocRef,
                mapOf("istekler.gönderilen" to FieldValue.arrayRemove(currentUserName)),
                SetOptions.merge()
            )
        }.addOnSuccessListener {
            Toast.makeText(context, "Arkadaşlık isteği reddedildi.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Toast.makeText(context, "İstek reddedilirken hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    //maçlar

    fun saveMatch(match: Match, onComplete: (Boolean) -> Unit) {
        collectionReferenceMatches
            .add(match.copy(date = getCurrentDateFormatted(), id = UUID.randomUUID().toString()))
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("Repository", "Maç kaydedilemedi: ${e.message}")
                onComplete(false)
            }
    }
    fun deleteMatchByField(id: String, onComplete: (Boolean) -> Unit) {
        collectionReferenceMatches
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    for (document in querySnapshot.documents) {
                        document.reference.delete()
                            .addOnSuccessListener {
                            }
                    }
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Repository", "Sorgu başarısız: ${e.message}")
                onComplete(false)
            }
    }

    fun getMatchesByUserName(currentUserName: String, onResult: (List<Match>) -> Unit) {
        val matchesList = mutableListOf<Match>()

        val homeMatchesQuery = collectionReferenceMatches
            .whereEqualTo("userHome", currentUserName)
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val awayMatchesQuery = collectionReferenceMatches
            .whereEqualTo("userAway", currentUserName)
            .orderBy("timestamp", Query.Direction.ASCENDING)

        homeMatchesQuery.get()
            .addOnSuccessListener { homeQuerySnapshot ->
                matchesList.addAll(homeQuerySnapshot.documents.mapNotNull { document ->
                    document.toObject(Match::class.java)
                })

                awayMatchesQuery.get()
                    .addOnSuccessListener { awayQuerySnapshot ->
                        matchesList.addAll(awayQuerySnapshot.documents.mapNotNull { document ->
                            document.toObject(Match::class.java)
                        })

                        val sortedMatches = matchesList.sortedBy { it.timestamp }
                        onResult(sortedMatches)
                    }
                    .addOnFailureListener { e ->
                        Log.e("Repository", "Away maçlar alınamadı: ${e.message}")
                        onResult(matchesList.sortedBy { it.timestamp })
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Repository", "Home maçlar alınamadı: ${e.message}")
                onResult(emptyList())
            }
    }

    fun getCurrentDateFormatted(): String {
        val dateFormat = SimpleDateFormat("M/d/yy", Locale.getDefault())
        return dateFormat.format(Date())
    }
    fun updateMatch(match: Match, onComplete: (Boolean) -> Unit) {
        collectionReferenceMatches
            .whereEqualTo("id", match.id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    Log.e("Repository", "Maç bulunamadı")
                    onComplete(false)
                    return@addOnSuccessListener
                }

                for (document in querySnapshot.documents) {
                    document.reference
                        .set(match)
                        .addOnSuccessListener {
                            onComplete(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e("Repository", "Maç güncellenemedi: ${e.message}")
                            onComplete(false)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Repository", "Maç bulunamadı: ${e.message}")
                onComplete(false)
            }
    }

    fun getAverageScorePerMatch(currentUserName: String, onResult: (Double) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            if (matches.isEmpty()) {
                onResult(0.0)
                return@getMatchesByUserName
            }

            var totalScore = 0
            var totalMatches = 0

            matches.forEach { match ->
                if (match.userHome == currentUserName) {
                    totalScore += match.userHomeScore
                } else if (match.userAway == currentUserName) {
                    totalScore += match.userAwayScore
                }
                totalMatches++
            }
            val averageScore = if (totalMatches > 0) {
                totalScore.toDouble() / totalMatches
            } else {
                0.0
            }

            onResult(averageScore)
        }
    }
    fun getSetWinRates(currentUserName: String, onResult: (Map<Int, Double>) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            val winRates = mutableMapOf<Int, Double>()

            val totalSetsPlayed = IntArray(5) { 0 }
            val setsWon = IntArray(5) { 0 }

            for (match in matches) {
                for (setIndex in 0 until match.setScores.size) {
                    if (setIndex < 5) {
                        totalSetsPlayed[setIndex]++

                        val setScore = match.setScores[setIndex]

                        if (match.userHome == currentUserName && setScore.userScore > setScore.opponentScore) {
                            setsWon[setIndex]++
                        }

                        if (match.userAway == currentUserName &&setScore.opponentScore > setScore.userScore) {
                            setsWon[setIndex]++
                        }
                    }
                }
            }
            for (setNumber in 0 until 5) {
                val winRate = if (totalSetsPlayed[setNumber] > 0) {
                    setsWon[setNumber].toDouble() / totalSetsPlayed[setNumber]
                } else {
                    0.0
                }
                winRates[setNumber] = winRate
            }

            onResult(winRates)
        }
    }
    fun getSetAverageScores(currentUserName: String, onResult: (Map<Int, Double>) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            val averageScores = mutableMapOf<Int, Double>()

            val totalSetsPlayed = IntArray(5) { 0 }
            val totalScores = DoubleArray(5) { 0.0 }

            for (match in matches) {
                for (setIndex in 0 until match.setScores.size) {
                    if (setIndex < 5) {
                        totalSetsPlayed[setIndex]++

                        val setScore = match.setScores[setIndex]
                        if (match.userHome == currentUserName) {
                            totalScores[setIndex] += setScore.userScore.toDouble()
                        } else if (match.userAway == currentUserName) {
                            totalScores[setIndex] += setScore.opponentScore.toDouble()
                        }
                    }
                }
            }

            for (setNumber in 0 until 5) {
                val averageScore = if (totalSetsPlayed[setNumber] > 0) {
                    totalScores[setNumber] / totalSetsPlayed[setNumber]
                } else {
                    0.0
                }
                println("Average score for set ${setNumber + 1} is $averageScore")
                averageScores[setNumber] = averageScore
            }

            onResult(averageScores)
        }
    }

    fun getTotalMatchesByUser(currentUserName: String, onResult: (Int) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            onResult(matches.size)
        }
    }
    fun getMatchResultsByUser(currentUserName: String, onResult: (Int, Int) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            var wins = 0
            var losses = 0

            for (match in matches) {
                if (match.winner == currentUserName) {
                    wins++
                } else if (match.winner!="Berabere"&&match.winner!="Maç daha bitmedi") {
                    losses++
                }
            }
            onResult(wins, losses)
        }
    }
    fun getMatchAverageScore(currentUserName: String, onResult: (Double) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            var totalMatchesPlayed = 0
            var totalScores = 0.0

            for (match in matches) {
                totalMatchesPlayed++

                var matchScore = 0.0
                for (setScore in match.setScores) {
                    if (match.userHome == currentUserName) {
                        matchScore += setScore.userScore.toDouble()
                    } else if (match.userAway == currentUserName) {
                        matchScore += setScore.opponentScore.toDouble()
                    }
                }

                totalScores += matchScore
            }

            val averageScore = if (totalMatchesPlayed > 0) {
                totalScores / totalMatchesPlayed
            } else {
                0.0
            }

            onResult(averageScore)
        }
    }
    fun getTotalSetsPlayed(currentUserName: String, onResult: (Int) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            var totalSetsPlayed = 0
            for (match in matches) {
                for (setScore in match.setScores) {
                    if (match.userHome == currentUserName || match.userAway == currentUserName) {
                        totalSetsPlayed++
                    }
                }
            }
            onResult(totalSetsPlayed)
        }
    }
    fun getTotalSetsPlayedWithOpponent(currentUserName: String, opponentUserName: String, onResult: (Int) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            var totalSetsPlayed = 0

            for (match in matches) {
                if ((match.userHome == currentUserName && match.userAway == opponentUserName) ||
                    (match.userHome == opponentUserName && match.userAway == currentUserName)) {
                    totalSetsPlayed += match.setScores.size
                }
            }
            onResult(totalSetsPlayed)
        }
    }

    fun getTotalPointsWon(currentUserName: String, onResult: (Int) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            var totalPointsWon = 0

            for (match in matches) {
                for (setScore in match.setScores) {
                    if (match.userHome == currentUserName) {
                        totalPointsWon += setScore.userScore
                    } else if (match.userAway == currentUserName) {
                        totalPointsWon += setScore.opponentScore
                    }
                }
            }

            onResult(totalPointsWon)
        }
    }
    fun getTotalPointsWonAgainstOpponent(currentUserName: String, opponentUserName: String, onResult: (Int,Int) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            var totalPointsWon = 0
            var totalPointWonOpponent=0
            for (match in matches) {
                if ((match.userHome == currentUserName && match.userAway == opponentUserName) ||
                    (match.userHome == opponentUserName && match.userAway == currentUserName)) {

                    for (setScore in match.setScores) {
                        if (match.userHome == currentUserName) {
                            totalPointsWon += setScore.userScore
                            totalPointWonOpponent+=setScore.opponentScore
                        } else if (match.userAway == currentUserName) {
                            totalPointsWon += setScore.opponentScore
                            totalPointWonOpponent += setScore.userScore
                        }
                    }
                }
            }

            onResult(totalPointsWon,totalPointWonOpponent)
        }
    }

    fun getTotalMatchesAgainstOpponent(currentUserName: String, opponentUserName: String, onResult: (Int) -> Unit) {
        val matchesList = mutableListOf<Match>()

        val homeMatchesQuery = collectionReferenceMatches
            .whereEqualTo("userHome", currentUserName)
            .whereEqualTo("userAway", opponentUserName)
            .orderBy("timestamp", Query.Direction.ASCENDING)

        val awayMatchesQuery = collectionReferenceMatches
            .whereEqualTo("userHome", opponentUserName)
            .whereEqualTo("userAway", currentUserName)
            .orderBy("timestamp", Query.Direction.ASCENDING)

        homeMatchesQuery.get()
            .addOnSuccessListener { homeQuerySnapshot ->
                matchesList.addAll(homeQuerySnapshot.documents.mapNotNull { document ->
                    document.toObject(Match::class.java)
                })

                awayMatchesQuery.get()
                    .addOnSuccessListener { awayQuerySnapshot ->
                        matchesList.addAll(awayQuerySnapshot.documents.mapNotNull { document ->
                            document.toObject(Match::class.java)
                        })
                        onResult(matchesList.size)
                    }
                    .addOnFailureListener { e ->
                        onResult(matchesList.size)
                    }
            }
            .addOnFailureListener { e ->
                onResult(0)
            }
    }
    fun getTotalWinsAgainstOpponent(currentUserName: String, opponentUserName: String, onResult: (Int) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            var wins = 0
            for (match in matches) {
                if (match.winner == currentUserName && (match.userHome == opponentUserName || match.userAway == opponentUserName)) {
                    wins++
                }
            }
            onResult(wins)
        }
    }
    fun getAverageScorePerMatch(currentUserName: String,opponentUserName:String, onResult: (Double,Double) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            if (matches.isEmpty()) {
                onResult(0.0,0.0)
                return@getMatchesByUserName
            }

            var totalScore = 0
            var totalMatches = 0
            var totalScoreOpponent = 0

            matches.forEach { match ->
                if (match.userHome == currentUserName&&match.userAway==opponentUserName) {
                    totalScore += match.userHomeScore
                    totalScoreOpponent+=match.userAwayScore
                    totalMatches++
                } else if (match.userAway == currentUserName&&match.userHome==opponentUserName) {
                    totalScore += match.userAwayScore
                    totalScoreOpponent+=match.userHomeScore
                    totalMatches++
                }
                println(totalMatches)
            }
            val averageScore = if (totalMatches > 0) {
                totalScore.toDouble() / totalMatches
            } else {
                0.0
            }
            val averageScoreOpponent = if (totalMatches > 0) {
                totalScoreOpponent.toDouble() / totalMatches
            } else {
                0.0
            }

            onResult(averageScore,averageScoreOpponent)
        }
    }

    fun getCurrentUserSetWinRates(currentUserName: String, opponentUserName: String, onResult: (Map<Int, Double>) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            val winRates = mutableMapOf<Int, Double>()

            val totalSetsPlayed = IntArray(5) { 0 }
            val setsWon = IntArray(5) { 0 }

            for (match in matches) {
                if (match.userAway == opponentUserName || match.userHome == opponentUserName) {
                    for (setIndex in 0 until match.setScores.size) {
                        if (setIndex < 5) {
                            totalSetsPlayed[setIndex]++
                            val setScore = match.setScores[setIndex]

                            if (match.userHome == currentUserName && setScore.userScore > setScore.opponentScore) {
                                setsWon[setIndex]++
                            }

                            if (match.userAway == currentUserName && setScore.opponentScore > setScore.userScore) {
                                setsWon[setIndex]++
                            }
                        }
                    }
                }
            }
            for (setNumber in 0 until 5) {
                val winRate = if (totalSetsPlayed[setNumber] > 0) {
                    setsWon[setNumber].toDouble() / totalSetsPlayed[setNumber]
                } else {
                    0.0
                }

                winRates[setNumber] = winRate
            }
            onResult(winRates)
        }
    }
    fun getOpponentSetWinRates(currentUserName: String, opponentUserName: String, onResult: (Map<Int, Double>) -> Unit) {
        getMatchesByUserName(opponentUserName) { matches ->
            val winRates = mutableMapOf<Int, Double>()

            val totalSetsPlayed = IntArray(5) { 0 }
            val setsWon = IntArray(5) { 0 }

            for (match in matches) {
                if (match.userAway == currentUserName || match.userHome == currentUserName) {
                    for (setIndex in 0 until match.setScores.size) {
                        if (setIndex < 5) {
                            totalSetsPlayed[setIndex]++
                            val setScore = match.setScores[setIndex]

                            if (match.userHome == opponentUserName && setScore.userScore > setScore.opponentScore) {
                                setsWon[setIndex]++
                            }

                            if (match.userAway == opponentUserName && setScore.opponentScore > setScore.userScore) {
                                setsWon[setIndex]++
                            }
                        }
                    }
                }
            }

            for (setNumber in 0 until 5) {
                val winRate = if (totalSetsPlayed[setNumber] > 0) {
                    setsWon[setNumber].toDouble() / totalSetsPlayed[setNumber]
                } else {
                    0.0
                }

                winRates[setNumber] = winRate
            }

            onResult(winRates)
        }
    }
    fun getSetAverageScoresAgainstOpponent(currentUserName: String, opponentUserName: String, onResult: (Map<Int, Double>) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            val averageScores = mutableMapOf<Int, Double>()

            val totalSetsPlayed = IntArray(5) { 0 }
            val totalScores = DoubleArray(5) { 0.0 }

            for (match in matches) {
                if (match.userAway == opponentUserName || match.userHome == opponentUserName) {
                    for (setIndex in 0 until match.setScores.size) {
                        if (setIndex < 5) {
                            totalSetsPlayed[setIndex]++

                            val setScore = match.setScores[setIndex]
                            if (match.userHome == currentUserName) {
                                totalScores[setIndex] += setScore.userScore.toDouble()
                            } else if (match.userAway == currentUserName) {
                                totalScores[setIndex] += setScore.opponentScore.toDouble()
                            }
                        }
                    }
                }
            }

            for (setNumber in 0 until 5) {
                val averageScore = if (totalSetsPlayed[setNumber] > 0) {
                    totalScores[setNumber] / totalSetsPlayed[setNumber]
                } else {
                    0.0
                }
                println("Average score for set ${setNumber + 1} against $opponentUserName is $averageScore")
                averageScores[setNumber] = averageScore
            }

            onResult(averageScores)
        }
    }
    fun getOpponentSetAverageScores(currentUserName: String, opponentUserName: String, onResult: (Map<Int, Double>) -> Unit) {
        getMatchesByUserName(currentUserName) { matches ->
            val averageScores = mutableMapOf<Int, Double>()

            val totalSetsPlayed = IntArray(5) { 0 }
            val totalScores = DoubleArray(5) { 0.0 }

            for (match in matches) {
                if (match.userAway == opponentUserName || match.userHome == opponentUserName) {
                    for (setIndex in 0 until match.setScores.size) {
                        if (setIndex < 5) {
                            totalSetsPlayed[setIndex]++

                            val setScore = match.setScores[setIndex]
                            if (match.userHome == opponentUserName) {
                                totalScores[setIndex] += setScore.userScore.toDouble()
                            } else if (match.userAway == opponentUserName) {
                                totalScores[setIndex] += setScore.opponentScore.toDouble()
                            }
                        }
                    }
                }
            }

            for (setNumber in 0 until 5) {
                val averageScore = if (totalSetsPlayed[setNumber] > 0) {
                    totalScores[setNumber] / totalSetsPlayed[setNumber]
                } else {
                    0.0
                }
                println("Average score for set ${setNumber + 1} by $opponentUserName against $currentUserName is $averageScore")
                averageScores[setNumber] = averageScore
            }

            onResult(averageScores)
        }
    }



}