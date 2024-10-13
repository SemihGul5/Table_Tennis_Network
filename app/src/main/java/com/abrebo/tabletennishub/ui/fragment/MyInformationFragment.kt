package com.abrebo.tabletennishub.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.data.model.User
import com.abrebo.tabletennishub.databinding.FragmentMyInformationBinding
import com.abrebo.tabletennishub.ui.viewmodel.SettingsViewModel
import com.google.android.material.snackbar.Snackbar
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
            binding.updateButton.setOnClickListener {
                viewModel.checkUserNameAvailability(binding.userNameText.text.toString())
                if (binding.nameFamilyText.text!!.isNotEmpty()&&binding.userNameText.text!!.isNotEmpty()){
                    viewModel.userNameAvailability.observe(viewLifecycleOwner){isAvailability->
                        if (isAvailability){
                            val user= User(map["id"].toString(),
                                binding.nameFamilyText.text.toString(),
                                binding.userNameText.text.toString(),
                                map["email"].toString())
                            viewModel.updateUserData(user)
                            Snackbar.make(it,"Bilgileriniz güncellendi",Snackbar.LENGTH_SHORT).show()
                        }else{if (binding.userNameText.text.toString()!=map["userName"]){
                            Snackbar.make(it,"Bu kullanıcı adı zaten kullanılıyor",Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }else{
                    Snackbar.make(it,"Boş alan olmamalıdır.",Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}