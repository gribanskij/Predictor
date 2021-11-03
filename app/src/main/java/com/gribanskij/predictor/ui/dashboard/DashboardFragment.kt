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
import com.gribanskij.predictor.data.StockModel
import com.gribanskij.predictor.data.StockParam
import com.gribanskij.predictor.databinding.FragmentDashboardBinding
import com.gribanskij.predictor.ui.dashboard.stock.StockFragment
import dagger.hilt.android.AndroidEntryPoint


const val ARG_STOCK_CODE = "stockCode"


@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!


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

        binding.pager.adapter = StockAdapter(this, StockParam.COLLECTION.stocks)
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = StockParam.COLLECTION.stocks[position]!!.NAME
        }.attach()
    }


    class StockAdapter(fragment: Fragment, private val stocks: Map<Int, StockModel>) :
        FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = stocks.size

        override fun createFragment(position: Int): Fragment {
            val fragment = StockFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_STOCK_CODE, stocks[position]!!.CODE)
            }
            return fragment
        }
    }
}