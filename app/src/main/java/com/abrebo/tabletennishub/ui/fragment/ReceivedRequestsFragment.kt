package com.abrebo.tabletennishub.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.abrebo.tabletennishub.databinding.FragmentReceivedRequestsBinding
import com.abrebo.tabletennishub.ui.adapter.RequestsAdapter
import com.abrebo.tabletennishub.ui.viewmodel.RequestsViewModel
import com.abrebo.tabletennishub.utils.PageType
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceivedRequestsFragment : Fragment() {
    private lateinit var viewModel:RequestsViewModel
    private lateinit var binding:FragmentReceivedRequestsBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val temp: RequestsViewModel by viewModels()
        viewModel = temp
        viewModel.getUserNameByEmail(auth.currentUser!!.email!!) {
            if (it != null) {
                viewModel.fetchReceivedRequests(it)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentReceivedRequestsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.receivedRequests.observe(viewLifecycleOwner){
            val adapter=RequestsAdapter(requireContext(),it,viewModel,PageType.RECEIVED_REQUESTS)
            binding.receivedRequestsRecyclerView.adapter=adapter
        }

    }

}