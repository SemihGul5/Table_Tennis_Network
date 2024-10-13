package com.abrebo.tabletennishub.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import androidx.navigation.Navigation
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.databinding.FragmentSettingsBinding
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
        settingsList.add("Uygulama Dilini Değiştir")
        settingsList.add("Uygulamayı Paylaş")
        settingsList.add("Destek ve İletişim")
        settingsList.add("Çıkış Yap")

        val adapter=ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,settingsList)
        binding.settingListView.adapter=adapter

        binding.settingListView.setOnItemClickListener { adapterView, view, i, l ->
            when(i){
                0->{Navigation.findNavController(view).navigate(R.id.action_settingsFragment_to_myInformationFragment)}
                1->{}
                2->{}
                3->{}
                4->{}
            }
        }
    }

}