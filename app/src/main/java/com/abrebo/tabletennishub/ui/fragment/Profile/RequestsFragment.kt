package com.abrebo.tabletennishub.ui.fragment.Profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentRequestsBinding
import com.abrebo.tabletennishub.ui.adapter.RequestsPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestsFragment : Fragment() {
    private lateinit var binding:FragmentRequestsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentRequestsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RequestsPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        // TabLayout ve ViewPager arasında bağ kur
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) requireContext().getString(R.string.IncomingRequests) else
                requireContext().getString(R.string.SentRequests)
        }.attach()
    }

}