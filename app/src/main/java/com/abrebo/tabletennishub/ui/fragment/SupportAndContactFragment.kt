package com.abrebo.tabletennishub.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.data.model.Message
import com.abrebo.tabletennishub.databinding.FragmentSupportAndContactBinding
import com.abrebo.tabletennishub.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupportAndContactFragment : Fragment() {
    private lateinit var binding:FragmentSupportAndContactBinding
    private val viewModel:SettingsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentSupportAndContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSendMessage.setOnClickListener {
            if (binding.messageText.text!!.isNotEmpty()&&binding.messageTitle.text!!.isNotEmpty()){
                val message=Message(binding.messageTitle.text.toString(),binding.messageText.text.toString())
                viewModel.sendMessage(message)
                Toast.makeText(requireContext(), "Mesajınız başarıyla tarafımıza ulaştı", Toast.LENGTH_LONG).show()
                binding.messageText.setText("")
                binding.messageTitle.setText("")
            }else{
                Toast.makeText(requireContext(), "Başlık ve içerik boş olamaz", Toast.LENGTH_SHORT).show()
            }
        }

    }

}