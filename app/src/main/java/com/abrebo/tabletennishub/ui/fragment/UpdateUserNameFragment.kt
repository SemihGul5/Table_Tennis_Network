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
import com.abrebo.tabletennishub.databinding.FragmentMyInformationBinding
import com.abrebo.tabletennishub.databinding.FragmentUpdateUserNameBinding
import com.abrebo.tabletennishub.ui.viewmodel.SettingsViewModel
import com.abrebo.tabletennishub.utils.PageType
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateUserNameFragment : Fragment() {
    private lateinit var binding:FragmentUpdateUserNameBinding
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
        binding= FragmentUpdateUserNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pageType=UpdateUserNameFragmentArgs.fromBundle(requireArguments()).PageType
        if (pageType==PageType.USER_NAME){
            binding.materialToolbar7.setTitle("Kullanıcı Adı Değiştir")
            binding.textInputLayoutUserName.setHint("Yeni Kullanıcı Adı")
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

                                Toast.makeText(requireContext(),"Kullanıcı adı başarıyla güncellendi",Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(requireContext(),"Bu kullanıcı adı zaten mevcut",Toast.LENGTH_SHORT).show()
                            }

                        }
                    }else{
                        Toast.makeText(requireContext(), "Kullanıcı adı boş olamaz", Toast.LENGTH_SHORT).show()

                    }
                }

            }
        }else{
            binding.materialToolbar7.setTitle("Ad Soyad Değiştir")
            binding.textInputLayoutUserName.setHint("Yeni Ad Soyad")
            binding.userNameUpdateButton.setOnClickListener {
                if (binding.userNameText.text!!.isNotEmpty()){
                    viewModel.map.observe(viewLifecycleOwner){map->
                        val nameFamily=map["nameFamily"].toString()
                        val userName=map["userName"].toString()
                        val email=map["email"].toString()
                        val id=map["id"].toString()
                        val user=User(id,binding.userNameText.text.toString(),userName,email)
                        viewModel.updateUserData(user)
                        Toast.makeText(requireContext(),"Ad soyad başarıyla güncellendi",Toast.LENGTH_SHORT).show()

                    }
                }else{
                    Toast.makeText(requireContext(), "Ad soyad boş olamaz", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}