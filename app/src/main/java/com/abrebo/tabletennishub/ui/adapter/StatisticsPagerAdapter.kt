package com.abrebo.tabletennishub.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.abrebo.tabletennishub.ui.fragment.Statistics.CompareStatisticsFragment
import com.abrebo.tabletennishub.ui.fragment.Statistics.MyStatisticsFragment


class StatisticsPagerAdapter(fa:FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> MyStatisticsFragment()
            1 -> CompareStatisticsFragment()
            else -> MyStatisticsFragment()
        }
    }
}