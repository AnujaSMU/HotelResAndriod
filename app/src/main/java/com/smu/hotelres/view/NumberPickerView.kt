package com.smu.hotelres.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.smu.hotelres.R

class NumberPickerView @JvmOverloads constructor(
    context: Context, 
    attrs: AttributeSet? = null, 
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    
    private val labelTextView: TextView
    private val valueTextView: TextView
    private val minusButton: MaterialButton
    private val plusButton: MaterialButton
    
    private var currentValue = 1
    private var minValue = 1
    private var maxValue = 10
    private var labelText = "Number of Guests"
    
    var onValueChangedListener: ((Int) -> Unit)? = null
    
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.number_picker_layout, this, true)
        
        labelTextView = view.findViewById(R.id.labelTextView)
        valueTextView = view.findViewById(R.id.valueTextView)
        minusButton = view.findViewById(R.id.minusButton)
        plusButton = view.findViewById(R.id.plusButton)
        
        setupAttributes(attrs)
        setupButtons()
        updateUI()
    }
    
    private fun setupAttributes(attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerView)
            
            try {
                labelText = typedArray.getString(R.styleable.NumberPickerView_labelText) ?: labelText
                minValue = typedArray.getInt(R.styleable.NumberPickerView_minValue, minValue)
                maxValue = typedArray.getInt(R.styleable.NumberPickerView_maxValue, maxValue)
                currentValue = typedArray.getInt(R.styleable.NumberPickerView_value, currentValue)
                
                // Make sure current value is within range
                if (currentValue < minValue) currentValue = minValue
                if (currentValue > maxValue) currentValue = maxValue
            } finally {
                typedArray.recycle()
            }
        }
    }
    
    private fun setupButtons() {
        minusButton.setOnClickListener {
            if (currentValue > minValue) {
                currentValue--
                updateUI()
                onValueChangedListener?.invoke(currentValue)
            }
        }
        
        plusButton.setOnClickListener {
            if (currentValue < maxValue) {
                currentValue++
                updateUI()
                onValueChangedListener?.invoke(currentValue)
            }
        }
    }
    
    private fun updateUI() {
        labelTextView.text = labelText
        valueTextView.text = currentValue.toString()
        
        // Disable minus button when at min value
        minusButton.isEnabled = currentValue > minValue
        
        // Disable plus button when at max value
        plusButton.isEnabled = currentValue < maxValue
    }
    
    fun setValue(value: Int) {
        if (value in minValue..maxValue && value != currentValue) {
            currentValue = value
            updateUI()
        }
    }
    
    fun getValue(): Int = currentValue
    
    fun setMinValue(min: Int) {
        if (min <= maxValue) {
            minValue = min
            if (currentValue < minValue) {
                currentValue = minValue
            }
            updateUI()
        }
    }
    
    fun setMaxValue(max: Int) {
        if (max >= minValue) {
            maxValue = max
            if (currentValue > maxValue) {
                currentValue = maxValue
            }
            updateUI()
        }
    }
    
    fun setLabel(label: String) {
        labelText = label
        updateUI()
    }
} 