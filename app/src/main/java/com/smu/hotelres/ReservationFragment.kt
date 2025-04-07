package com.smu.hotelres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.smu.hotelres.databinding.FragmentReservationBinding
import com.smu.hotelres.model.Hotel

class ReservationFragment : Fragment() {
    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!
    private lateinit var hotel: Hotel
    private var checkInDate: String = ""
    private var checkOutDate: String = ""
    private var guests: Int = 0
    private val guestInputs = mutableListOf<Pair<EditText, RadioGroup>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get arguments from previous screen
        arguments?.let { bundle ->
            hotel = bundle.getSerializable("hotel") as Hotel
            checkInDate = bundle.getString("checkInDate") ?: ""
            checkOutDate = bundle.getString("checkOutDate") ?: ""
            guests = bundle.getInt("guests", 0)
        }

        // Display hotel information
        binding.apply {
            hotelNameTextView.text = hotel.name
            datesTextView.text = "Check-in: $checkInDate\nCheck-out: $checkOutDate"
            priceTextView.text = "Total Price: $${hotel.price * guests}"
            guestsCountTextView.text = "Number of Guests: $guests"
        }

        // Create input fields for each guest
        for (i in 1..guests) {
            val guestLayout = layoutInflater.inflate(
                R.layout.guest_input_layout,
                binding.guestsContainer,
                false
            ) as ViewGroup

            val nameEditText = guestLayout.findViewById<EditText>(R.id.guestNameEditText)
            val genderRadioGroup = guestLayout.findViewById<RadioGroup>(R.id.genderRadioGroup)
            
            guestLayout.findViewById<TextView>(R.id.guestNumberTextView).text = "Guest $i"
            binding.guestsContainer.addView(guestLayout)
            guestInputs.add(Pair(nameEditText, genderRadioGroup))
        }

        binding.submitButton.setOnClickListener {
            if (validateInputs()) {
                submitReservation()
            }
        }
    }

    private fun validateInputs(): Boolean {
        for ((nameEditText, genderRadioGroup) in guestInputs) {
            if (nameEditText.text.toString().trim().isEmpty()) {
                Toast.makeText(context, "Please enter all guest names", Toast.LENGTH_SHORT).show()
                return false
            }
            if (genderRadioGroup.checkedRadioButtonId == -1) {
                Toast.makeText(context, "Please select gender for all guests", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun submitReservation() {
        val guestDetails = guestInputs.map { (nameEditText, genderRadioGroup) ->
            val name = nameEditText.text.toString()
            val gender = when (genderRadioGroup.checkedRadioButtonId) {
                R.id.maleRadioButton -> "Male"
                R.id.femaleRadioButton -> "Female"
                else -> "Other"
            }
            Pair(name, gender)
        }

        // TODO: Make API call to submit reservation
        // For now, just show a success message
        Toast.makeText(context, "Reservation submitted successfully!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 