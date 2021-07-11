package com.example.calculatorapp.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.math.*

// Math constant values
const val E_VALUE = E.toFloat()
const val PI_VALUE = PI.toFloat()

class CalculatorViewModel : ViewModel(){

    // Tells the calculator if it's in Decimal or Radian
    private var _toggleState = MutableLiveData<String>()
    val toggleState: LiveData<String>
        get() = _toggleState

    // Variable to help if a number has a decimal already
    private var _decimalCheck = MutableLiveData<Boolean>()
    private val decimalCheck: LiveData<Boolean>
        get() = _decimalCheck

    // Stores the current calculation the user entered
    private var _calculationString = MutableLiveData<String>()
    val calculationString: LiveData<String>
        get() = _calculationString

    // Stores the current result based on the current calculation
    private var _result = MutableLiveData<Float?>()
    val result: LiveData<Float?>
        get() = _result

    // Flag for invalid factorial
    private var _invalidFactFlag = MutableLiveData<Boolean>()
    val invalidFactFlag: LiveData<Boolean>
        get() = _invalidFactFlag

    // String to hold the last operand pressed
    private var operandList = mutableListOf<String>()

    // Counts the number of left parentheses
    private var numLeftParentheses: Int

    // Counts the number of right parentheses
    private var numRightParentheses: Int

    // A stack to hold calculation segments when parentheses are included
    private var calcStack = mutableListOf<String>()

    //TODO: Add variable to store the date the calculation was made for the database

    // Initialize to default values
    init {
        _toggleState.value = "DEG"
        _decimalCheck.value = false
        _calculationString.value = ""
        _result.value = 0f
        numLeftParentheses= 0
        numRightParentheses = 0
    }

    /**
     * Add a decimal to the latest number if possible
     * */
    fun onDecimal() {
        if(decimalCheck.value == false){
            _decimalCheck.value = true
            _calculationString.value = _calculationString.value?.plus('.')
        }
    }

    /**
     * Completes the calculation
     * */
    fun onEquals(){

        // TODO: Save the calculation and result values into the Room database

        // Sets the calculation string to empty
        _calculationString.value = ""
    }

    /**
     * Add the operation to the calculation string and compute the current result
     * */
    fun onOperand(operand: Char){
        _calculationString.value = calculationString.value?.plus(operand)

        //Store the operand value
        operandList.add(operand.toString())

        // Reset the decimal flag for the next number
        // TODO: May change the variable type to a list/stack
        _decimalCheck.value = false

    }

    /**
     * Adds a complex operand to the list
     *
     * This function is passed the operand as a string to account for the logarithmic and trig
     * function names
     * */
    fun onComplexOperand(operand: String){
        _calculationString.value = calculationString.value.plus(operand)

        // Store the operand value
        operandList.add(operand)

        // Reset the decimal flag for the next number
        _decimalCheck.value = false

        // Add to the left parentheses counter if applicable
        if(operand.contains('(')) numLeftParentheses++
    }

    /**
     * Compute the percentage of the number
     * */
    fun onPercent(){
        // Add the percent symbol to the string
        _calculationString.value = calculationString.value.plus('%')

        // Store the operand
        operandList.add("%")

        // Reset the decimal flag for the next number
        _decimalCheck.value = false

        // Can only apply this operation if result is not null
        if(result.value != null){
            _result.value = result.value!!.div(100)
        }
        // TODO: Flag that the user's expression is invalid
    }

    /**
     * Update the result based on the current operand
     * */
    private fun performOperation(){

        // Get the index of the current operand
        val currentOperand = operandList[operandList.size - 1]
        val idx = calculationString.value!!.lastIndexOf(currentOperand)

        // Get the number via substring
        val num = calculationString.value!!.substring(
            IntRange(idx + 1, calculationString.value!!.length - 1)).toFloat()

        // Update the result by performing the operation
        when (currentOperand) {
            "+" -> {
                _result.value = result.value!!.plus(num)
            }
            "-" -> {
                _result.value = result.value!!.minus(num)
            }
            "x" -> {
                _result.value = result.value!!.times(num)
            }
            else-> {
                _result.value = result.value!!.div(num)
            }

        }
    }

