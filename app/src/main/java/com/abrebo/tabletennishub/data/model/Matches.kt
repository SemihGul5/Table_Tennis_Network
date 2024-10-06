package com.abrebo.tabletennishub.data.model

data class Matches(var id:String?="",
                   var home:String?="",
                   var away:String?="",
                   var set1:Int?,
                   var set2:Int?,
                   var set3:Int?,
                   var set4:Int?,
                   var set5:Int?,
                   var scoreHome:Int?,
                   var scoreAway:Int?) {
}