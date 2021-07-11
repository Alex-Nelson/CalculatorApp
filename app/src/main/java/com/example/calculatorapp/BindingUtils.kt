package com.example.calculatorapp

import android.widget.TextView
import androidx.databinding.BindingAdapter
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Format the total by converting it to an Int, if applicable, to display in the CalculationTextView
 * */
@BindingAdapter("resultValue")
fun TextView.formatResult(total: Float){
    text = if(floor(total) == ceil(total)) total.toInt().toString() else total.toString()
}