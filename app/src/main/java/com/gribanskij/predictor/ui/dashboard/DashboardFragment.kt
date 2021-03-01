package com.gribanskij.predictor.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.gribanskij.predictor.R
import com.gribanskij.predictor.databinding.FragmentDashboardBinding


const val ARG_OBJECT = "stockName"

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var stockAdapter: StockAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stockAdapter = StockAdapter(this)
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pager.adapter = stockAdapter

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            val tabName = stockAdapter.getTabName(position)
            tab.text = tabName
        }.attach()


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    class StockAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 5

        override fun createFragment(position: Int): Fragment {
            val tabName = getTabName(position)
            val fragment = StockFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_OBJECT, tabName)
            }
            return fragment
        }

         fun getTabName(position: Int): String {
            return when (position) {
                0 -> {
                    "SBER"
                }
                1 -> {
                    "YNDX"
                }
                2 -> {
                    "GAZP"
                }
                3 -> {
                    "LKOH"
                }
                4 -> {
                    "ROSN"
                }
                else -> {
                    "SBER"
                }
            }
        }
    }
}