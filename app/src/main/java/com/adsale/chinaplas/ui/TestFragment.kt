package com.adsale.chinaplas.ui


import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import androidx.navigation.fragment.findNavController

import com.adsale.chinaplas.R
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.databinding.FragmentTestBinding
import com.adsale.chinaplas.utils.LogUtil

/**
 * A simple [Fragment] subclass.
 */
class TestFragment : BaseFragment() {

//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
//                              savedInstanceState: Bundle?): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_test, container, false)
//    }

    override fun initedView(inflater: LayoutInflater) {
        val binding = FragmentTestBinding.inflate(inflater, baseBinding.baseFrameLayout, true)
    }

    override fun initView() {
    }

    override fun initedData() {
    }

    override fun initData() {
        isBackCustom=true

    }

    override fun back() {
        LogUtil.i("back.....()")
        findNavController().navigate(R.id.filterHallFragment)
    }


}
