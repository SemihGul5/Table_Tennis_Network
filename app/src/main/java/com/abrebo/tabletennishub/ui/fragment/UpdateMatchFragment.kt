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
import com.abrebo.tabletennishub.data.model.Match
import com.abrebo.tabletennishub.data.model.SetScore
import com.abrebo.tabletennishub.databinding.FragmentUpdateMatchBinding
import com.abrebo.tabletennishub.ui.viewmodel.AddMatchViewModel
import com.abrebo.tabletennishub.ui.viewmodel.UpdateMatchViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateMatchFragment : Fragment() {
    private lateinit var binding: FragmentUpdateMatchBinding
    private val viewModel: AddMatchViewModel by viewModels()
    private lateinit var auth: FirebaseAuth
    private var currentUserName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        viewModel.getUserNameByEmail(auth.currentUser?.email!!) { userName ->
            if (userName != null) {
                currentUserName = userName
                viewModel.getfriends(userName)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUpdateMatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val match = arguments?.getSerializable("match") as Match
        populateMatchData(match)

        binding.submitButton.setOnClickListener {
            updateMatch(match)
        }

        // Set ekleme butonları için dinleyiciler
        setupSetButtons()
    }

    private fun setupSetButtons() {
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


        binding.linearLayoutAdd2Clear.setOnClickListener { clearSet(2) }
        binding.linearLayoutAdd3Clear.setOnClickListener { clearSet(3) }
        binding.linearLayoutAdd4Clear.setOnClickListener { clearSet(4) }
        binding.linearLayoutAdd5Clear.setOnClickListener { clearSet(5) }
    }

    private fun populateMatchData(match: Match) {
        binding.autoCompleteTextView.setText(match.userAway, false)


        binding.set1Home.setText(match.setScores.getOrNull(0)?.userScore.toString())
        binding.set1Away.setText(match.setScores.getOrNull(0)?.opponentScore.toString())

        if (match.setScores.size > 1) {
            binding.linearLayoutAdd2.visibility = View.VISIBLE
            binding.set2Home.setText(match.setScores[1].userScore.toString())
            binding.set2Away.setText(match.setScores[1].opponentScore.toString())
            binding.addNewSet1Button.visibility = View.GONE
        } else {
            binding.linearLayoutAdd2.visibility = View.GONE
            binding.addNewSet1Button.visibility = View.VISIBLE
        }

        if (match.setScores.size > 2) {
            binding.linearLayoutAdd3.visibility = View.VISIBLE
            binding.set3Home.setText(match.setScores[2].userScore.toString())
            binding.set3Away.setText(match.setScores[2].opponentScore.toString())
            binding.addNewSet2Button.visibility = View.GONE
        } else {
            binding.linearLayoutAdd3.visibility = View.GONE
            binding.addNewSet2Button.visibility = View.VISIBLE
        }

        if (match.setScores.size > 3) {
            binding.linearLayoutAdd4.visibility = View.VISIBLE
            binding.set4Home.setText(match.setScores[3].userScore.toString())
            binding.set4Away.setText(match.setScores[3].opponentScore.toString())
            binding.addNewSet3Button.visibility = View.GONE
        } else {
            binding.linearLayoutAdd4.visibility = View.GONE
            binding.addNewSet3Button.visibility = View.VISIBLE
        }

        if (match.setScores.size > 4) {
            binding.linearLayoutAdd5.visibility = View.VISIBLE
            binding.set5Home.setText(match.setScores[4].userScore.toString())
            binding.set5Away.setText(match.setScores[4].opponentScore.toString())
            binding.addNewSet4Button.visibility = View.GONE
        } else {
            binding.linearLayoutAdd5.visibility = View.GONE
            binding.addNewSet4Button.visibility = View.VISIBLE
        }
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

    // Maçı güncelleme
    private fun updateMatch(match: Match) {
        val opponentUserName = binding.autoCompleteTextView.text.toString()

        if (opponentUserName.isEmpty() || opponentUserName == "Rakipler") {
            Snackbar.make(requireView(), "Lütfen bir rakip seçin", Snackbar.LENGTH_SHORT).show()
            return
        }


        val setScores = mutableListOf<SetScore>()


        val set1Home = binding.set1Home.text.toString().toIntOrNull() ?: 0
        val set1Away = binding.set1Away.text.toString().toIntOrNull() ?: 0
        setScores.add(SetScore(set1Home, set1Away))

        if (binding.linearLayoutAdd2.visibility == View.VISIBLE) {
            val set2Home = binding.set2Home.text.toString().toIntOrNull() ?: 0
            val set2Away = binding.set2Away.text.toString().toIntOrNull() ?: 0
            setScores.add(SetScore(set2Home, set2Away))
        }

        if (binding.linearLayoutAdd3.visibility == View.VISIBLE) {
            val set3Home = binding.set3Home.text.toString().toIntOrNull() ?: 0
            val set3Away = binding.set3Away.text.toString().toIntOrNull() ?: 0
            setScores.add(SetScore(set3Home, set3Away))
        }

        if (binding.linearLayoutAdd4.visibility == View.VISIBLE) {
            val set4Home = binding.set4Home.text.toString().toIntOrNull() ?: 0
            val set4Away = binding.set4Away.text.toString().toIntOrNull() ?: 0
            setScores.add(SetScore(set4Home, set4Away))
        }

        if (binding.linearLayoutAdd5.visibility == View.VISIBLE) {
            val set5Home = binding.set5Home.text.toString().toIntOrNull() ?: 0
            val set5Away = binding.set5Away.text.toString().toIntOrNull() ?: 0
            setScores.add(SetScore(set5Home, set5Away))
        }

        val updatedMatch = match.copy(
            userAway = opponentUserName,
            setScores = setScores
        )

        viewModel.updateMatch(updatedMatch, opponentUserName, setScores)
    }

}
