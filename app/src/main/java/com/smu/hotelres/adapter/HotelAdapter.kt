package com.smu.hotelres.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smu.hotelres.R
import com.smu.hotelres.databinding.ItemHotelBinding
import com.smu.hotelres.model.Hotel
import java.text.SimpleDateFormat
import java.util.Locale

class HotelAdapter(
    private val onHotelSelected: (Hotel) -> Unit
) : ListAdapter<Hotel, HotelAdapter.HotelViewHolder>(HotelDiffCallback()) {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val binding = ItemHotelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HotelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition)
    }

    inner class HotelViewHolder(
        private val binding: ItemHotelBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val hotel = getItem(position)
                    
                    // Update selection state
                    val previousPosition = selectedPosition
                    selectedPosition = position
                    
                    // Notify adapter of changes to update UI
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    
                    // Call the selection callback
                    onHotelSelected(hotel)
                }
            }
        }

        fun bind(hotel: Hotel, isSelected: Boolean) {
            binding.apply {
                // Set hotel name and rating
                hotelNameTextView.text = hotel.name
                ratingTextView.text = hotel.rating.toString()
                
                // Format price
                priceTextView.text = "$${hotel.price}/night"
                
                // Set hotel image (placeholder for now)
                // In a real app, you would load an image from a URL using Glide or similar
                
                // Display availability and available until date if available
                val availabilityText = if (hotel.available) {
                    Log.d("HotelAdapter", "Available until date: ${hotel.available_until}")
                    hotel.available_until?.let { dateString ->
                        try {
                            // Parse the date from yyyy-MM-dd format
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            val date = inputFormat.parse(dateString)
                            date?.let {
                                "Available until ${outputFormat.format(it)}"
                            } ?: "Available"
                        } catch (e: Exception) {
                            Log.e("HotelAdapter", "Error parsing date: ${e.message}")
                            "Available"
                        }
                    } ?: "Available"
                } else {
                    "Not Available"
                }
                availabilityTextView.text = availabilityText
                
                // Set availability chip
                availabilityChip.text = if (hotel.available) "Available" else "Unavailable"
                val chipColor = if (hotel.available) {
                    ContextCompat.getColor(root.context, R.color.accent)
                } else {
                    ContextCompat.getColor(root.context, R.color.text_secondary)
                }
                availabilityChip.chipBackgroundColor = android.content.res.ColorStateList.valueOf(chipColor)
                
                // Set card selection state
                root.isChecked = isSelected
                
                if (isSelected) {
                    root.strokeColor = ContextCompat.getColor(root.context, R.color.accent)
                    root.strokeWidth = root.resources.getDimensionPixelSize(R.dimen.card_stroke_width)
                    root.cardElevation = root.resources.getDimension(R.dimen.card_selected_elevation)
                } else {
                    root.strokeColor = ContextCompat.getColor(root.context, android.R.color.transparent)
                    root.cardElevation = root.resources.getDimension(R.dimen.card_default_elevation)
                }
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