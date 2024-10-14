package com.abrebo.tabletennishub.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentAddFriendBinding
import com.abrebo.tabletennishub.ui.adapter.AddFriendAdapter
import com.abrebo.tabletennishub.ui.viewmodel.AddFriendViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddFriendFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding:FragmentAddFriendBinding
    private lateinit var viewModel:AddFriendViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
        val temp:AddFriendViewModel by viewModels()
        viewModel=temp
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentAddFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clearUserList()
        viewModel.userList.observe(viewLifecycleOwner){userList->

            val adapter=AddFriendAdapter(requireContext(),userList,viewModel,auth)
            binding.recyclerViewAddFriend.adapter=adapter
        }


        binding.searchFriendText.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //arama kelimesi girilirkenki durum
                if (p0.toString().isNotEmpty()){
                    val searchText= p0.toString().trim()
                    viewModel.search(searchText)
                }else{
                    //arama yapılmadan önceki durum
                    viewModel.clearUserList()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

}