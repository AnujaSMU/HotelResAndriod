package com.smu.hotelres

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.smu.hotelres.databinding.FragmentSearchBinding
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: SharedPreferences
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity().getSharedPreferences("HotelPrefs", Context.MODE_PRIVATE)
        setupSearchButton()
    }

    private fun getDateFromDatePicker(datePicker: DatePicker): String {
        val calendar = Calendar.getInstance()
        calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
        return dateFormat.format(calendar.time)
    }

    private fun setupSearchButton() {
        binding.searchButton.setOnClickListener {
            val checkInDate = getDateFromDatePicker(binding.checkInDatePicker)
            val checkOutDate = getDateFromDatePicker(binding.checkOutDatePicker)
            val guests = binding.guestsEditText.text.toString()

            if (guests.isEmpty()) {
                Toast.makeText(context, "Please enter number of guests", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate dates
            val checkInCalendar = Calendar.getInstance().apply {
                time = dateFormat.parse(checkInDate)!!
            }
            val checkOutCalendar = Calendar.getInstance().apply {
                time = dateFormat.parse(checkOutDate)!!
            }

            if (checkOutCalendar.before(checkInCalendar)) {
                Toast.makeText(context, "Check-out date must be after check-in date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Store data in SharedPreferences
            sharedPreferences.edit().apply {
                putString("guests", guests)
                apply()
            }

            // Navigate to next screen with data
            val action = SearchFragmentDirections.actionSearchFragmentToHotelListFragment(
                checkInDate = checkInDate,
                checkOutDate = checkOutDate,
                guests = guests.toInt()
            )
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 