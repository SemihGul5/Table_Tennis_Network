package com.abrebo.tabletennishub.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.abrebo.tabletennishub.ui.fragment.Profile.ReceivedRequestsFragment
import com.abrebo.tabletennishub.ui.fragment.Profile.SentRequestsFragment

class RequestsPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ReceivedRequestsFragment()
            1 -> SentRequestsFragment()
            else -> ReceivedRequestsFragment() // Varsayılan olarak Gelen İstekler
        }
    }
}
