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
    private lateinit var binding: FragmentAddMatchBinding
    private lateinit var viewModel: AddMatchViewModel
    private lateinit var auth: FirebaseAuth
    private var currentUserName:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val temp: AddMatchViewModel by viewModels()
        viewModel = temp
        viewModel.getUserNameByEmail(auth.currentUser?.email!!) {
            if (it != null) {
                currentUserName=it
                viewModel.getfriends(it)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddMatchBinding.inflate(inflater, container, false)
        hideAllSets()
        return binding.root
    }

    private fun hideAllSets() {
        binding.linearLayoutAdd2.visibility = View.GONE
        binding.linearLayoutAdd3.visibility = View.GONE
        binding.linearLayoutAdd4.visibility = View.GONE
        binding.linearLayoutAdd5.visibility = View.GONE
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
                    Snackbar.make(
                        requireView(),
                        "Rakip listen boş. İlk öncelikle profilden rakip eklemelisin",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setActionTextColor(Color.WHITE)
                        .setAction("Tamam") {}.show()
                }
            } else {
                Log.e("AddMatchFragment", "Arkadaş listesi null.")
            }
        }

        binding.addNewSet1Button.setOnClickListener {
            binding.linearLayoutAdd2.visibility = View.VISIBLE
            binding.addNewSet1Button.visibility = View.GONE
            binding.linearLayoutAdd2Clear.visibility = View.VISIBLE
        }
        binding.addNewSet2Button.setOnClickListener {
            binding.linearLayoutAdd3.visibility = View.VISIBLE
            binding.addNewSet2Button.visibility = View.GONE
            binding.linearLayoutAdd3Clear.visibility = View.VISIBLE
        }
        binding.addNewSet3Button.setOnClickListener {
            binding.linearLayoutAdd4.visibility = View.VISIBLE
            binding.addNewSet3Button.visibility = View.GONE
            binding.linearLayoutAdd4Clear.visibility = View.VISIBLE
        }
        binding.addNewSet4Button.setOnClickListener {
            binding.linearLayoutAdd5.visibility = View.VISIBLE
            binding.addNewSet4Button.visibility = View.GONE
            binding.linearLayoutAdd5Clear.visibility = View.VISIBLE
        }


        binding.linearLayoutAdd2Clear.setOnClickListener {
            clearSet(2)
        }
        binding.linearLayoutAdd3Clear.setOnClickListener {
            clearSet(3)
        }
        binding.linearLayoutAdd4Clear.setOnClickListener {
            clearSet(4)
        }
        binding.linearLayoutAdd5Clear.setOnClickListener {
            clearSet(5)
        }


        binding.submitButton.setOnClickListener {
            submitMatch()
        }



    }
    private fun submitMatch() {
        val opponentUserName = binding.autoCompleteTextView.text.toString()

        if (opponentUserName.isEmpty() || opponentUserName == "Rakipler") {
            Snackbar.make(requireView(), "Lütfen bir rakip seçin", Snackbar.LENGTH_SHORT).show()
            return
        }

        val setScores = mutableListOf<Pair<Int, Int>>()

        val set1Home = binding.set1Home.text.toString().toIntOrNull() ?: 0
        val set1Away = binding.set1Away.text.toString().toIntOrNull() ?: 0
        setScores.add(Pair(set1Home, set1Away))

        if (binding.linearLayoutAdd2.visibility == View.VISIBLE) {
            val set2Home = binding.set2Home.text.toString().toIntOrNull() ?: 0
            val set2Away = binding.set2Away.text.toString().toIntOrNull() ?: 0
            setScores.add(Pair(set2Home, set2Away))
        }

        if (binding.linearLayoutAdd3.visibility == View.VISIBLE) {
            val set3Home = binding.set3Home.text.toString().toIntOrNull() ?: 0
            val set3Away = binding.set3Away.text.toString().toIntOrNull() ?: 0
            setScores.add(Pair(set3Home, set3Away))
        }

        if (binding.linearLayoutAdd4.visibility == View.VISIBLE) {
            val set4Home = binding.set4Home.text.toString().toIntOrNull() ?: 0
            val set4Away = binding.set4Away.text.toString().toIntOrNull() ?: 0
            setScores.add(Pair(set4Home, set4Away))
        }

        if (binding.linearLayoutAdd5.visibility == View.VISIBLE) {
            val set5Home = binding.set5Home.text.toString().toIntOrNull() ?: 0
            val set5Away = binding.set5Away.text.toString().toIntOrNull() ?: 0
            setScores.add(Pair(set5Home, set5Away))
        }

        viewModel.saveMatch(currentUserName, opponentUserName, setScores)
    }

    private fun clearSet(setNumber: Int) {
        when (setNumber) {
            2 -> {
                if (binding.linearLayoutAdd3.visibility == View.GONE) {
                    binding.linearLayoutAdd2.visibility = View.GONE
                    binding.addNewSet1Button.visibility = View.VISIBLE
                } else {
                    Snackbar.make(binding.root, "İlk önce 3. seti kaldırmalısınız", Snackbar.LENGTH_SHORT).show()
                }
            }
            3 -> {
                if (binding.linearLayoutAdd4.visibility == View.GONE) {
                    binding.linearLayoutAdd3.visibility = View.GONE
                    binding.addNewSet2Button.visibility = View.VISIBLE
                } else {
                    Snackbar.make(binding.root, "İlk önce 4. seti kaldırmalısınız", Snackbar.LENGTH_SHORT).show()
                }
            }
            4 -> {
                if (binding.linearLayoutAdd5.visibility == View.GONE) {
                    binding.linearLayoutAdd4.visibility = View.GONE
                    binding.addNewSet3Button.visibility = View.VISIBLE
                } else {
                    Snackbar.make(binding.root, "İlk önce 5. seti kaldırmalısınız", Snackbar.LENGTH_SHORT).show()
                }
            }
            5 -> {
                binding.linearLayoutAdd5.visibility = View.GONE
                binding.addNewSet4Button.visibility = View.VISIBLE
            }
        }
    }
}
