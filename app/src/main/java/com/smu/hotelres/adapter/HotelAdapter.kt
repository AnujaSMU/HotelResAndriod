package com.smu.hotelres.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smu.hotelres.databinding.ItemHotelBinding
import com.smu.hotelres.model.Hotel
import java.text.SimpleDateFormat
import java.util.Locale

class HotelAdapter(
    private val onHotelSelected: (Hotel) -> Unit
) : ListAdapter<Hotel, HotelAdapter.HotelViewHolder>(HotelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val binding = ItemHotelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HotelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HotelViewHolder(
        private val binding: ItemHotelBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onHotelSelected(getItem(position))
                }
            }
        }

        fun bind(hotel: Hotel) {
            binding.apply {
                hotelNameTextView.text = hotel.name
                priceTextView.text = "$${hotel.price}/night"
                ratingTextView.text = hotel.rating.toString()
                
                // Display availability and available until date if available
                val availabilityText = if (hotel.available) {
                    hotel.availableUntil?.let { date ->
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        "Available until ${dateFormat.format(date)}"
                    } ?: "Available"
                } else {
                    "Not Available"
                }
                availabilityTextView.text = availabilityText
                
                // Set card selection state
                root.isSelected = hotel.available
            }
        }
    }

    private class HotelDiffCallback : DiffUtil.ItemCallback<Hotel>() {
        override fun areItemsTheSame(oldItem: Hotel, newItem: Hotel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Hotel, newItem: Hotel): Boolean {
            return oldItem == newItem
        }
    }
} 