package com.abrebo.tabletennishub.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentMyInformationBinding
import com.abrebo.tabletennishub.ui.viewmodel.SettingsViewModel
import com.abrebo.tabletennishub.utils.PageType
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyInformationFragment : Fragment() {
    private lateinit var binding:FragmentMyInformationBinding
    private lateinit var viewModel:SettingsViewModel
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
        val temp:SettingsViewModel by viewModels()
        viewModel=temp
        viewModel.getUserInfo(auth.currentUser?.email!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentMyInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.map.observe(viewLifecycleOwner){map->
            binding.nameFamilyText.setText(map["nameFamily"].toString())
            binding.userNameText.setText(map["userName"].toString())
            binding.emailText.setText(map["email"].toString())
        }

        binding.userNameText.setOnClickListener {
            val navDirection=MyInformationFragmentDirections.actionMyInformationFragmentToUpdateUserNameFragment(PageType.USER_NAME)
            Navigation.findNavController(it).navigate(navDirection)
        }
        binding.nameFamilyText.setOnClickListener {
            val navDirection=MyInformationFragmentDirections.actionMyInformationFragmentToUpdateUserNameFragment(PageType.NAME_FAMILY)
            Navigation.findNavController(it).navigate(navDirection)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserInfo(auth.currentUser?.email!!)
    }
}