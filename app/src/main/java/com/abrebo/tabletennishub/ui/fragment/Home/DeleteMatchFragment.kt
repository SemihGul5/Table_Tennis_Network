package com.abrebo.tabletennishub.ui.fragment.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.data.model.Match
import com.abrebo.tabletennishub.databinding.FragmentDeleteMatchBinding
import com.abrebo.tabletennishub.ui.viewmodel.Home.AddMatchViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteMatchFragment : Fragment() {
    private lateinit var binding: FragmentDeleteMatchBinding
    private val viewModel: AddMatchViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private var currentUserName = ""
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDeleteMatchBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext()) {}

        // Setup Banner Ad
        adView = AdView(requireContext())
        adView.adUnitId = "ca-app-pub-4667560937795938/5466871752"
        adView.setAdSize(AdSize.BANNER)
        binding.adView.removeAllViews()
        binding.adView.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        viewModel.loadInterstitialAd()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val match = DeleteMatchFragmentArgs.fromBundle(requireArguments()).match
        binding.confirmHomeTeamNameText.text = match.userHome
        binding.confirmAwayTeamNameText.text = match.userAway
        binding.homeTeamConfirmStatusText.text = match.confirmDeleteStatusHome.toString()
        binding.awayTeamConfirmStatusText.text = match.confirmDeleteStatusAway.toString()

        viewModel.getUserNameByEmail(auth.currentUser?.email!!) { userName ->
            if (userName != null) {
                currentUserName = userName
                updateConfirmButtonVisibility(match)
                binding.confirmButton.setOnClickListener {
                    handleConfirmButtonClick(match)
                }
            }
        }
    }

    private fun updateConfirmButtonVisibility(match: Match) {
        if (currentUserName == match.userHome) {
            binding.confirmButton.visibility = if (match.confirmDeleteStatusHome) View.GONE else View.VISIBLE
        } else {
            binding.confirmButton.visibility = if (match.confirmDeleteStatusAway) View.GONE else View.VISIBLE
        }
    }

    private fun handleConfirmButtonClick(match: Match) {
        if (currentUserName == match.userHome) {
            viewModel.updateMatchConfirmDelete(true, true, match.id)
            binding.homeTeamConfirmStatusText.text = "true"
        } else {
            viewModel.updateMatchConfirmDelete(false, true, match.id)
            binding.awayTeamConfirmStatusText.text = "true"
        }
        binding.confirmButton.visibility = View.GONE
        Toast.makeText(requireContext(), "Maç silme onayınız alındı", Toast.LENGTH_SHORT).show()
        if (binding.homeTeamConfirmStatusText.text=="true" && binding.awayTeamConfirmStatusText.text=="true") {
            viewModel.deleteMatchByField(match.id)
            Toast.makeText(
                requireContext(),
                requireContext().getString(R.string.Matchhasbeensuccessfullydeleted),
                Toast.LENGTH_SHORT
            ).show()
            viewModel.showInterstitialAd(requireActivity())
            Navigation.findNavController(binding.root).navigate(R.id.action_deleteMatchFragment_to_mainFragment)
        }
    }
}
