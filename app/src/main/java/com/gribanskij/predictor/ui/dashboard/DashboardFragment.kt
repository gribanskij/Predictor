package com.gribanskij.predictor.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.gribanskij.predictor.R
import com.gribanskij.predictor.data.source.remote.*
import com.gribanskij.predictor.databinding.FragmentDashboardBinding
import com.gribanskij.predictor.ui.stock.StockFragment
import dagger.hilt.android.AndroidEntryPoint


const val ARG_STOCK_NAME = "stockName"


@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var stockAdapter: StockAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stockAdapter = StockAdapter(this)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)
        initTab()


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    private fun initTab(){
        binding.pager.adapter = stockAdapter
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            val tabName = stockAdapter.getTabName(position)
            tab.text = tabName
        }.attach()
    }


    class StockAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 5

        override fun createFragment(position: Int): Fragment {
            val tabName = getTabName(position)
            val fragment = StockFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_STOCK_NAME, tabName)
            }
            return fragment
        }

         fun getTabName(position: Int): String {
            return when (position) {
                0 -> {
                    SBER_NAME
                }
                1 -> {
                    YAND_NAME
                }
                2 -> {
                    GAZPROM_NAME
                }
                3 -> {
                    LUKOIL_NAME
                }
                4 -> {
                    ROSN_NAME
                }
                else -> {
                    SBER_NAME
                }
            }
        }
    }
}