    /**
     * Perform a complex operation
     * */
    private fun performComplexOperation(){
        // Get the latest operand
        val currentOperand = operandList.last()
        val idx = calculationString.value!!.lastIndexOf(currentOperand)

        // Update the result
        when (currentOperand) {
            "^" -> {
                // Get the number via substring
                val num = calculationString.value!!.substring(
                    IntRange(idx + 1, calculationString.value!!.length - 1)).toFloat()

                // Apply the pow function to raise the value to num
                _result.value = result.value!!.pow(num)
            }

        }
    }

    /**
     * Perform trigonometric functions
     *
     * Based on the current state, the number is either in degrees (DEG) or radians (RAD)
     * */
    private fun performTrigFunction(trig: String){

        // Pop the expression off the stack
        val num = calcStack.removeLast().toFloat()

        when(trig){
            "sin" -> {
                // Perform the sine function
                _result.value = if (toggleState.value == "DEG") convertRadToDeg(sin(num))
                else sin(num)
            }
            "cos" -> {
                // Perform the cosine function
                _result.value = if (toggleState.value == "DEG") convertRadToDeg(cos(num))
                else cos(num)
            }
            "tan" -> {
                // Perform the tangent function
                _result.value = if (toggleState.value == "DEG") convertRadToDeg(tan(num))
                else tan(num)
            }
        }
    }

    /**
     *
     * */

    /**
     * Updates the calculation string based the number entered
     * */
    fun onNumber(num: Int) {

        when {
            calculationString.value == "" -> {
                //If the calculation string is empty, set the string's value and update the result
                _calculationString.value = num.toString()
                _result.value = calculationString.value!!.toFloat()
            }
            operandList.isEmpty() -> {
                // If no operands have been pressed, add the number to the string and update result
                _calculationString.value = calculationString.value?.plus(num.toString())
                _result.value = calculationString.value!!.toFloat()
            }
            else -> {
                // Check if the calculation string ends in a digit
                if (calculationString.value!!.last().isDigit()){
                    // Undo the result's value before updating
                    reverseOperation(operandList[operandList.lastIndex])
                }

                // Add the digit to the string and perform the operation
                _calculationString.value = calculationString.value?.plus(num.toString())
                performOperation()
            }
        }

    }

    /**
     * Toggle between Degrees and Radians
     * */
    fun onToggle(){

        // TODO: Change value of result if a trig function has been used
        if(toggleState.value == "DEG"){
           _toggleState.value = "RAD"

        }else {
            _toggleState.value = "DEG"
        }
    }

    /**
     * Deletes a character from the calculation string
     * */
    fun onDelete(){

        // Only delete if the calculation string is not empty
        if(calculationString.value!!.isNotEmpty()){
            // Get the last character in the string
            val char = calculationString.value!!.last()

            when {
                char.isDigit() -> {
                    // If the character was a number, update the result
                    numberDeleted(char)
                }
                char == '.' -> {
                    // If the character was a decimal, reset the flag
                    decimalDeleted()
                }
                else -> {
                    // Else, reverse the operation that was done
                    operandDeleted()
                }
            }
        }

    }

    /**
     * When the PI button is clicked, it has PI by itself or multiplies it with the number next to
     * it
     * */
    fun onPI(){

    }

    /**
     * When 'e' (Euler's number) is clicked, it has 'e' by itself or multiplies it with the number
     * next to it
     * */
    fun onE(){
        if(calculationString.value!!.isEmpty()){
            // If the string is empty, just add the value of 'e' to the total
            _result.value = result.value!!.plus(E_VALUE)
        }
    }

    /**
     * Adds a left or right parentheses
     * */
    fun onParentheses(char: Char){
        // TODO: Push the previous expression segment onto the stack
        _calculationString.value = calculationString.value.plus(char)
        if(char == '(') numLeftParentheses++
        else numRightParentheses++
    }

