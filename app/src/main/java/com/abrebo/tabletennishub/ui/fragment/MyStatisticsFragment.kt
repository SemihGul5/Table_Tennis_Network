package com.abrebo.tabletennishub.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.abrebo.tabletennishub.databinding.FragmentMyStatisticsBinding
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
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MyStatisticsFragment : Fragment() {
    private lateinit var binding:FragmentMyStatisticsBinding
    private lateinit var viewModel:StatisticsViewModel
    private lateinit var auth: FirebaseAuth
    private var totalMatch=0
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
        val temp:StatisticsViewModel by viewModels()
        viewModel=temp
        viewModel.getUserNameByEmail(auth.currentUser?.email!!){
            if (it!=null){
                viewModel.getSetWinRates(it)
                viewModel.getAverageScorePerMatch(it)
                viewModel.getTotalMatchesByUser(it)
                viewModel.loadMatchResults(it)
                viewModel.getAverageScorePerMatch(it)
                viewModel.getSetAverageScores(it)
                viewModel.getTotalSetsPlayed(it)
                viewModel.getTotalPointsWon(it)
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentMyStatisticsBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext()) {}

        // Setup Banner Ad
        adView = AdView(requireContext())
        adView.adUnitId = "ca-app-pub-4667560937795938/1527626744"
        adView.setAdSize(AdSize.BANNER)
        binding.adView.removeAllViews()
        binding.adView.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        return binding.root
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setWinRates.observe(viewLifecycleOwner) { winRates ->
            for (setNumber in 0..4) {
                val winRate = winRates[setNumber] ?: 0.0
                when (setNumber) {
                    0 -> binding.set1WinText.text = String.format("%.2f%%", winRate * 100)
                    1 -> binding.set2WinText.text = String.format("%.2f%%", winRate * 100)
                    2 -> binding.set3WinText.text = String.format("%.2f%%", winRate * 100)
                    3 -> binding.set4WinText.text = String.format("%.2f%%", winRate * 100)
                    4 -> binding.set5WinText.text = String.format("%.2f%%", winRate * 100)
                }
            }
        }



        viewModel.matchResults.observe(viewLifecycleOwner) { (wins, losses) ->
            binding.wonMatchValue.text = wins.toString()
            binding.lostMatchValue.text = losses.toString()
            totalMatch = wins + losses
            val percentage: Double = if (totalMatch > 0) {
                (wins.toDouble() / totalMatch) * 100
            } else {
                0.0
            }
            binding.winPercentageMatchValue.text = String.format("%.2f", percentage) + "%"
            binding.statisticProgress.progress = percentage.toInt()
        }


        viewModel.setAvgScores.observe(viewLifecycleOwner) { avgScoresMap ->

            val set1AvgScore = avgScoresMap[0]?.toFloat() ?: 0.0f
            val set2AvgScore = avgScoresMap[1]?.toFloat() ?: 0.0f
            val set3AvgScore = avgScoresMap[2]?.toFloat() ?: 0.0f
            val set4AvgScore = avgScoresMap[3]?.toFloat() ?: 0.0f
            val set5AvgScore = avgScoresMap[4]?.toFloat() ?: 0.0f

            binding.set1AvgScoreText.text = String.format("%.2f", set1AvgScore)
            binding.set2AvgScoreText.text = String.format("%.2f", set2AvgScore)
            binding.set3AvgScoreText.text = String.format("%.2f", set3AvgScore)
            binding.set4AvgScoreText.text = String.format("%.2f", set4AvgScore)
            binding.set5AvgScoreText.text = String.format("%.2f", set5AvgScore)
            viewModel.totalMatches.observe(viewLifecycleOwner){totalMatches->
                totalMatch=totalMatches
                binding.totalMatchValue.text=totalMatches.toString()
                viewModel.totalScore.observe(viewLifecycleOwner){totalScore->
                    binding.totalScore.text=totalScore.toString()
                    binding.matchPerScoreAvgText.text=String.format("%.2f", (totalScore.toDouble()/totalMatch.toDouble()))
                    viewModel.totalSet.observe(viewLifecycleOwner){totalSet->
                        binding.totalSet.text=String.format(totalSet.toString())
                        updateRadarChart(
                            matchPerScoreAvg = (totalScore.toDouble()/totalSet.toDouble()).toFloat(),
                            set1AvgScore = set1AvgScore,
                            set2AvgScore = set2AvgScore,
                            set3AvgScore = set3AvgScore,
                            set4AvgScore = set4AvgScore,
                            set5AvgScore = set5AvgScore
                        )
                    }
                }

            }

        }
        viewModel.averageScorePerMatch.observe(viewLifecycleOwner) { averageScorePerMatch ->
            binding.averageScorePerMatch.text = String.format("%.2f", averageScorePerMatch)
        }

    }

    private fun updateRadarChart(
        matchPerScoreAvg: Float,
        set1AvgScore: Float,
        set2AvgScore: Float,
        set3AvgScore: Float,
        set4AvgScore: Float,
        set5AvgScore: Float
    ) {
        val entries = ArrayList<RadarEntry>()

        entries.add(RadarEntry(matchPerScoreAvg))
        entries.add(RadarEntry(set2AvgScore))
        entries.add(RadarEntry(set4AvgScore))
        entries.add(RadarEntry(set5AvgScore))
        entries.add(RadarEntry(set3AvgScore))
        entries.add(RadarEntry(set1AvgScore))

        val dataSet = RadarDataSet(entries, "Performance")
        dataSet.color = Color.BLUE
        dataSet.fillColor = Color.BLUE
        dataSet.lineWidth = 1.5f
        dataSet.setDrawFilled(true)
        dataSet.fillAlpha = 80
        dataSet.isDrawHighlightCircleEnabled=true
        dataSet.setDrawHighlightIndicators(false)

        val radarData = RadarData(dataSet)
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
        binding.performanceChart.legend.isEnabled=false
        binding.performanceChart.animateXY(1000, 1000)
        binding.performanceChart.invalidate()
    }

    override fun onResume() {
        super.onResume()
        auth=FirebaseAuth.getInstance()
        val temp:StatisticsViewModel by viewModels()
        viewModel=temp
        viewModel.getUserNameByEmail(auth.currentUser?.email!!){
            if (it!=null){
                viewModel.getSetWinRates(it)
                viewModel.getAverageScorePerMatch(it)
                viewModel.getTotalMatchesByUser(it)
                viewModel.loadMatchResults(it)
                viewModel.getAverageScorePerMatch(it)
                viewModel.getSetAverageScores(it)
                viewModel.getTotalSetsPlayed(it)
                viewModel.getTotalPointsWon(it)
            }
        }
    }

}