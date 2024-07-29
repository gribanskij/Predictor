package com.gribanskij.predictor.ui.dashboard

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
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



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)
        initMenu()
        initTab()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initMenu(){
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.visit_memu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_refresh  -> {
                        initTab()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    private fun initTab() {

        binding.pager.adapter = StockAdapter(this, StockParam.COLLECTION.stocks)
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = StockParam.COLLECTION.stocks[position]!!.NAME
        }.attach()
    }


    private class StockAdapter(fragment: Fragment, private val stocks: Map<Int, StockModel>) :
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