package com.gribanskij.predictor.ui.stock

import androidx.fragment.app.Fragment
import com.gribanskij.predictor.R
import com.gribanskij.predictor.databinding.FragmentStockBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StockFragment:Fragment(R.layout.fragment_stock) {
    private var _binding: FragmentStockBinding? = null
    private val binding get() = _binding!!

}