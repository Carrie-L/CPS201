package com.adsale.chinaplas.ui.exhibitors


import android.text.TextUtils
import androidx.lifecycle.Observer
import com.adsale.chinaplas.adapters.ApplicationAdapter
import com.adsale.chinaplas.base.BaseFilterFragment
import com.adsale.chinaplas.data.dao.CpsDatabase
import com.adsale.chinaplas.data.dao.NewtechRepository
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.setSPEventFilter
import com.adsale.chinaplas.utils.setSPSeminarFilter

/**
 * 列表
 */
const val APPLICATION_EXHIBITOR = "appExhi"
const val APPLICATION_NEW_TECH = "appNewTech"
const val APPLICATION_EVENT = "appEvent"
const val APPLICATION_SEMINAR = "appSeminar"

/*新技术产品*/
const val TYPE_NEW_TEC_PRODUCT = "PRD" /* 列表为 新技术产品 - 筛选 - 产品 */
const val TYPE_NEW_TEC_APPLICATIONS = "APT" /* 列表为 新技术产品 - 筛选 - 应用 */
const val TYPE_NEW_TEC_THEMATIC = "THS" /* 列表为 新技术产品 - 筛选 - 主题专集 */
const val TYPE_NEW_TECH_AREA = "AREA" /* 列表为 新技术产品 - 筛选 - 新品技术 */

class FilterApplicationFragment : BaseFilterFragment() {
    private var type = ""

    override fun initData() {
        LogUtil.i("this FilterApplicationFragment=${this},\nparent=${requireParentFragment()}")
        arguments?.let {
            type = FilterApplicationFragmentArgs.fromBundle(it).type.toString()
            if (TextUtils.isEmpty(type)) {
                type = APPLICATION_EXHIBITOR
            }
            LogUtil.i("type=$type")
        }

        val adapter = ApplicationAdapter(listOf(), viewModel.appItemListener)
        recyclerView.adapter = adapter
        if (type == APPLICATION_EXHIBITOR) {     // ※ 展商列表
            getExhibitorApplications()
        } else if (type == APPLICATION_EVENT || type == APPLICATION_SEMINAR) {    // ※ 同期活动
            getEventApplications()
        } else if (type == TYPE_NEW_TEC_PRODUCT || type == TYPE_NEW_TEC_APPLICATIONS
            || type == TYPE_NEW_TEC_THEMATIC || type == TYPE_NEW_TECH_AREA) {     // ※ 新技术产品
            getNewtechList(type)
        }

        viewModel.applications.observe(this, Observer {
            LogUtil.i("applications= ${it.size}, ${it}")
            adapter.setList(it)
        })

        mainViewModel.backClicked.observe(this, Observer {
            if (mainViewModel.isChangeRightIcon.value == false && it == true) { // 返回
                LogUtil.i("带着结果返回啦啦啦2~~~${viewModel.appFilters.value.toString()}")
                if (type == APPLICATION_EVENT) {
                    setSPEventFilter(viewModel.appFilters.value.toString())
                } else if (type == APPLICATION_SEMINAR) {
                    setSPSeminarFilter(viewModel.appFilters.value.toString())
                }
            }
        })
    }

    private fun getExhibitorApplications() {
        viewModel.getAllApplications()
        viewModel.appFilterClear()
    }

    private fun getEventApplications() {
        viewModel.getEventApplications()
        viewModel.appFilterClear()
    }

    private fun getNewtechList(type: String) {
        when (type) {
            TYPE_NEW_TEC_PRODUCT -> viewModel.newTechClear1()
            TYPE_NEW_TEC_APPLICATIONS -> viewModel.newTechClear2()
            TYPE_NEW_TEC_THEMATIC -> viewModel.newTechClear3()
            TYPE_NEW_TECH_AREA -> viewModel.newTechClear4()
        }
        viewModel.setNewtechType(type)
        viewModel.setNewtechRepository(NewtechRepository.getInstance(CpsDatabase.getInstance(requireContext()).newtechDao()))
        viewModel.getNewtechList(type)
    }



}
