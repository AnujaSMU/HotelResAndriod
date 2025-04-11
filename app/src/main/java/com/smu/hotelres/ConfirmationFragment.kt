package com.smu.hotelres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smu.hotelres.databinding.FragmentConfirmationBinding
import com.smu.hotelres.model.Reservation
import java.text.SimpleDateFormat
import java.util.Locale

class ConfirmationFragment : Fragment() {
    private var _binding: FragmentConfirmationBinding? = null
    private val binding get() = _binding!!
    private lateinit var reservation: Reservation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get reservation from arguments
        arguments?.let { bundle ->
            reservation = bundle.getSerializable("reservation") as Reservation
        }

        // Format dates
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val checkinFormatted = dateFormat.format(reservation.checkin)
        val checkoutFormatted = dateFormat.format(reservation.checkout)

        // Display reservation details
        binding.apply {
            confirmationNumberTextView.text = "Confirmation: ${reservation.confirmation_number}"
            hotelNameTextView.text = reservation.hotel_name
            datesTextView.text = "Check-in: $checkinFormatted\nCheck-out: $checkoutFormatted"
            guestsCountTextView.text = "Number of Guests: ${reservation.guests_list.size}"

            // Add all guests
            for (guest in reservation.guests_list) {
                val guestView = layoutInflater.inflate(R.layout.item_guest, binding.guestsContainer, false)
                guestView.findViewById<TextView>(R.id.guestNameTextView).text = guest.guest_name
                guestView.findViewById<TextView>(R.id.guestGenderTextView).text = guest.gender
                binding.guestsContainer.addView(guestView)
            }

            // Done button to go back to home
            doneButton.setOnClickListener {
                // Go back to search screen (clear the back stack)
                val action = ConfirmationFragmentDirections.actionConfirmationFragmentToSearchFragment()
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 