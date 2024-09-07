package com.himangsKalita.calipro

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.himangsKalita.calipro.databinding.ActivityMainBinding
import com.notkamui.keval.Keval
import com.notkamui.keval.KevalInvalidArgumentException
import com.notkamui.keval.KevalInvalidExpressionException
import com.notkamui.keval.KevalInvalidSymbolException
import com.notkamui.keval.KevalZeroDivisionException
import kotlin.math.truncate

val operands = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
val operators = listOf("+", "-", "÷", "×")
var openBrackets = 0
var closeBrackets = 0
var lastChar = ""

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupEdgeToEdge()
        initialization()
    }

    private fun initialization() {

        window.statusBarColor = ContextCompat.getColor(this, R.color.grey)
        binding.etQuery.apply {
            showSoftInputOnFocus = false
            requestFocus()
        }

        binding.btnOne.setOnClickListener { appendQuery("1") }
        binding.btnTwo.setOnClickListener { appendQuery("2") }
        binding.btnThree.setOnClickListener { appendQuery("3") }
        binding.btnFour.setOnClickListener { appendQuery("4") }
        binding.btnFive.setOnClickListener { appendQuery("5") }
        binding.btnSix.setOnClickListener { appendQuery("6") }
        binding.btnSeven.setOnClickListener { appendQuery("7") }
        binding.btnEight.setOnClickListener { appendQuery("8") }
        binding.btnNine.setOnClickListener { appendQuery("9") }
        binding.btnZero.setOnClickListener { appendQuery("0") }
        binding.btnDot.setOnClickListener { appendQuery(".") }


        binding.btnAddition.setOnClickListener {

            val query = binding.etQuery.text.toString()

            if (query.isNotEmpty()) {

                lastChar = query.last().toString()

                if (lastChar !in operators) {

                    appendQuery("+")
                }
            }
        }
        binding.btnSubraction.setOnClickListener {

            val query = binding.etQuery.text.toString()

            if (query.isEmpty()) {

                appendQuery("-")
            }

            if (query.isNotEmpty() && query.last().toString() !in operators) {

                appendQuery("-")
            }
        }
        binding.btnMultiply.setOnClickListener {

            val query = binding.etQuery.text.toString()

            if (query.isNotEmpty()) {

                lastChar = query.last().toString()

                if (lastChar !in operators) {

                    appendQuery("×")
                }
            }
        }
        binding.btnDivide.setOnClickListener {

            val query = binding.etQuery.text.toString()

            if (query.isNotEmpty()) {

                lastChar = query.last().toString()

                if (lastChar !in operators) {

                    appendQuery("÷")
                }
            }
        }
        binding.btnModulo.setOnClickListener {

            val query = binding.etQuery.text.toString()

            if (query.isNotEmpty()) {

                lastChar = query.last().toString()

                if (lastChar !in operators) {

                    appendQuery("%")
                }
            }
        }

        binding.btnBrackets.setOnClickListener {

            val query = binding.etQuery.text.toString()

            if (query.isNotEmpty()) {

                lastChar = query.last().toString()
            }

            if (openBrackets >= closeBrackets &&
                (lastChar == "" || lastChar in operators || lastChar == "(")
            ) {

                appendQuery("(")
                openBrackets++
            } else if (closeBrackets < openBrackets &&
                (lastChar in operands || lastChar == ")")
            ) {

                appendQuery(")")
                closeBrackets++
            }
        }

        binding.btnClearAC.setOnClickListener {

            binding.etQuery.text.clear()
            binding.tvResult.text = ""
            lastChar = ""
            openBrackets = 0
            closeBrackets = 0
        }

        binding.btnDelete.setOnClickListener {

            val currentQuery = binding.etQuery.text.toString()
            if (currentQuery.isNotEmpty()) {

                if (currentQuery.last().toString() == "(") {

                    openBrackets--
                }

                if (currentQuery.last().toString() == ")") {

                    closeBrackets--
                }

                binding.etQuery.setText(binding.etQuery.text.substring(0, currentQuery.length - 1))
                binding.etQuery.setSelection(currentQuery.length - 1)

                calculationEvaluation()
            }
        }

        binding.btnDelete.setOnLongClickListener {

            binding.etQuery.text.clear()
            binding.tvResult.text = ""
            true
        }

        binding.btnEquals.setOnClickListener {

            binding.etQuery.setText(binding.tvResult.text.toString())
            binding.etQuery.setSelection(binding.etQuery.text.toString().length)
            binding.tvResult.text = ""
        }
    }

    private fun appendQuery(query: String) {

        val currentQuery = binding.etQuery.text.toString()

        if (currentQuery == "0") {

            if (query in operands) {

                binding.etQuery.setText(query)
                binding.etQuery.setSelection(1)
            }else if (query in operators || query == ".") {

                binding.etQuery.append(query)
            }

        } else if (currentQuery.isNotEmpty() && query == ".") {

            lastChar = currentQuery.last().toString()

            if (lastChar in operands) {

                binding.etQuery.append(query)
            }

        } else if (currentQuery.isEmpty() && query == ".") {

            binding.etQuery.append("0.")
            binding.etQuery.setSelection(2)
            return

        } else {

            binding.etQuery.append(query)
        }

        if (binding.etQuery.text.toString().isNotEmpty()) {

            calculationEvaluation()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculationEvaluation() {

        val query = binding.etQuery.text.toString()

        if (query.isNotEmpty()) {

            try {
                val expression = query
                    .replace("×", "*")
                    .replace("÷", "/")
                val result = Keval.eval(expression)
                binding.tvResult.setTextColor(resources.getColor(R.color.black, null))

                if (result.equals(truncate(result))) {

                    binding.tvResult.text = result.toInt().toString()
                }else {

                    binding.tvResult.text = result.toString()
                }



            } catch (e: KevalZeroDivisionException) {

                binding.tvResult.apply {
                    text = "Can't divide by 0"
                    setTextColor(resources.getColor(R.color.red, null))
                }
            } catch (e: KevalInvalidArgumentException) {

                binding.tvResult.apply {
                    text = "Invalid Argument!"
                    setTextColor(resources.getColor(R.color.red, null))
                }
            } catch (e: KevalInvalidExpressionException) {

                binding.tvResult.apply {
                    text = "Invalid Expression!"
                    setTextColor(resources.getColor(R.color.red, null))
                }
            } catch (e: KevalInvalidSymbolException) {

                binding.tvResult.apply {
                    text = "Invalid Operator!"
                    setTextColor(resources.getColor(R.color.red, null))
                }
            } catch (e: Exception) {

                binding.tvResult.text = "Error!"
            }
        }else {

            binding.tvResult.text = ""
        }
    }

    private fun setupEdgeToEdge() {

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}