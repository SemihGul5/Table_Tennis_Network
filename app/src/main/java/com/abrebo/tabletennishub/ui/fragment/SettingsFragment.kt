package com.abrebo.tabletennishub.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.MainActivity
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentSettingsBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding:FragmentSettingsBinding
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentSettingsBinding.inflate(inflater, container, false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsList=ArrayList<String>()
        settingsList.add(requireContext().getString(R.string.MyInformation))
        settingsList.add(requireContext().getString(R.string.SharetheApplication))
        settingsList.add(requireContext().getString(R.string.SupportandContact))
        settingsList.add(requireContext().getString(R.string.LogOut))

        val adapter=ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,settingsList)
        binding.settingListView.adapter=adapter

        binding.settingListView.setOnItemClickListener { adapterView, view, i, l ->
            when(i){
                0->{Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_myInformationFragment)}
                1->{shareApp()}
                2->{Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_supportAndContactFragment)}
                3->{
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }

            }
        }
    }
    fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val appPackageName = context?.packageName
        val shareMessage = requireContext().getString(R.string.Irecommendtryingthisamazingapp)

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, requireContext().getString(R.string.SharetheApplication)))
    }



}