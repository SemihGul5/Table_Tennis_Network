package com.abrebo.tabletennishub.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.MainActivity
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding:FragmentSettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsList=ArrayList<String>()
        settingsList.add("Bilgilerim")
        settingsList.add("Uygulamayı Paylaş")
        settingsList.add("Destek ve İletişim")
        settingsList.add("Çıkış Yap")

        val adapter=ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,settingsList)
        binding.settingListView.adapter=adapter

        binding.settingListView.setOnItemClickListener { adapterView, view, i, l ->
            when(i){
                0->{Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_myInformationFragment)}
                1->{shareApp()}
                2->{Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_supportAndContactFragment)}
                3->{
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                }

            }
        }
    }
    fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        val appPackageName = context?.packageName
        val shareMessage = "Bu harika uygulamayı denemenizi tavsiye ederim!"

        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        startActivity(Intent.createChooser(shareIntent, "Uygulamayı Paylaş"))
    }



}