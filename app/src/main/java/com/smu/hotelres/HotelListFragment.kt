package com.smu.hotelres

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.smu.hotelres.adapter.HotelAdapter
import com.smu.hotelres.databinding.FragmentHotelListBinding
import com.smu.hotelres.model.Hotel
import com.smu.hotelres.repository.HotelRepository
import kotlinx.coroutines.launch

class HotelListFragment : Fragment() {
    private val TAG = "HotelListFragment"
    private var _binding: FragmentHotelListBinding? = null
    private val binding get() = _binding!!
    private var selectedHotel: Hotel? = null
    private lateinit var adapter: HotelAdapter
    private val repository = HotelRepository()

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
            selectedHotel = hotel
            binding.nextButton.isEnabled = hotel.available
            Log.d(TAG, "Selected hotel: ${hotel.name}, ID: ${hotel.id}, Available: ${hotel.available}")
        }

        binding.hotelsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = this@HotelListFragment.adapter
        }

        // Initially disable next button
        binding.nextButton.isEnabled = false

        // Setup next button
        binding.nextButton.setOnClickListener {
            selectedHotel?.let { hotel ->
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

        // Show loading state
        showLoading(true)

        // Fetch hotels from the API using our repository
        Log.d(TAG, "Fetching hotels for dates: $checkInDate to $checkOutDate")
        fetchHotels(checkInDate, checkOutDate)
    }

    private fun fetchHotels(checkInDate: String, checkOutDate: String) {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Starting API call to fetch hotels")
                val hotels = repository.getAvailableHotels(checkInDate, checkOutDate)
                Log.d(TAG, "Received ${hotels.size} hotels from repository")
                
                adapter.submitList(hotels)
                showLoading(false)
                
                if (hotels.isEmpty()) {
                    Log.d(TAG, "No hotels available for selected dates")
                    showEmptyState(true)
                } else {
                    showEmptyState(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching hotels: ${e.message}", e)
                showLoading(false)
                showError("Failed to load hotels: ${e.message}")
            }
        }
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