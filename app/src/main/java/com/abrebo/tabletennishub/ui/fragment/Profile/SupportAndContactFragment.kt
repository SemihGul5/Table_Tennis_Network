package com.abrebo.tabletennishub.ui.fragment.Profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.abrebo.tabletennishub.R
import com.abrebo.tabletennishub.data.model.Message
import com.abrebo.tabletennishub.databinding.FragmentSupportAndContactBinding
import com.abrebo.tabletennishub.ui.viewmodel.Profile.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SupportAndContactFragment : Fragment() {
    private lateinit var binding:FragmentSupportAndContactBinding
    private val viewModel: SettingsViewModel by viewModels()
    private var cooldownTimer: CountDownTimer? = null
    private var messageInFlight = false
    private val cooldownMillis = 24 * 60 * 60 * 1000L
    private val preferences by lazy { requireContext().getSharedPreferences("support_contact", 0) }
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
        updateMessageCooldown()
        binding.buttonSendMessage.setOnClickListener {
            if (remainingCooldown() > 0L || messageInFlight) {
                updateMessageCooldown()
                return@setOnClickListener
            }
            if (binding.messageText.text!!.isNotEmpty()&&binding.messageTitle.text!!.isNotEmpty()){
                val message=Message(binding.messageTitle.text.toString(),binding.messageText.text.toString())
                messageInFlight = true
                binding.buttonSendMessage.isEnabled = false
                viewModel.sendMessage(message) { success ->
                    if (!isAdded) return@sendMessage
                    messageInFlight = false
                    if (success) {
                        preferences.edit()
                            .putLong("cooldown_until", System.currentTimeMillis() + cooldownMillis)
                            .apply()
                        Toast.makeText(requireContext(), R.string.Yourmessagehasbeensuccessfullyreceived, Toast.LENGTH_LONG).show()
                        binding.messageText.setText("")
                        binding.messageTitle.setText("")
                    } else {
                        Toast.makeText(requireContext(), R.string.support_message_send_failed, Toast.LENGTH_SHORT).show()
                    }
                    updateMessageCooldown()
                }
            }else{
                Toast.makeText(requireContext(),
                    requireContext().getString(R.string.Titleandcontentcannotbeempty),
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun remainingCooldown(): Long =
        (preferences.getLong("cooldown_until", 0L) - System.currentTimeMillis()).coerceAtLeast(0L)

    private fun updateMessageCooldown() {
        cooldownTimer?.cancel()
        val remaining = remainingCooldown()
        binding.buttonSendMessage.isEnabled = remaining <= 0L && !messageInFlight
        if (remaining <= 0L) {
            binding.supportCooldownGroup.visibility = View.GONE
            return
        }
        binding.supportCooldownGroup.visibility = View.VISIBLE
        binding.supportCooldownProgress.max = cooldownMillis.toInt()
        cooldownTimer = object : CountDownTimer(remaining, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished + 999L) / 1000L
                binding.supportCooldownProgress.progress = millisUntilFinished.toInt()
                binding.supportCooldownText.text = getString(
                    R.string.retry_in,
                    seconds / 3600,
                    (seconds % 3600) / 60,
                    seconds % 60
                )
            }

            override fun onFinish() {
                binding.supportCooldownGroup.visibility = View.GONE
                binding.buttonSendMessage.isEnabled = !messageInFlight
            }
        }.start()
    }

    override fun onDestroyView() {
        cooldownTimer?.cancel()
        cooldownTimer = null
        super.onDestroyView()
    }

}
