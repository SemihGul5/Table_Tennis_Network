package com.abrebo.tabletennishub.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentCompareStatisticsBinding
import com.abrebo.tabletennishub.ui.viewmodel.StatisticsViewModel
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CompareStatisticsFragment : Fragment() {
    private lateinit var binding: FragmentCompareStatisticsBinding
    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private var userName=""
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        viewModel.getUserNameByEmail(auth.currentUser?.email!!) {
            if (it != null) {
                userName=it
                viewModel.getfriends(it)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentCompareStatisticsBinding.inflate(inflater, container, false)
        binding.cardMatches.visibility=View.INVISIBLE
        binding.detailedStatisticsTitle.visibility=View.INVISIBLE
        binding.linearLayoutAll.visibility=View.INVISIBLE
        MobileAds.initialize(requireContext()) {}

        // Setup Banner Ad
        adView = AdView(requireContext())
        adView.adUnitId = "ca-app-pub-3940256099942544/9214589741"
        adView.setAdSize(AdSize.BANNER)
        binding.adView.removeAllViews()
        binding.adView.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        return binding.root
    }

    @SuppressLint("DefaultLocale")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.friends.observe(viewLifecycleOwner) { friendList ->
            if (friendList != null && friendList.isNotEmpty()) {
                val friendsAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, friendList)
                binding.autoCompleteTextView.setAdapter(friendsAdapter)
                binding.autoCompleteTextView.threshold = 1

                // Rakip seçimi yapıldığında istatistikleri güncellenmesi
                binding.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
                    val selectedFriend = friendList[position]
                    viewModel.getTotalMatchesAgainstOpponent(userName, selectedFriend)
                    viewModel.getTotalWinsAgainstOpponent(userName,selectedFriend)
                    viewModel.getAverageScorePerMatch(userName,selectedFriend)
                    binding.averageScorePerMatchCurrentName.text=userName
                    binding.averageScorePerMatchOpponentName.text=selectedFriend
                    binding.currentUserName.text=userName
                    binding.opponentUserName.text=selectedFriend
                    viewModel.getCurrentUserSetWinRates(userName,selectedFriend)
                    viewModel.getOpponentSetWinRates(userName,selectedFriend)
                    binding.currentUserNameWinScoreSet.text=userName
                    binding.opponentUserNameWinScoreSet.text=selectedFriend
                    viewModel.getSetAverageScoresAgainstOpponent(userName,selectedFriend)
                    viewModel.getOpponentSetAverageScores(userName,selectedFriend)
                    viewModel.getTotalSetsPlayedWithOpponent(userName,selectedFriend)
                    binding.currentUserNameTotalScore.text=userName
                    binding.opponentUserNameTotalScore.text=selectedFriend
                    viewModel.getTotalPointsWonAgainstOpponent(userName,selectedFriend)
                    binding.currentUserNamePlaySet.text=userName
                    binding.opponentUserNamePlaySet.text=selectedFriend
                }
            }
        }

        // viewmodel livedata gözlemleyici
        viewModel.averageScorePerMatchWithOpponen.observe(viewLifecycleOwner){(user,opponent)->
            binding.averageScorePerMatchUser.text=String.format("%.2f",user)
            binding.averageScorePerMatchOpponent.text=String.format("%.2f",opponent)

        }
        viewModel.setWinRatesCurrentWithOpponent.observe(viewLifecycleOwner) {
            for (setNumber in 0..4) {
                val winRate = it[setNumber] ?: 0.0
                when (setNumber) {
                    0 -> binding.currentSet1Value.text = String.format("%.2f%%", winRate * 100)
                    1 -> binding.currentSet2Value.text = String.format("%.2f%%", winRate * 100)
                    2 -> binding.currentSet3Value.text = String.format("%.2f%%", winRate * 100)
                    3 -> binding.currentSet4Value.text = String.format("%.2f%%", winRate * 100)
                    4 -> binding.currentSet5Value.text = String.format("%.2f%%", winRate * 100)
                }
            }
        }

        viewModel.setWinRatesOpponent.observe(viewLifecycleOwner){
            for (setNumber in 0..4) {
                val winRate = it[setNumber] ?: 0.0
                when (setNumber) {
                    0 -> binding.opponentSet1Value.text = String.format("%.2f%%", winRate * 100)
                    1 -> binding.opponentSet2Value.text = String.format("%.2f%%", winRate * 100)
                    2 -> binding.opponentSet3Value.text = String.format("%.2f%%", winRate * 100)
                    3 -> binding.opponentSet4Value.text = String.format("%.2f%%", winRate * 100)
                    4 -> binding.opponentSet5Value.text = String.format("%.2f%%", winRate * 100)
                }
            }
        }

        viewModel.setAvgScoresWithOpponent.observe(viewLifecycleOwner){avgScoresMapCurrent->
            val set1AvgScore = avgScoresMapCurrent[0]?.toFloat() ?: 0.0f
            val set2AvgScore = avgScoresMapCurrent[1]?.toFloat() ?: 0.0f
            val set3AvgScore = avgScoresMapCurrent[2]?.toFloat() ?: 0.0f
            val set4AvgScore = avgScoresMapCurrent[3]?.toFloat() ?: 0.0f
            val set5AvgScore = avgScoresMapCurrent[4]?.toFloat() ?: 0.0f

            binding.currentSet1ValueWinScoreSet.text = String.format("%.2f", set1AvgScore)
            binding.currentSet2ValueWinScoreSet.text = String.format("%.2f", set2AvgScore)
            binding.currentSet3ValueWinScoreSet.text = String.format("%.2f", set3AvgScore)
            binding.currentSet4ValueWinScoreSet.text = String.format("%.2f", set4AvgScore)
            binding.currentSet5ValueWinScoreSet.text = String.format("%.2f", set5AvgScore)

            viewModel.setAvgScoresOpponent.observe(viewLifecycleOwner){avgScoresMapOpponent->
                val set1AvgScoreOpponent = avgScoresMapOpponent[0]?.toFloat() ?: 0.0f
                val set2AvgScoreOpponent = avgScoresMapOpponent[1]?.toFloat() ?: 0.0f
                val set3AvgScoreOpponent = avgScoresMapOpponent[2]?.toFloat() ?: 0.0f
                val set4AvgScoreOpponent = avgScoresMapOpponent[3]?.toFloat() ?: 0.0f
                val set5AvgScoreOpponent = avgScoresMapOpponent[4]?.toFloat() ?: 0.0f

                binding.opponentSet1ValueWinScoreSet.text = String.format("%.2f", set1AvgScoreOpponent)
                binding.opponentSet2ValueWinScoreSet.text = String.format("%.2f", set2AvgScoreOpponent)
                binding.opponentSet3ValueWinScoreSet.text = String.format("%.2f", set3AvgScoreOpponent)
                binding.opponentSet4ValueWinScoreSet.text = String.format("%.2f", set4AvgScoreOpponent)
                binding.opponentSet5ValueWinScoreSet.text = String.format("%.2f", set5AvgScoreOpponent)
                viewModel.totalMatchesAgainstOpponent.observe(viewLifecycleOwner) { totalMatches ->
                    if(totalMatches>0){
                        binding.cardMatches.visibility=View.VISIBLE
                        binding.detailedStatisticsTitle.visibility=View.VISIBLE
                        binding.linearLayoutAll.visibility=View.VISIBLE

                        binding.totalMatchValue.text = totalMatches.toString()
                        viewModel.totalWinAgainstOpponent.observe(viewLifecycleOwner){
                            binding.wonMatchValue.text=it.toString()
                            binding.lostMatchValue.text=(totalMatches-it).toString()
                            binding.winPercentageMatchValue.text=String.format("%.2f",((it.toDouble()*100.0)/totalMatches.toDouble()))
                            binding.statisticProgress.progress=(it*100)/totalMatches
                        }
                        viewModel.totalScoreCurrrentAndOpponent.observe(viewLifecycleOwner){(user,opponent)->
                            binding.currentUserTotalScore.text=user.toString()
                            binding.opponentUserTotalScore.text=opponent.toString()
                            binding.currentUserScore.text=String.format("%.2f",(user.toDouble()/totalMatches.toDouble()))
                            binding.opponentUserScore.text=String.format("%.2f",(opponent.toDouble()/totalMatches.toDouble()))

                            viewModel.totalSetWithOpponent.observe(viewLifecycleOwner){totalSet->
                                binding.totalSet.text=totalSet.toString()
                                updateRadarChart(
                                    matchPerScoreAvgCurrent = (user.toDouble()/totalSet.toDouble()).toFloat(),
                                    set1AvgScoreCurrent = set1AvgScore,
                                    set2AvgScoreCurrent = set2AvgScore,
                                    set3AvgScoreCurrent = set3AvgScore,
                                    set4AvgScoreCurrent = set4AvgScore,
                                    set5AvgScoreCurrent = set5AvgScore,
                                    matchPerScoreAvgOpponent = (opponent.toDouble()/totalSet.toDouble()).toFloat(),
                                    set1AvgScoreOpponent = set1AvgScoreOpponent,
                                    set2AvgScoreOpponent = set2AvgScoreOpponent,
                                    set3AvgScoreOpponent = set3AvgScoreOpponent,
                                    set4AvgScoreOpponent = set4AvgScoreOpponent,
                                    set5AvgScoreOpponent = set5AvgScoreOpponent
                                )
                            }
                        }
                    }
                    else{
                        Snackbar.make(binding.root, requireContext().getString(R.string.Therearenomatchesplayedwiththeselectedcompetitor), Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    private fun updateRadarChart(
        matchPerScoreAvgCurrent: Float,
        set1AvgScoreCurrent: Float,
        set2AvgScoreCurrent: Float,
        set3AvgScoreCurrent: Float,
        set4AvgScoreCurrent: Float,
        set5AvgScoreCurrent: Float,
        matchPerScoreAvgOpponent: Float,
        set1AvgScoreOpponent: Float,
        set2AvgScoreOpponent: Float,
        set3AvgScoreOpponent: Float,
        set4AvgScoreOpponent: Float,
        set5AvgScoreOpponent: Float
    ) {
        val entriesCurrent = ArrayList<RadarEntry>()
        val entriesOpponent = ArrayList<RadarEntry>()

        entriesCurrent.add(RadarEntry(matchPerScoreAvgCurrent))
        entriesCurrent.add(RadarEntry(set2AvgScoreCurrent))
        entriesCurrent.add(RadarEntry(set4AvgScoreCurrent))
        entriesCurrent.add(RadarEntry(set5AvgScoreCurrent))
        entriesCurrent.add(RadarEntry(set3AvgScoreCurrent))
        entriesCurrent.add(RadarEntry(set1AvgScoreCurrent))

        entriesOpponent.add(RadarEntry(matchPerScoreAvgOpponent))
        entriesOpponent.add(RadarEntry(set2AvgScoreOpponent))
        entriesOpponent.add(RadarEntry(set4AvgScoreOpponent))
        entriesOpponent.add(RadarEntry(set5AvgScoreOpponent))
        entriesOpponent.add(RadarEntry(set3AvgScoreOpponent))
        entriesOpponent.add(RadarEntry(set1AvgScoreOpponent))

        val dataSetCurrent = RadarDataSet(entriesCurrent, "Current User")
        dataSetCurrent.color = Color.BLUE
        dataSetCurrent.fillColor = Color.BLUE
        dataSetCurrent.lineWidth = 1.5f
        dataSetCurrent.setDrawFilled(true)
        dataSetCurrent.fillAlpha = 80

        val dataSetOpponent = RadarDataSet(entriesOpponent, "Opponent")
        dataSetOpponent.color = Color.RED
        dataSetOpponent.fillColor = Color.RED
        dataSetOpponent.lineWidth = 1.5f
        dataSetOpponent.setDrawFilled(true)
        dataSetOpponent.fillAlpha = 80

        val radarData = RadarData(dataSetCurrent, dataSetOpponent)
        binding.performanceChart.data = radarData
        binding.performanceChart.xAxis.textSize = 10f
        binding.performanceChart.yAxis.textSize = 10f
        binding.performanceChart.legend.textSize = 11f

        binding.performanceChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return when (value.toInt()) {
                    0 -> "Skor"
                    1 -> "Set 2"
                    2 -> "Set 4"
                    3 -> "Set 5"
                    4 -> "Set 3"
                    5 -> "Set 1"
                    else -> ""
                }
            }
        }

        binding.performanceChart.yAxis.axisMaximum = 11f
        binding.performanceChart.yAxis.axisMinimum = 0f
        binding.performanceChart.xAxis.axisMaximum = 11f
        binding.performanceChart.xAxis.axisMinimum = 0f
        binding.performanceChart.yAxis.setDrawLabels(false)
        binding.performanceChart.yAxis.setDrawGridLines(false)
        binding.performanceChart.xAxis.setDrawGridLines(false)
        binding.performanceChart.yAxis.setDrawAxisLine(false)
        binding.performanceChart.xAxis.setDrawAxisLine(false)
        binding.performanceChart.yAxis.setDrawLabels(false)
        binding.performanceChart.yAxis.setDrawGridLines(false)
        binding.performanceChart.xAxis.setDrawGridLines(false)

        binding.performanceChart.animateXY(1000, 1000)
        binding.performanceChart.invalidate()
    }

    override fun onResume() {
        super.onResume()
        auth.currentUser?.email?.let { email ->
            viewModel.getUserNameByEmail(email) { userNameResult ->
                userNameResult?.let { userName ->
                    viewModel.getfriends(userName)
                }
            }
        }
    }

}
