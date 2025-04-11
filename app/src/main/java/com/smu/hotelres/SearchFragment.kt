package com.smu.hotelres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.smu.hotelres.databinding.FragmentSearchBinding
import com.smu.hotelres.viewmodel.SearchViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)

    private val viewModel: SearchViewModel by viewModels()

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

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.searchButton.setOnClickListener {
            val checkInDate = getDateFromDatePicker(binding.checkInDatePicker)
            val checkOutDate = getDateFromDatePicker(binding.checkOutDatePicker)
            val guests = binding.guestsNumberPicker.getValue().toString()

            if (viewModel.validateSearchInputs(checkInDate, checkOutDate, guests)) {
                // Navigate to next screen with data
                val action = SearchFragmentDirections.actionSearchFragmentToHotelListFragment(
                    checkInDate = checkInDate,
                    checkOutDate = checkOutDate,
                    guests = guests.toInt()
                )
                findNavController().navigate(action)
            }
        }
    }

    private fun getDateFromDatePicker(datePicker: android.widget.DatePicker): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
        return dateFormat.format(calendar.time)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 