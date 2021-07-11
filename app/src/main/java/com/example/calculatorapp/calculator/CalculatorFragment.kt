package com.example.calculatorapp.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.calculatorapp.R
import com.example.calculatorapp.databinding.FragmentCalculatorBinding


/**
 * A fragment that handles the calculator UI when the device is in portrait layout
 */
class CalculatorFragment : Fragment() {

    // Binding object instance with access to the views in the fragment_calculator_portrait.xml
    private lateinit var binding: FragmentCalculatorBinding

    // Create a ViewModel when the fragment is created the first time
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        // Inflate the layout XML file and return a binding object instance
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_calculator, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        // Specify the fragment view as the lifecycle owner of the binding
        binding.lifecycleOwner = viewLifecycleOwner

        // Execute any bindings
        binding.executePendingBindings()
    }
}