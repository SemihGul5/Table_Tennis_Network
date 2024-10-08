package com.abrebo.tabletennishub.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abrebo.tabletennishub.databinding.ItemReceivedRequestBinding
import com.abrebo.tabletennishub.ui.viewmodel.RequestsViewModel
import com.abrebo.tabletennishub.utils.PageType

class RequestsAdapter(var context: Context,
                      var requestList: List<String>,
                      var viewModel:RequestsViewModel,
                      var pageType: PageType):RecyclerView.Adapter<RequestsAdapter.ItemReceivedRequestHolder>() {
    inner class ItemReceivedRequestHolder(var binding:ItemReceivedRequestBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemReceivedRequestHolder {
        val binding=ItemReceivedRequestBinding.inflate(LayoutInflater.from(context),parent,false)
        return ItemReceivedRequestHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: ItemReceivedRequestHolder, position: Int) {
        val userEmail=requestList.get(position)
        val binding=holder.binding
        holder.binding.textViewUserName.text=userEmail

        if (pageType==PageType.SENT_REQUESTS){
            binding.buttonDeclineRequest.visibility=View.GONE
            binding.buttonAcceptRequest.text="Geri çek"
        }

        binding.buttonAcceptRequest.setOnClickListener {
            if (pageType==PageType.SENT_REQUESTS){

            }else{

            }
        }

        binding.buttonDeclineRequest.setOnClickListener {
            //sadece alınanda çalışıcak
        }


    }
}