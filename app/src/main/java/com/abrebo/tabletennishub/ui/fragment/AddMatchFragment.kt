package com.abrebo.tabletennishub.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentAddMatchBinding
import com.abrebo.tabletennishub.ui.viewmodel.AddMatchViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddMatchFragment : Fragment() {
    private lateinit var binding:FragmentAddMatchBinding
    private lateinit var viewModel:AddMatchViewModel
    private lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
        val temp:AddMatchViewModel by viewModels()
        viewModel=temp
        viewModel.getUserNameByEmail(auth.currentUser?.email!!){
            if (it!=null){
                viewModel.getfriends(it)
            }
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentAddMatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.friends.observe(viewLifecycleOwner) { friendList ->
            if (friendList != null) {
                if (friendList.isNotEmpty()) {
                    val friendsAdapter =
                        ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, friendList)
                    binding.autoCompleteTextView.setAdapter(friendsAdapter)
                    binding.autoCompleteTextView.threshold = 1
                } else {
                    Snackbar.make(requireView(),"Rakip listen boş. İlk öncelikle profilden rakip eklemelisin",Snackbar.LENGTH_INDEFINITE)
                        .setActionTextColor(Color.WHITE)
                        .setAction("Tamam"){
                        }.show()
                }
            } else {
                Log.e("AddMatchFragment", "Arkadaş listesi null.")
            }
        }


    }


}