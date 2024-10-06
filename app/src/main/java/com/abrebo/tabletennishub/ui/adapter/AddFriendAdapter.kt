package com.abrebo.tabletennishub.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abrebo.tabletennishub.data.model.User
import com.abrebo.tabletennishub.databinding.CardFriendListBinding
import com.abrebo.tabletennishub.databinding.FragmentAddFriendBinding
import com.abrebo.tabletennishub.ui.viewmodel.AddFriendViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AddFriendAdapter(var context: Context,
                       var userList: List<User>,
                       var viewModel:AddFriendViewModel,
                       var auth: FirebaseAuth
):RecyclerView.Adapter<AddFriendAdapter.AddFriendHolder>() {

    inner class AddFriendHolder(var binding:CardFriendListBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddFriendHolder {
        val binding= CardFriendListBinding.inflate(LayoutInflater.from(context), parent, false)
        return AddFriendHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: AddFriendHolder, position: Int) {
        val user=userList.get(position)
        val binding=holder.binding

        binding.userNameText.text=user.userName
        binding.addFriendButton.setOnClickListener {
            viewModel.sendFriendRequest(context,auth.currentUser!!.email!!,user.email!!)
        }
    }
}