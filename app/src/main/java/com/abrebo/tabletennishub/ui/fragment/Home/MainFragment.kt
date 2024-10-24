package com.abrebo.tabletennishub.ui.fragment.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentMainBinding
import com.abrebo.tabletennishub.ui.adapter.MatchAdapter
import com.abrebo.tabletennishub.ui.viewmodel.Home.MainViewModel
import com.abrebo.tabletennishub.utils.BackPressUtils
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding:FragmentMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var auth:FirebaseAuth
    private var currentUserName:String=""
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val temp: MainViewModel by viewModels()
        viewModel = temp
        val currentUserEmail = auth.currentUser?.email
        if (currentUserEmail != null) {
            viewModel.getUserNameByEmail(currentUserEmail) { userName ->
                if (userName != null) {
                    currentUserName = userName
                    viewModel.getMatchesByUserName(currentUserName)
                    viewModel.getfriends(currentUserName)
                }
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding= FragmentMainBinding.inflate(inflater, container, false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BackPressUtils.setBackPressCallback(this, viewLifecycleOwner)



        binding.floatingActionButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_mainFragment_to_addMatchFragment)
        }

        viewModel.matches.observe(viewLifecycleOwner){
            val adapter=MatchAdapter(requireContext(),it,viewModel,auth)
            binding.matchRecyclerView.adapter=adapter
        }
        binding.materialToolbar.setOnMenuItemClickListener {
            if (it.itemId==R.id.filterMatches){
                handleFilterMenuItem()
            }

            true
        }

    }
    private fun handleFilterMenuItem() {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheet = layoutInflater.inflate(R.layout.main_page_bottom_sheet, null)
        val listView = bottomSheet.findViewById<ListView>(R.id.listViewBottomSheet)

        viewModel.friends.observe(viewLifecycleOwner) { friendsList ->
            val modifiedList = mutableListOf("Tüm Maçlar")
            modifiedList.addAll(friendsList.sorted())
            val listViewAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, modifiedList)
            listView.adapter = listViewAdapter
        }

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItemText = (parent.getItemAtPosition(position) as String)
            if (selectedItemText == "Tüm Maçlar") {
                viewModel.getMatchesByUserName(currentUserName)
                dialog.dismiss()
            } else {
                val opponent = selectedItemText
                viewModel.getMatchesByUserNameWithFilter(currentUserName, opponent)
                dialog.dismiss()
            }
            viewModel.matches.observe(viewLifecycleOwner){
                val adapter=MatchAdapter(requireContext(),it,viewModel,auth)
                binding.matchRecyclerView.adapter=adapter
            }
        }
        dialog.setContentView(bottomSheet)
        dialog.show()
    }
    override fun onResume() {
        super.onResume()
        val currentUserEmail = auth.currentUser?.email
        if (currentUserEmail != null) {
            viewModel.getUserNameByEmail(currentUserEmail) { userName ->
                if (userName != null) {
                    currentUserName = userName
                    viewModel.getMatchesByUserName(currentUserName)
                }
            }
        }
    }

}