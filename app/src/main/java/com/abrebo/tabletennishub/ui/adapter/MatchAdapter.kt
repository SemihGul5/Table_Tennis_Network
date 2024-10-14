package com.abrebo.tabletennishub.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.data.model.Match
import com.abrebo.tabletennishub.databinding.MatchItemBinding
import com.abrebo.tabletennishub.ui.fragment.MainFragmentDirections
import com.abrebo.tabletennishub.ui.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

class MatchAdapter(var context:Context,
                   var matchlist:List<Match>,
                   var viewModel:MainViewModel,
                   var auth:FirebaseAuth
):RecyclerView.Adapter<MatchAdapter.MatchItemHolder>() {
    inner class MatchItemHolder(var binding:MatchItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchItemHolder {
        val binding=MatchItemBinding.inflate(LayoutInflater.from(context),parent,false)
        return MatchItemHolder(binding)
    }

    override fun getItemCount(): Int {
        return matchlist.size
    }

    override fun onBindViewHolder(holder: MatchItemHolder, position: Int) {
        val match=matchlist.get(position)
        val binding=holder.binding

        binding.dateTextView.text=match.date
        binding.homeTeamTextView.text=match.userHome
        binding.homeScoreTextView.text=match.userHomeScore.toString()
        binding.awayScoreTextView.text=match.userAwayScore.toString()
        binding.awayTeamTextView.text=match.userAway

        viewModel.getUserNameByEmail(auth.currentUser?.email!!){
            if (it != null) {
                bind(match,binding,it)
            }
        }

        if (position%2==0){
            binding.linearLayoutMatchItem.setBackgroundColor(context.getColor(R.color.white))
        }else{
            binding.linearLayoutMatchItem.setBackgroundColor(context.getColor(R.color.grey))
        }

        binding.linearLayoutMatchItem.setOnClickListener {
            val navDirections=MainFragmentDirections.actionMainFragmentToUpdateMatchFragment(match)
            Navigation.findNavController(it).navigate(navDirections)
        }

    }
    @SuppressLint("UseCompatLoadingForDrawables")
    fun bind(match: Match, binding: MatchItemBinding, userName:String) {
        if (userName == match.userHome) {
            if (match.userHomeScore > match.userAwayScore) {
                binding.winImageView.setImageDrawable(context.getDrawable(R.drawable.baseline_check_circle_24))
            } else if (match.userHomeScore == match.userAwayScore) {
                binding.winImageView.setImageDrawable(context.getDrawable(R.drawable.baseline_remove_circle_24))
            } else {
                binding.winImageView.setImageDrawable(context.getDrawable(R.drawable.delete))
            }
        } else {
            if (match.userHomeScore > match.userAwayScore) {
                binding.winImageView.setImageDrawable(context.getDrawable(R.drawable.delete))
            } else if (match.userHomeScore == match.userAwayScore) {
                binding.winImageView.setImageDrawable(context.getDrawable(R.drawable.baseline_remove_circle_24))
            } else {
                binding.winImageView.setImageDrawable(context.getDrawable(R.drawable.baseline_check_circle_24))
            }
        }
    }
}