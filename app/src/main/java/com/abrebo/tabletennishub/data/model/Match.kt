package com.abrebo.tabletennishub.data.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Match(
    var id:String="",
    val userHome: String = "",
    val userAway: String = "",
    val setScores: List<SetScore> = listOf(),
    val userHomeScore: Int = 0,
    val userAwayScore: Int = 0,
    val timestamp: Timestamp = Timestamp.now(),
    val date:String="",
    val winner:String=""
):Serializable