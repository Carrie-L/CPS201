package com.adsale.chinaplas.ui.tools


import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController

import com.adsale.chinaplas.R
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.databinding.FragmentToolBinding
import com.adsale.chinaplas.utils.isMyChinaplasLogin

/**
 * A simple [Fragment] subclass.
 */
class ToolFragment : BaseFragment() {

    private lateinit var binding: FragmentToolBinding

    override fun initedView(inflater: LayoutInflater) {
        binding = FragmentToolBinding.inflate(inflater, baseFrame, true)
    }

    override fun initView() {
    }

    override fun initedData() {

    }

    override fun initData() {
        isBackCustom = true

        val navController = NavHostFragment.findNavController(this)
        binding.tvMyCps.setOnClickListener {
            resetBackDefault()
            mainViewModel.addFragmentID(R.id.menu_tool)
            if (isMyChinaplasLogin()) {
                navController.navigate(ToolFragmentDirections.actionMenuToolToMyChinaplasFragment())
            } else {
                navController.navigate(ToolFragmentDirections.actionMenuToolToMyChinaplasLoginFragment())
            }
        }
        binding.tvMySchedule.setOnClickListener {
            resetBackDefault()
            Toast.makeText(requireContext(), "TBC", Toast.LENGTH_SHORT).show()
//            findNavController().navigate(R.id.myScheduleFragment)
        }
        binding.tvExhibitorHistory.setOnClickListener {
            resetBackDefault()
//            Toast.makeText(requireContext(), "TBC", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.exhibitorHistotyFragment)
        }
    }

    override fun back() {
        mainViewModel.removeFragmentId(R.id.menu_tool)
        findNavController().popBackStack(R.id.nav_home, false)
    }


}
