package com.gribanskij.predictor.ui.dashboard

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.gribanskij.predictor.R
import com.gribanskij.predictor.data.source.remote.*
import com.gribanskij.predictor.databinding.FragmentDashboardBinding
import com.gribanskij.predictor.ui.dashboard.stock.StockFragment
import dagger.hilt.android.AndroidEntryPoint


const val ARG_STOCK_NAME = "stockName"


@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val tabNames =
        mapOf(0 to SBER_NAME, 1 to YAND_NAME, 2 to GAZPROM_NAME, 3 to LUKOIL_NAME, 4 to ROSN_NAME)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.visit_memu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                initTab()
                true
            }
            else -> false
        }
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


    private fun initTab() {

        binding.pager.adapter = StockAdapter(this, tabNames)
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }


    class StockAdapter(fragment: Fragment, private val names: Map<Int, String>) :
        FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = names.size

        override fun createFragment(position: Int): Fragment {
            val fragment = StockFragment()
            fragment.arguments = Bundle().apply {
                putString(ARG_STOCK_NAME, names[position])
            }
            return fragment
        }
    }
}