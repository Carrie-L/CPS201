package com.adsale.chinaplas.ui.events


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager

import com.adsale.chinaplas.R
import com.adsale.chinaplas.base.BaseFragment
import com.adsale.chinaplas.databinding.FragmentSeminarBinding

/**
 * A simple [Fragment] subclass.
 */
class SeminarFragment : BaseFragment() {
    private lateinit var viewPager: ViewPager

    override fun initedView(inflater: LayoutInflater) {
        val binding = FragmentSeminarBinding.inflate(inflater, baseFrame, true)
        viewPager = binding.seminarViewPager
    }

    override fun initView() {
    }

    override fun initedData() {
        val fragments = arrayListOf<Fragment>()
    }

    override fun initData() {
    }

    override fun back() {
    }

    companion object {
        @Volatile
        private var instance: SeminarFragment? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SeminarFragment().also { instance = it }
            }
    }


}
