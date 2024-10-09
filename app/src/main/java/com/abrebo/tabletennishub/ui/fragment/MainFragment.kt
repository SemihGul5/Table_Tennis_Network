package com.abrebo.tabletennishub.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentMainBinding
import com.abrebo.tabletennishub.ui.adapter.MatchAdapter
import com.abrebo.tabletennishub.ui.viewmodel.MainViewModel
import com.abrebo.tabletennishub.utils.BackPressUtils
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding:FragmentMainBinding
    private lateinit var viewModel:MainViewModel
    private lateinit var auth:FirebaseAuth
    private var currentUserName:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
        val temp:MainViewModel by viewModels()
        viewModel=temp
        viewModel.getUserNameByEmail(auth.currentUser?.email!!){
            if (it!=null){
                currentUserName=it
                viewModel.getMatchesByUserName(it)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding= FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BackPressUtils.setBackPressCallback(this, viewLifecycleOwner)

        binding.floatingActionButton.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_mainFragment_to_addMatchFragment)
        }

        viewModel.matches.observe(viewLifecycleOwner){
            val adapter=MatchAdapter(requireContext(),it)
            binding.matchRecyclerView.adapter=adapter
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getMatchesByUserName(currentUserName)
    }

}