    /**
     * Update the result since a digit in the last number was deleted
     *
     * Cases:
     * 1. Calculation string is empty
     * 2. No operands have been pressed
     * 3.
     * */
    private fun numberDeleted(digit: Char) {

        // Delete the last character (the digit that was deleted
        _calculationString.value = calculationString.value!!.dropLast(1)

        when {
            calculationString.value?.isEmpty() == true -> {
                // If calculation string is empty, set result to default value
                _result.value = 0f
            }
            operandList.isEmpty() -> {
                // If there is no operand, just update the result
                _result.value = calculationString.value?.toFloat()
            }
            else -> {
                // Temporarily add the digit back
                _calculationString.value = calculationString.value.plus(digit)

                // Perform the inverse operation
                reverseOperation(operandList.last())

                // Delete the digit
                _calculationString.value = calculationString.value!!.dropLast(1)

                // If there is a digit after the operand, update the result
                if(calculationString.value!!.last().isDigit()){
                    performOperation()
                }
            }
        }
    }

    /**
     * Reset the decimal flag if it was deleted
     * */
    private fun decimalDeleted(){
        // Reset the decimal flag
        _decimalCheck.value = false

        _calculationString.value = calculationString.value!!.dropLast(1)
    }

    /**
     * Delete the operand from the list
     * */
    private fun operandDeleted(){
        // Remove the operand from the list and the string
        operandList.removeLast()
        _calculationString.value = calculationString.value!!.dropLast(1)
    }

    /**
     * Reverse the operation
     * */
    private fun reverseOperation(operand: String){

        val idx = calculationString.value!!.lastIndexOf(operand)

        // Get the number via substring
        val num = calculationString.value!!.substring(
            IntRange(idx + 1, calculationString.value!!.length - 1)).toFloat()

        // Update the result by performing the inverse operation
        when (operand) {
            "+" -> {
                _result.value = result.value!!.minus(num)
            }
            "-" -> {
                _result.value = result.value!!.plus(num)
            }
            "x" -> {
                _result.value = result.value!!.div(num)
            }
            else -> {
                _result.value = result.value!!.times(num)
            }
            // TODO: Add reverse operation for the complex operands
        }
    }

    /**
     * Convert radians to degrees
     * */
    private fun convertRadToDeg(rad: Float) : Float {
        return (rad * 180.0f) / PI_VALUE
    }

    /**
     * Convert degrees to radians
     * */
    private fun convertDegToRad(deg: Float): Float {
        return deg * (PI_VALUE / 180.0f)
    }

    /**
     * Determines if a factorial can be computed.
     *
     * If the number is not an integer or no other input has been made, it will set a flag for the
     * Fragment UI to display an error
     * */
    fun onFactorial(){
        // Set flag to true if number is a decimal or the string is empty
        if(decimalCheck.value == true ||
            calculationString.value!!.isEmpty()) _invalidFactFlag.value = true

        // Reset the decimal flag for the next number
        _decimalCheck.value = false


        val num: Float = if(operandList.isNotEmpty()){
            // Get the number after the previous operand
            val currentOperand = operandList[operandList.size - 1]
            val idx = calculationString.value!!.lastIndexOf(currentOperand)

            // Get the number via substring
            calculationString.value!!.substring(
                IntRange(idx + 1, calculationString.value!!.length - 1)).toFloat()
        }else{
            // Get the number
            calculationString.value!!.toFloat()
        }

        // Can only apply this operation if result is not null
        _result.value = factorial(num)

        // Add the factorial symbol to the string
        _calculationString.value = calculationString.value.plus('!')

        // Store the operand
        operandList.add("!")
    }

    /**
     * Calculate the factorial
     *
     * If decimal flag is indicated for the current number, flag it
     * */
    private fun factorial(num: Float): Float{

        if(num == 0f) return 1f

        var res = num
        for(n in num.toInt() - 1 downTo 1){
            res *= n
        }

        return res
    }

}