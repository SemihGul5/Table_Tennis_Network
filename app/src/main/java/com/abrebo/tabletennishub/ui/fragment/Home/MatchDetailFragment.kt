package com.abrebo.tabletennishub.ui.fragment.Home

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentMatchDetailBinding
import com.abrebo.tabletennishub.ui.viewmodel.Home.MainViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchDetailFragment : Fragment() {
    private lateinit var binding:FragmentMatchDetailBinding
    private val viewModel:MainViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private var currentUserName:String=""
    private lateinit var adView: AdView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=FragmentMatchDetailBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext()) {}

        // Setup Banner Ad
        adView = AdView(requireContext())
        adView.adUnitId = "ca-app-pub-4667560937795938/5466871752"
        adView.setAdSize(AdSize.BANNER)
        binding.adView.removeAllViews()
        binding.adView.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val match=MatchDetailFragmentArgs.fromBundle(requireArguments()).match
        binding.homeTeamTextView.text=match.userHome
        binding.awayTeamTextView.text=match.userAway
        binding.homeTeamScoreText.text=match.userHomeScore.toString()
        binding.awayTeamScoreText.text=match.userAwayScore.toString()
        viewModel.getUserNameByEmail(auth.currentUser?.email!!){
            if (it!=null){
                currentUserName=it
                if (currentUserName==match.userHome){
                    if(match.confirmStatusHome){
                        binding.confirmButton.visibility=View.GONE
                    }
                }else{
                    if(match.confirmStatusAway){
                        binding.confirmButton.visibility=View.GONE
                    }
                }
            }
        }

        if (match.confirmStatusHome&&match.confirmStatusAway){
            binding.linearLayoutConfirmStatus.visibility=View.GONE
            binding.linearLayoutConfirmStatus2.visibility=View.GONE
            binding.confirmButton.visibility=View.GONE
            binding.lastUpdateDateText.visibility=View.GONE
        }

        binding.currentSet1Value.text = match.setScores.getOrNull(0)?.userScore.toString()
        binding.opponentSet1Value.text = match.setScores.getOrNull(0)?.opponentScore.toString()
        if (match.setScores[0].userScore>=match.setScores[0].opponentScore){
            binding.currentSet1Value.setTypeface(null, Typeface.BOLD)
            binding.opponentSet1Value.setTypeface(null, Typeface.NORMAL)
        }else{
            binding.currentSet1Value.setTypeface(null, Typeface.NORMAL)
            binding.opponentSet1Value.setTypeface(null, Typeface.BOLD)
        }
        if (match.setScores.size>1){
            binding.currentSet2Value.text = match.setScores[1].userScore.toString()
            binding.opponentSet2Value.text = match.setScores[1].opponentScore.toString()
            if (match.setScores[1].userScore>=match.setScores[1].opponentScore){
                binding.currentSet2Value.setTypeface(null, Typeface.BOLD)
                binding.opponentSet2Value.setTypeface(null, Typeface.NORMAL)
            }else{
                binding.currentSet2Value.setTypeface(null, Typeface.NORMAL)
                binding.opponentSet2Value.setTypeface(null, Typeface.BOLD)
            }
        }else{
            binding.linearLayoutSet2.visibility=View.GONE
            binding.linearLayoutSet3.visibility=View.GONE
            binding.linearLayoutSet4.visibility=View.GONE
            binding.linearLayoutSet5.visibility=View.GONE
        }
        if (match.setScores.size>2){
            binding.currentSet3Value.text = match.setScores[2].userScore.toString()
            binding.opponentSet3Value.text = match.setScores[2].opponentScore.toString()
            if (match.setScores[2].userScore>=match.setScores[2].opponentScore){
                binding.currentSet3Value.setTypeface(null, Typeface.BOLD)
                binding.opponentSet3Value.setTypeface(null, Typeface.NORMAL)
            }else{
                binding.currentSet3Value.setTypeface(null, Typeface.NORMAL)
                binding.opponentSet3Value.setTypeface(null, Typeface.BOLD)
            }
        }
        else{
            binding.linearLayoutSet3.visibility=View.GONE
            binding.linearLayoutSet4.visibility=View.GONE
            binding.linearLayoutSet5.visibility=View.GONE
        }
        if (match.setScores.size>3){
            binding.currentSet4Value.text = match.setScores[3].userScore.toString()
            binding.opponentSet4Value.text = match.setScores[3].opponentScore.toString()

            if (match.setScores[3].userScore>=match.setScores[3].opponentScore){
                binding.currentSet4Value.setTypeface(null, Typeface.BOLD)
                binding.opponentSet4Value.setTypeface(null, Typeface.NORMAL)
            }else{
                binding.currentSet4Value.setTypeface(null, Typeface.NORMAL)
                binding.opponentSet4Value.setTypeface(null, Typeface.BOLD)
            }
        }else{
            binding.linearLayoutSet4.visibility=View.GONE
            binding.linearLayoutSet5.visibility=View.GONE
        }
        if (match.setScores.size>4){
            binding.currentSet5Value.text = match.setScores[4].userScore.toString()
            binding.opponentSet5Value.text = match.setScores[4].opponentScore.toString()

            if (match.setScores[4].userScore>=match.setScores[4].opponentScore){
                binding.currentSet5Value.setTypeface(null, Typeface.BOLD)
                binding.opponentSet5Value.setTypeface(null, Typeface.NORMAL)
            }else{
                binding.currentSet5Value.setTypeface(null, Typeface.NORMAL)
                binding.opponentSet5Value.setTypeface(null, Typeface.BOLD)
            }
        }else{
            binding.linearLayoutSet5.visibility=View.GONE
        }
        val t="${requireContext().getString(R.string.Lastupdatedate)}: ${match.timestamp.toDate().toString()}"
        binding.lastUpdateDateText.text=t
        binding.confirmHomeTeamNameText.text=match.userHome
        binding.confirmAwayTeamNameText.text=match.userAway


        if (match.confirmStatusHome){
            binding.homeTeamConfirmStatusText.text=requireContext().getString(R.string.Confirmed)
        }else{
            binding.homeTeamConfirmStatusText.text=requireContext().getString(R.string.Pending)
        }
        if (match.confirmStatusAway){
            binding.awayTeamConfirmStatusText.text=requireContext().getString(R.string.Confirmed)
        }else{
            binding.awayTeamConfirmStatusText.text=requireContext().getString(R.string.Pending)
        }



        binding.materialToolbar.setOnMenuItemClickListener {
            if(it.itemId==R.id.update_match){
                if (match.confirmStatusHome&&match.confirmStatusAway){
                    Snackbar.make(requireView(),
                        requireContext().getString(R.string.match_cannot_be_updated),
                        Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.WHITE)
                        .setAction(requireContext().getString(R.string.GotoMatchDeletionPage)){
                            val navDirection=MatchDetailFragmentDirections.actionMatchDetailFragmentToDeleteMatchFragment(match)
                            Navigation.findNavController(binding.root).navigate(navDirection)
                        }.show()
                }else{
                    val navDirection=MatchDetailFragmentDirections.actionMatchDetailFragmentToUpdateMatchFragment(match)
                    Navigation.findNavController(binding.root).navigate(navDirection)
                }
            }else if(it.itemId==R.id.contact_support){
                Navigation.findNavController(binding.root).navigate(R.id.action_matchDetailFragment_to_supportAndContactFragment)
            }

            true
        }

        binding.confirmButton.setOnClickListener {
            if (currentUserName==match.userHome){
               viewModel.updateMatchConfirm(true,true,match.id)
                binding.homeTeamConfirmStatusText.text = requireContext().getString(R.string.Confirmed)
            }else{
                viewModel.updateMatchConfirm(false,true,match.id)
                binding.awayTeamConfirmStatusText.text = requireContext().getString(R.string.Confirmed)
            }
            Toast.makeText(requireContext(),requireContext().getString(R.string.MatchConfirmed),Toast.LENGTH_SHORT).show()
            binding.confirmButton.visibility=View.GONE
        }

    }

}