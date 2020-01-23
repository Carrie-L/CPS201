package com.adsale.chinaplas.ui.exhibitors


import android.text.TextUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adsale.chinaplas.adapters.ApplicationAdapter
import com.adsale.chinaplas.base.BaseFilterFragment
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.viewmodels.FilterViewModel
import com.adsale.chinaplas.viewmodels.FilterViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
const val APPLICATION_EXHIBITOR = "appExhi"
const val APPLICATION_NEW_TECH = "appNewTech"

class FilterApplicationFragment() : BaseFilterFragment() {
    private var type = ""

    override fun initData() {
        LogUtil.i("this FilterApplicationFragment=${this},\nparent=${requireParentFragment()}")
        arguments?.let {
            type = FilterApplicationFragmentArgs.fromBundle(it).type.toString()
            if (TextUtils.isEmpty(type)) {
                type = APPLICATION_EXHIBITOR
            }
        }

        val adapter = ApplicationAdapter(listOf(), viewModel.appItemListener)
        recyclerView.adapter = adapter
        viewModel.getAllApplications()
        viewModel.appFilterClear()

        viewModel.applications.observe(this, Observer {
            LogUtil.i("applications= ${it.size}, ${it}")
            adapter.setList(it)
        })
    }


}
