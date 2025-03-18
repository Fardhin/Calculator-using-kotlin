package com.example.calculatorusingkotlin
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var tvExpression: TextView
    private lateinit var tvResult: TextView
    private var expressionText: String = ""
    private val historyList = mutableListOf<String>()  // Stores last 10 calculations

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvExpression = findViewById(R.id.tvExpression)
        tvResult = findViewById(R.id.tvResult)

        // Number buttons
        val numberButtons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        for (id in numberButtons) {
            findViewById<Button>(id).setOnClickListener {
                expressionText += (it as Button).text
                tvExpression.text = expressionText
            }
        }

        // Operator buttons
        findViewById<Button>(R.id.btnPlus).setOnClickListener { addOperator("+") }
        findViewById<Button>(R.id.btnMinus).setOnClickListener { addOperator("−") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { addOperator("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { addOperator("÷") }
        findViewById<Button>(R.id.btnMod).setOnClickListener { addOperator("%") }
        findViewById<Button>(R.id.btnDot).setOnClickListener { addOperator(".") }

        // Clear all
        findViewById<Button>(R.id.btnAC).setOnClickListener {
            expressionText = ""
            tvExpression.text = ""
            tvResult.text = ""
        }

        // Delete last character
        findViewById<Button>(R.id.btnDel).setOnClickListener {
            if (expressionText.isNotEmpty()) {
                expressionText = expressionText.dropLast(1)
                tvExpression.text = expressionText
            }
        }

        // Evaluate expression
        findViewById<Button>(R.id.btnEqual).setOnClickListener {
            try {
                val formattedExpression = expressionText
                    .replace("÷", "/")
                    .replace("×", "*")
                    .replace("−", "-")

                val result = ExpressionBuilder(formattedExpression).build().evaluate()
                val calculation = "$expressionText = $result"

                tvResult.text = "=$result"

                // Store last 10 calculations
                if (historyList.size >= 10) {
                    historyList.removeAt(0)  // Remove oldest entry
                }
                historyList.add(calculation)

            } catch (e: Exception) {
                e.printStackTrace()
                tvResult.text = "Error"
            }
        }

        // Show history
        findViewById<Button>(R.id.btnHistory).setOnClickListener {
            showHistory()
        }
    }

    private fun addOperator(operator: String) {
        if (expressionText.isNotEmpty() && !isLastCharacterOperator()) {
            expressionText += operator
            tvExpression.text = expressionText
        }
    }

    private fun isLastCharacterOperator(): Boolean {
        return expressionText.lastOrNull()?.let { it in "+−×÷%" } ?: false
    }

    private fun showHistory() {
        val historyText = if (historyList.isEmpty()) "No history available" else historyList.joinToString("\n")
        AlertDialog.Builder(this)
            .setTitle("Calculation History")
            .setMessage(historyText)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
