package com.adsale.chinaplas.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.adapters.SideAdapter
import com.adsale.chinaplas.data.dao.Country
import com.adsale.chinaplas.data.dao.ExhIndustry
import com.adsale.chinaplas.data.dao.Exhibitor
import com.adsale.chinaplas.utils.LogUtil

/**
 * Created by Carrie on 2020/1/19.
 * [展商列表]
 * [筛选产品列表]
 * [筛选国家列表]
 * [我的参展商列表]
 */
const val SIDE_EXHIBITOR = 101
const val SIDE_INDEUSTRY = 102
const val SIDE_COUNTRY = 103

class SideBarView : RelativeLayout {
    private lateinit var recyclerView: RecyclerView
    private var adapter: SideAdapter? = null
    private var exhibitors = mutableListOf<Exhibitor>()
    private var industries = mutableListOf<ExhIndustry>()
    private var countries = mutableListOf<Country>()
    private lateinit var rvScrollTo: RecyclerViewScrollTo
    private var sorts = mutableListOf<String>()
    private var sideType = 0
    private var isSortBySZ = true

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.view_side_bar, this, true)
        recyclerView = view.findViewById(R.id.rv_side)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
    }

    fun setAdapter(itemClickListener: OnItemClickListener) {
        if (adapter == null) {
            adapter = SideAdapter(listOf(), itemClickListener)
            recyclerView.adapter = adapter
        }
    }

    fun setAdapter(type: Int, scrollTo: RecyclerViewScrollTo) {
        sideType = type
        rvScrollTo = scrollTo
        if (adapter == null) {
            adapter = SideAdapter(listOf(), clickListener)
            recyclerView.adapter = adapter
        }
    }

    fun setData(sorts: List<String>) {
        LogUtil.i("setData=sorts= ${sorts.size}, list0=$")
        if (sorts.isNotEmpty()) {
            val itemHeight = measuredHeight / sorts.size
            LogUtil.i("height=$height, itemHeight=$itemHeight")
            adapter?.setItemHeight(itemHeight)
        }
        adapter?.setList(sorts)
    }

    /**
     * @param list 产品列表
     * @param isSort 是否需要排序
     */
    fun setExhibitorData(list: List<Exhibitor>) {
        sorts.clear()
        exhibitors.clear()
        exhibitors.addAll(list)
        for (entity in list) {
            sorts.add(entity.getSort())
        }
        if (sorts.isNotEmpty()) {
            sorts = sorts.distinct() as MutableList<String>
        }
        setSorts()
    }

    /**
     * @param list 产品列表
     * @param isSort 是否需要排序
     */
    fun setIndustryData(list: List<ExhIndustry>) {
        if (list.isEmpty()) {
            return
        }
        sorts.clear()
        industries.clear()
        industries.addAll(list)
        for (entity in list) {
            sorts.add(entity.getSort())
        }
        if (sorts.isNotEmpty()) {
            sorts = sorts.distinct() as MutableList<String>
        }
        setSorts()
    }

    /**
     * @param list 国家地区列表
     * @param isSort 是否需要排序
     */
    fun setCountryData(list: List<Country>) {
        if (list.isEmpty()) {
            return
        }
        sorts.clear()
        countries.clear()
        countries.addAll(list)
        for (entity in list) {
            sorts.add(entity.getSort())
        }
        if (sorts.isNotEmpty()) {
            sorts = sorts.distinct() as MutableList<String>
        }
        setSorts()
    }

    private fun setSorts() {
        val itemHeight = measuredHeight / sorts.size
        adapter?.setItemHeight(itemHeight)
        adapter?.setList(sorts)
    }

    private fun scrollIndustry(sort: String) {
        for ((i, entity) in industries.withIndex()) {
            if (entity.getSort() == sort) {
                rvScrollTo.scroll(i)
                LogUtil.i("scroll to: $i, $sort")
                break
            }
        }
    }

    private fun scrollCountry(sort: String) {
        for ((i, entity) in countries.withIndex()) {
            if (entity.getSort() == sort) {
                rvScrollTo.scroll(i)
                LogUtil.i("scroll to: $i, $sort")
                break
            }
        }
    }

    private val clickListener = OnItemClickListener { entity, pos ->
        when (sideType) {
            SIDE_INDEUSTRY -> scrollIndustry(entity as String)
            SIDE_COUNTRY -> scrollCountry(entity as String)
        }
    }


}