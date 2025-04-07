package com.smu.hotelres

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.smu.hotelres.adapter.HotelAdapter
import com.smu.hotelres.databinding.FragmentHotelListBinding
import com.smu.hotelres.model.Hotel

class HotelListFragment : Fragment() {
    private var _binding: FragmentHotelListBinding? = null
    private val binding get() = _binding!!
    private var selectedHotel: Hotel? = null

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

        // Update search summary
        binding.searchSummaryTextView.text = "Check-in: $checkInDate\nCheck-out: $checkOutDate\nGuests: $guests"

        // Setup RecyclerView
        val adapter = HotelAdapter { hotel ->
            selectedHotel = hotel
            binding.nextButton.isEnabled = hotel.availability
        }

        binding.hotelsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }

        // Initially disable next button
        binding.nextButton.isEnabled = false

        // Setup next button
        binding.nextButton.setOnClickListener {
            selectedHotel?.let { hotel ->
                            Toast.makeText(context, "Selected: ${hotel.name}", Toast.LENGTH_SHORT).show()
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

        // TODO: Make API call to get hotels
        // For now, using sample data
        val sampleHotels = listOf(
            Hotel("1", "Grand Hotel", 199.99, true),
            Hotel("2", "Seaside Resort", 249.99, true),
            Hotel("3", "Mountain View Inn", 179.99, false),
            Hotel("4", "City Center Hotel", 299.99, true)
        )
        adapter.submitList(sampleHotels)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 