package com.abrebo.tabletennishub.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.ItemReceivedRequestBinding
import com.abrebo.tabletennishub.ui.viewmodel.Profile.RequestsViewModel
import com.abrebo.tabletennishub.utils.PageType
import com.google.firebase.auth.FirebaseAuth

class RequestsAdapter(var context: Context,
                      var requestList: List<String>,
                      var viewModel: RequestsViewModel,
                      var pageType: PageType,
                      var auth: FirebaseAuth):RecyclerView.Adapter<RequestsAdapter.ItemReceivedRequestHolder>() {
    inner class ItemReceivedRequestHolder(var binding:ItemReceivedRequestBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemReceivedRequestHolder {
        val binding=ItemReceivedRequestBinding.inflate(LayoutInflater.from(context),parent,false)
        return ItemReceivedRequestHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: ItemReceivedRequestHolder, position: Int) {
        val userName=requestList.get(position)
        val binding=holder.binding
        holder.binding.textViewUserName.text=userName

        if (pageType==PageType.SENT_REQUESTS){
            binding.buttonDeclineRequest.visibility=View.GONE
            binding.buttonAcceptRequest.text=context.getString(R.string.Withdraw)
        }else if(pageType==PageType.FRIENDS){
            binding.buttonDeclineRequest.visibility=View.GONE
            binding.buttonAcceptRequest.text=context.getString(R.string.Delete)
        }

        binding.buttonAcceptRequest.setOnClickListener {
            viewModel.getUserNameByEmail(auth.currentUser?.email!!){
                if (it!=null){
                    if (pageType==PageType.SENT_REQUESTS){
                        viewModel.withdrawFriendRequest(it,userName)
                    }else if(pageType==PageType.FRIENDS){
                        viewModel.removeFriend(it,userName)
                    }else{
                        viewModel.acceptFriendRequest(it,userName)
                    }
                }
            }
        }

        binding.buttonDeclineRequest.setOnClickListener {
            viewModel.getUserNameByEmail(auth.currentUser?.email!!){
                if (it!=null){
                    viewModel.declineFriendRequest(it,userName)
                }
            }
        }


    }
}