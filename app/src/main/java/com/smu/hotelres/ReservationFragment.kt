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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.smu.hotelres.databinding.FragmentReservationBinding
import com.smu.hotelres.model.Guest
import com.smu.hotelres.model.Hotel
import com.smu.hotelres.repository.HotelRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

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

        // Display hotel information
        binding.apply {
            hotelNameTextView.text = hotel.name
            ratingTextView.text = hotel.rating.toString()
            datesTextView.text = "Check-in: $checkInDate\nCheck-out: $checkOutDate"
            
            // Format the available until date if available
            val availabilityInfo = hotel.available_until?.let { dateString ->
                try {
                    // Parse the date from yyyy-MM-dd format
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                    val date = inputFormat.parse(dateString)
                    date?.let {
                        "Available until: ${outputFormat.format(it)}"
                    } ?: ""
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing available_until date: ${e.message}")
                    ""
                }
            } ?: ""
            
            // Calculate total price (price per night * number of guests * number of days)
            val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
            val checkIn = dateFormat.parse(checkInDate)
            val checkOut = dateFormat.parse(checkOutDate)
            val diffInMillis = checkOut!!.time - checkIn!!.time //Cannot be Null
            val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
            
            // Validate stay duration is not more than 100 days (as a fallback)
            if (diffInDays > 100) {
                Toast.makeText(context, "Bookings cannot exceed 100 days", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return@apply
            }
            
            val totalPrice = hotel.price * guests * diffInDays
            priceTextView.text = "Price: $${hotel.price}/night\nStay Duration: $diffInDays night(s)\nTotal Price: $${totalPrice}"
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
        // Show loading indicator
        binding.progressBar.visibility = View.VISIBLE
        binding.submitButton.isEnabled = false

        // Collect guest information
        val guestList = guestInputs.map { (nameEditText, genderRadioGroup) ->
            val name = nameEditText.text.toString()
            val gender = when (genderRadioGroup.checkedRadioButtonId) {
                R.id.maleRadioButton -> "Male"
                R.id.femaleRadioButton -> "Female"
                else -> "Other"
            }
            Guest(guest_name = name, gender = gender)
        }

        Log.d(TAG, "Submitting reservation with ${guestList.size} guests")

        // Make API call to submit reservation
        lifecycleScope.launch {
            try {
                val reservation = repository.createReservation(
                    hotel = hotel,
                    checkinDate = checkInDate,
                    checkoutDate = checkOutDate,
                    guests = guestList
                )

                // Hide loading
                binding.progressBar.visibility = View.GONE

                if (reservation != null) {
                    // Verify all fields are present in the reservation object
                    if (reservation.confirmation_number.isNotEmpty() &&
                        reservation.hotel_name.isNotEmpty() && reservation.guests_list.isNotEmpty()
                    ) {
                        
                        Log.d(TAG, "Reservation successful with confirmation: ${reservation.confirmation_number}")
                        
                        // Navigate to confirmation screen
                        val action = ReservationFragmentDirections
                            .actionReservationFragmentToConfirmationFragment(reservation)
                        findNavController().navigate(action)
                    } else {
                        // If any required field is missing
                        Log.e(TAG, "Reservation object is incomplete: $reservation")
                        binding.submitButton.isEnabled = true
                        showError("Reservation data is incomplete. Please try again.")
                    }
                } else {
                    binding.submitButton.isEnabled = true
                    showError("Failed to create reservation. Please try again.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating reservation: ${e.message}", e)
                binding.progressBar.visibility = View.GONE
                binding.submitButton.isEnabled = true
                showError("Error: ${e.message}")
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 