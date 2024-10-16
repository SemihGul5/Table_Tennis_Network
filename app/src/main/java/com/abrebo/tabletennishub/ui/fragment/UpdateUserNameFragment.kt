package com.abrebo.tabletennishub.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.data.model.User
import com.abrebo.tabletennishub.databinding.FragmentUpdateUserNameBinding
import com.abrebo.tabletennishub.ui.viewmodel.SettingsViewModel
import com.abrebo.tabletennishub.utils.PageType
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateUserNameFragment : Fragment() {
    private lateinit var binding:FragmentUpdateUserNameBinding
    private lateinit var viewModel:SettingsViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
        val temp:SettingsViewModel by viewModels()
        viewModel=temp
        viewModel.getUserInfo(auth.currentUser?.email!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding= FragmentUpdateUserNameBinding.inflate(inflater, container, false)
        MobileAds.initialize(requireContext()) {}

        // Setup Banner Ad
        adView = AdView(requireContext())
        adView.adUnitId = "ca-app-pub-4667560937795938/9141888302"
        adView.setAdSize(AdSize.BANNER)
        binding.adView.removeAllViews()
        binding.adView.addView(adView)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pageType=UpdateUserNameFragmentArgs.fromBundle(requireArguments()).PageType
        if (pageType==PageType.USER_NAME){
            binding.materialToolbar7.setTitle(requireContext().getString(R.string.ChangeUsername))
            binding.textInputLayoutUserName.setHint(requireContext().getString(R.string.newUsername))
            viewModel.map.observe(viewLifecycleOwner){map->
                val nameFamily=map["nameFamily"].toString()
                val userName=map["userName"].toString()
                val email=map["email"].toString()
                val id=map["id"].toString()
                binding.userNameUpdateButton.setOnClickListener {
                    if (binding.userNameText.text!!.isNotEmpty()){
                        viewModel.checkUserNameAvailability(binding.userNameText.text.toString())
                        viewModel.userNameAvailability.observe(viewLifecycleOwner){isAvailability->
                            if (isAvailability){
                                val user=User(id,nameFamily,binding.userNameText.text.toString(),email)
                                viewModel.updateUserData(user)
                                viewModel.updateUserDocumentId(userName,binding.userNameText.text.toString())
                                viewModel.updateWinnerUserName(userName,binding.userNameText.text.toString())
                                viewModel.updateUserHomeUserName(userName,binding.userNameText.text.toString())
                                viewModel.updateUserAwayUserName(userName,binding.userNameText.text.toString())
                                viewModel.updateUserNameInDocuments(userName,binding.userNameText.text.toString())
                                viewModel.updateUserNameInFriendRequests(userName,binding.userNameText.text.toString())
                                Toast.makeText(requireContext(),
                                    requireContext().getString(R.string.Usernamehasbeensuccessfullyupdated)
                                    ,Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(requireContext(),
                                    requireContext().getString(R.string.Usernamealreadyexists),
                                    Toast.LENGTH_SHORT).show()
                            }

                        }
                    }else{
                        Toast.makeText(requireContext(),
                            requireContext().getString(R.string.Usernamecannotbeempty),
                            Toast.LENGTH_SHORT).show()

                    }
                }

            }
        }else{
            binding.materialToolbar7.setTitle(requireContext().getString(R.string.ChangeFirstNameandLastName))
            binding.textInputLayoutUserName.setHint(requireContext().getString(R.string.NewFirstNameandLastName))
            binding.userNameUpdateButton.setOnClickListener {
                if (binding.userNameText.text!!.isNotEmpty()){
                    viewModel.map.observe(viewLifecycleOwner){map->
                        val nameFamily=map["nameFamily"].toString()
                        val userName=map["userName"].toString()
                        val email=map["email"].toString()
                        val id=map["id"].toString()
                        val user=User(id,binding.userNameText.text.toString(),userName,email)
                        viewModel.updateUserData(user)
                        Toast.makeText(requireContext(),
                            requireContext().getString(R.string.FirstNameandLastNamehavebeensuccessfullyupdated),
                            Toast.LENGTH_SHORT).show()

                    }
                }else{
                    Toast.makeText(requireContext(),
                        requireContext().getString(R.string.FirstNameandLastNamecannotbeempty),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}