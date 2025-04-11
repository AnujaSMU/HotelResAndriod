package com.smu.hotelres

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.smu.hotelres.adapter.HotelAdapter
import com.smu.hotelres.databinding.FragmentHotelListBinding
import com.smu.hotelres.model.Hotel
import com.smu.hotelres.repository.HotelRepository
import com.smu.hotelres.viewmodel.HotelListViewModel
import com.smu.hotelres.viewmodel.HotelListViewModelFactory

class HotelListFragment : Fragment() {
    private val TAG = "HotelListFragment"
    private var _binding: FragmentHotelListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HotelAdapter
    private val repository = HotelRepository()

    private val viewModel: HotelListViewModel by viewModels<HotelListViewModel> {
        HotelListViewModelFactory(repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHotelListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get arguments from previous screen
        val checkInDate = arguments?.getString("checkInDate") ?: ""
        val checkOutDate = arguments?.getString("checkOutDate") ?: ""
        val guests = arguments?.getInt("guests") ?: 0

        Log.d(TAG, "Search params - Check-in: $checkInDate, Check-out: $checkOutDate, Guests: $guests")

        // Update search summary
        binding.searchSummaryTextView.text = "Check-in: $checkInDate\nCheck-out: $checkOutDate\nGuests: $guests"

        // Setup RecyclerView
        adapter = HotelAdapter { hotel ->
            viewModel.selectHotel(hotel)
            if (hotel.available) {
                showContinueButton(true)
            } else {
                Toast.makeText(context, "Sorry, this hotel is not available for booking", Toast.LENGTH_SHORT).show()
                showContinueButton(false)
            }
            Log.d(TAG, "Selected hotel: ${hotel.name}, ID: ${hotel.id}, Available: ${hotel.available}")
        }

        binding.hotelsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@HotelListFragment.adapter
        }

        // Initially hide next button
        showContinueButton(false)

        // Setup next button
        binding.nextButton.setOnClickListener {
            viewModel.selectedHotel.value?.let { hotel ->
                Log.d(TAG, "Proceeding with hotel: ${hotel.name}, ID: ${hotel.id}")
                val action = HotelListFragmentDirections
                    .actionHotelListFragmentToReservationFragment(
                        hotel = hotel,
                        checkInDate = checkInDate,
                        checkOutDate = checkOutDate,
                        guests = guests
                    )
                findNavController().navigate(action)
            }
        }

        // Setup observers
        setupObservers()

        // Fetch hotels
        viewModel.fetchHotels(checkInDate, checkOutDate)
    }

    private fun setupObservers() {
        viewModel.hotels.observe(viewLifecycleOwner) { hotels ->
            adapter.submitList(hotels)
            showEmptyState(hotels.isEmpty())
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { message ->
                showError(message)
            }
        }
    }

    private fun showContinueButton(show: Boolean) {
        binding.nextButton.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.hotelsRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        Log.d(TAG, "Loading state: $isLoading")
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.emptyStateTextView.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        Log.e(TAG, "Error shown to user: $message")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 