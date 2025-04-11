package com.smu.hotelres

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.smu.hotelres.databinding.FragmentReservationBinding
import com.smu.hotelres.model.Guest
import com.smu.hotelres.model.Hotel
import com.smu.hotelres.model.Reservation
import com.smu.hotelres.repository.HotelRepository
import com.smu.hotelres.viewmodel.ReservationViewModel
import com.smu.hotelres.viewmodel.ReservationViewModelFactory
import kotlinx.coroutines.launch

class ReservationFragment : Fragment() {
    private val TAG = "ReservationFragment"
    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!
    private lateinit var hotel: Hotel
    private var checkInDate: String = ""
    private var checkOutDate: String = ""
    private var guests: Int = 0
    private val guestInputs = mutableListOf<Pair<EditText, RadioGroup>>()
    private val repository = HotelRepository()

    private val viewModel: ReservationViewModel by viewModels {
        ReservationViewModelFactory(repository)
    }

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

        Log.d(TAG, "Got hotel: ${hotel.name}, Check-in: $checkInDate, Check-out: $checkOutDate, Guests: $guests")

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.apply {
            hotelNameTextView.text = hotel.name
            ratingTextView.text = hotel.rating.toString()
            datesTextView.text = "Check-in: $checkInDate\nCheck-out: $checkOutDate"
            
            // Format the available until date
            val availabilityInfo = viewModel.formatAvailabilityDate(hotel.available_until)
            if (availabilityInfo.isNotEmpty()) {
                // Add TextView for availability info if not in layout
                val availabilityTextView = TextView(context)
                availabilityTextView.text = availabilityInfo
                binding.root.addView(availabilityTextView)
            }
            
            // Calculate and display total price
            val priceInfo = viewModel.calculateTotalPrice(hotel, checkInDate, checkOutDate, guests)
            if (priceInfo.isNotEmpty()) {
                priceTextView.text = priceInfo
            } else {
                Toast.makeText(context, "Invalid booking duration", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return
            }
            
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
            submitReservation()
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.submitButton.isEnabled = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.reservationResult.observe(viewLifecycleOwner) { reservation ->
            reservation?.let {
                val action = ReservationFragmentDirections
                    .actionReservationFragmentToConfirmationFragment(it)
                findNavController().navigate(action)
            }
        }
    }

    private fun submitReservation() {
        val guestInputsList = guestInputs.map { (nameEditText, genderRadioGroup) ->
            val name = nameEditText.text.toString().trim()
            val gender = when (genderRadioGroup.checkedRadioButtonId) {
                R.id.maleRadioButton -> "Male"
                R.id.femaleRadioButton -> "Female"
                else -> "Other"
            }
            Pair(name, gender)
        }

        if (viewModel.validateGuestInputs(guestInputsList)) {
            viewModel.submitReservation(hotel, checkInDate, checkOutDate, guestInputsList)
        } else {
            Toast.makeText(context, "Please enter all guest information", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 