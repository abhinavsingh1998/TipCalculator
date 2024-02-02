package com.example.tipcalculator.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tipcalculator.R
import com.example.tipcalculator.databinding.ActivityMainBinding
import com.example.tipcalculator.viewmodels.TipCalculatorViewModel
import kotlin.math.ceil
import kotlin.math.floor

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TipCalculatorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[TipCalculatorViewModel::class.java]
        viewModel.billAmountMutableLiveData.postValue(0.0)
        addObservers()
        addListeners()
    }

    private fun addObservers() {
        viewModel.billAmountMutableLiveData.observe(this, Observer {
            calculateNewTotalBill(binding.sbTip.progress)
            calculateNewSplitBill(binding.sbSplit.progress)
        })
    }

    private fun addListeners() {
        binding.etBillAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                if(text.isNotEmpty()) {
                    viewModel.billAmountMutableLiveData.postValue(text.toDouble())
                } else {
                    viewModel.billAmountMutableLiveData.postValue(0.0)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.sbTip.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.tvTipEditPercentage.text = "${p1}%"
                calculateNewTotalBill(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        binding.sbSplit.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.tvSplitEditNumber.text = "${p1}"
                calculateNewSplitBill(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        binding.rgRoundingAmount.setOnCheckedChangeListener { radioGroup, checkedId ->
            val enteredBillAmount = viewModel.billAmountMutableLiveData.value
            enteredBillAmount?.let { billAmount ->
                if(billAmount != 0.0) {
                    when (checkedId) {

                        R.id.rb_round_up -> {
                            viewModel.billAmountOriginalMutableLiveData.postValue(viewModel.billAmountMutableLiveData.value)
                            viewModel.billAmountMutableLiveData.postValue(ceil(billAmount))
                            binding.etBillAmount.setText(ceil(billAmount).toString())
                        }

                        R.id.rb_round_down -> {
                            viewModel.billAmountOriginalMutableLiveData.postValue(viewModel.billAmountMutableLiveData.value)
                            viewModel.billAmountMutableLiveData.postValue(floor(billAmount))
                            binding.etBillAmount.setText(floor(billAmount).toString())
                        }
                    }
                }
            }
        }
    }

    private fun calculateNewTotalBill(tipPercentage: Int) {
        val billAmount = viewModel.billAmountMutableLiveData.value
        billAmount?.let { amount ->
            val tipAmount = ((tipPercentage / 100.0) * amount)
            val totalBillAmount = amount + tipAmount
            binding.tvTipAmount.text = String.format("%.2f", tipAmount)
            binding.tvTotalBillAmount.text = String.format("%.2f", totalBillAmount)
        }
    }

    private fun calculateNewSplitBill(splitCount: Int) {
        val billAmount = viewModel.billAmountMutableLiveData.value
        billAmount?.let { amount ->
            val splitBillAmount = String.format("%.2f", (amount / splitCount))
            binding.tvTotalSplitAmount.text = "Rs ${splitBillAmount}"
        }
    }
}