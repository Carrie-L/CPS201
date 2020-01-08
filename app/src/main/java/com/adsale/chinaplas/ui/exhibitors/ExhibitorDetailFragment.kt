package com.adsale.chinaplas.ui.exhibitors


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.MainIconRepository
import com.adsale.chinaplas.databinding.ActivityTestBinding
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.viewmodels.MainViewModel
import com.adsale.chinaplas.viewmodels.MainViewModelFactory
import com.google.android.material.appbar.AppBarLayout

/**
 * A simple [Fragment] subclass.
 */
class ExhibitorDetailFragment : Fragment() {
    private lateinit var binding: ActivityTestBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_test, container, false)
        mainViewModel = ViewModelProviders.of(
            requireActivity(), MainViewModelFactory(
                requireNotNull(this.activity).application,
                MainIconRepository.getInstance(CpsDatabase.getInstance(requireContext()).mainIconDao())
            )
        )
            .get(MainViewModel::class.java)

        var o = 0  // 让收缩时候设置title只执行一次
        binding.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener() { _, offset ->
            if (offset >= -85) {  // 展开
                o = 0
                mainViewModel.title.value = getString(R.string.title_company_info)
            } else if (o == 0) { // 收缩
                mainViewModel.title.value = "阿曼石油与化工1"
                o++
            }
        })

        val navController = findNavController()






        return binding.root
    }


}
