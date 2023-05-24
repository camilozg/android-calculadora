package com.example.calculator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.github.ajalt.timberkt.d
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {

    private lateinit var auxTextView:TextView;
    private lateinit var mainTextView:TextView;

    companion object{
        const val DECIMAL = "."
        const val NO_OPERATION = 0
        const val EQUALS = 1
        const val SUM = 2
        const val SUBTRACTION = 3
        const val MULTIPLICATION = 4
        const val DIVISION = 5
        val OPERATION_CHAR = mapOf(
                                NO_OPERATION to "",
                                EQUALS to "=",
                                SUM to "+",
                                SUBTRACTION to "-",
                                MULTIPLICATION to Html.fromHtml("&#215",Html.FROM_HTML_MODE_LEGACY),
                                DIVISION to Html.fromHtml("&#247",Html.FROM_HTML_MODE_LEGACY)
                            )
    }

    private var storedResult: Double = 0.0;
    private var storedOperation: Int = NO_OPERATION;
    private var waitingForNumber: Boolean = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auxTextView = findViewById(R.id.auxTextView)
        mainTextView = findViewById(R.id.mainTextView)

        findViewById<Button>(R.id.zeroButton).setOnClickListener { pressDigit("0") }
        findViewById<Button>(R.id.oneButton).setOnClickListener { pressDigit("1") }
        findViewById<Button>(R.id.twoButton).setOnClickListener { pressDigit("2") }
        findViewById<Button>(R.id.threeButton).setOnClickListener { pressDigit("3") }
        findViewById<Button>(R.id.fourButton).setOnClickListener { pressDigit("4") }
        findViewById<Button>(R.id.fiveButton).setOnClickListener { pressDigit("5") }
        findViewById<Button>(R.id.sixButton).setOnClickListener { pressDigit("6") }
        findViewById<Button>(R.id.sevenButton).setOnClickListener { pressDigit("7") }
        findViewById<Button>(R.id.eightButton).setOnClickListener { pressDigit("8") }
        findViewById<Button>(R.id.nineButton).setOnClickListener { pressDigit("9") }

        findViewById<Button>(R.id.positiveNegativeButton).setOnClickListener { pressPositiveNegative() }
        findViewById<Button>(R.id.decimalButton).setOnClickListener { pressDecimal() }

        findViewById<Button>(R.id.plusButton).setOnClickListener { pressOperation(SUM) }
        findViewById<Button>(R.id.minusButton).setOnClickListener { pressOperation(SUBTRACTION) }
        findViewById<Button>(R.id.multiplicationButton).setOnClickListener { pressOperation(MULTIPLICATION) }
        findViewById<Button>(R.id.divisionButton).setOnClickListener { pressOperation(DIVISION) }

        findViewById<Button>(R.id.deleteButton).setOnClickListener { deleteDigit() }
        findViewById<Button>(R.id.resetButton).setOnClickListener { resetCalculator() }
        findViewById<Button>(R.id.equalsButton).setOnClickListener { pressEqualsButton() }

        findViewById<ImageButton>(R.id.expandResultButton).setOnClickListener { expandResult() }
    }

    private fun expandResult(){
        val intent = Intent(this, ExpandResultActivity::class.java)
        intent.putExtra(ExpandResultActivity.RESULT, formatNumberToDisplay(storedResult))
        startActivity(intent)
    }

    private fun deleteDigit(){
        if (storedOperation == EQUALS) {
            auxTextView.text = ""
        }

        if (!waitingForNumber){
            if(mainTextView.text.length == 2 && mainTextView.text.first().toString() == OPERATION_CHAR[SUBTRACTION]){
                mainTextView.text = "0"
            }else if (mainTextView.text.length > 1){
                mainTextView.text = mainTextView.text.toString().dropLast(1)
            }else{
                mainTextView.text = "0"
            }
        }
    }

    private fun resetCalculator(){
        auxTextView.text = ""
        mainTextView.text = "0"
        storedResult = 0.0;
        storedOperation = NO_OPERATION;
        waitingForNumber = false;
    }

    private fun pressDigit(digit: String){
        var mainTextViewNumber = mainTextView.text.toString()
        var firstDigit:String = mainTextView.text.toString().first().toString()

        if (waitingForNumber && storedOperation != EQUALS) {
            mainTextView.text = digit
            waitingForNumber = false
        }else if (waitingForNumber && storedOperation == EQUALS){
            auxTextView.text = ""
            storedResult = 0.0
            mainTextView.text = digit
            waitingForNumber = false
        }else{
            if (digit == "0"){
                var hasDecimal = mainTextViewNumber.indexOf(".") != -1
                if (firstDigit != "0" || hasDecimal){
                    mainTextView.text = "${mainTextViewNumber}$digit"
                }
            }else{
                if (firstDigit == "0" && mainTextViewNumber.length == 1){
                    mainTextView.text = "${mainTextViewNumber.dropLast(1)}$digit"
                }else{
                    mainTextView.text = "${mainTextViewNumber}$digit"
                }
            }
        }
    }

    private fun pressDecimal(){
        var mainTextViewNumber = mainTextView.text.toString()

        if (waitingForNumber && storedOperation != EQUALS){
            mainTextView.text = "0$DECIMAL"
            waitingForNumber = false
        }else if (waitingForNumber && storedOperation == EQUALS) {
            auxTextView.text = ""
            storedResult = 0.0
            mainTextView.text = "0$DECIMAL"
            waitingForNumber = false
        }else if (!mainTextViewNumber.contains(DECIMAL)) {
            mainTextView.text = "$mainTextViewNumber$DECIMAL"
        }
    }

    private fun pressPositiveNegative(){
        var mainTextViewNumber = mainTextView.text.toString()

        if (waitingForNumber) {
            waitingForNumber = false
        }

        if (storedOperation == EQUALS) {
            auxTextView.text = ""
        }

        if (mainTextViewNumber.toDouble() > 0.0) {
            mainTextView.text = "-$mainTextViewNumber"
        }else if (mainTextViewNumber.toDouble() < 0.0 && mainTextViewNumber.first() == '-'){
            mainTextView.text = "${mainTextViewNumber.drop(1)}"
        }
    }

    private fun pressOperation(operation: Int){
        var numberToDisplay: String

        if (waitingForNumber) {
            numberToDisplay = formatNumberToDisplay(storedResult)
            storedOperation = operation
            auxTextView.text = "$numberToDisplay${OPERATION_CHAR[operation]}"
            mainTextView.text = "$numberToDisplay"
        }else {
            if (storedOperation == DIVISION && mainTextView.text.toString().toDouble() == 0.0){
                resetCalculator()
                auxTextView.text = "Error: ${getString(R.string.divide_zero_error)}"
                return
            }else{
                storedResult = evaluateStoredOperation()
            }

            numberToDisplay = formatNumberToDisplay(storedResult)
            storedOperation = operation
            auxTextView.text = "$numberToDisplay${OPERATION_CHAR[operation]}"
            mainTextView.text = "$numberToDisplay"
            waitingForNumber = true
        }
    }

    private fun pressEqualsButton(){
        var mainTextViewNumber = mainTextView.text.toString()

        var num1 = formatNumberToDisplay(storedResult)
        var num2 = formatNumberToDisplay(mainTextView.text.toString().toDouble())

        if (storedOperation == DIVISION && mainTextView.text.toString().toDouble() == 0.0){
            resetCalculator()
            auxTextView.text = "Error: ${getString(R.string.divide_zero_error)}"
            return
        }else{
            storedResult = evaluateStoredOperation()
        }

        var numberToDisplay: String = formatNumberToDisplay(storedResult)
        if (storedOperation == NO_OPERATION){
            auxTextView.text = "$num2${OPERATION_CHAR[EQUALS]}"
        } else if(storedOperation != EQUALS) {
            auxTextView.text = "$num1${OPERATION_CHAR[storedOperation]}$num2${OPERATION_CHAR[EQUALS]}"
        }

        mainTextView.text = "$numberToDisplay"
        storedOperation = EQUALS
        waitingForNumber = true
    }

    private fun evaluateStoredOperation(): Double{
        var mainTextViewNumber = mainTextView.text.toString()

        return when (storedOperation){
            SUM -> storedResult + mainTextViewNumber.toDouble()
            SUBTRACTION -> storedResult - mainTextViewNumber.toDouble()
            MULTIPLICATION -> storedResult * mainTextViewNumber.toDouble()
            DIVISION -> storedResult / mainTextViewNumber.toDouble()
            NO_OPERATION -> mainTextViewNumber.toDouble()
            EQUALS -> mainTextViewNumber.toDouble()
            else -> 0.0
        }
    }

    private fun formatNumberToDisplay(number: Double): String {
        return if (number % 1 == 0.0){
            number.toInt().toString()
        } else{
            number.toBigDecimal().setScale(10, RoundingMode.HALF_UP ).toDouble().toBigDecimal().toPlainString()
        }
    }

}
