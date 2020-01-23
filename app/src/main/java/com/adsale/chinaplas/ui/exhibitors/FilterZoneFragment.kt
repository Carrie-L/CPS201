package com.adsale.chinaplas.ui.exhibitors


import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.base.BaseFilterFragment
import com.adsale.chinaplas.data.dao.Zone
import com.adsale.chinaplas.utils.LogUtil


/**
 * A simple [Fragment] subclass.
 */
class FilterZoneFragment : BaseFilterFragment() {

    override fun initData() {
        val adapter = ZoneAdapter(listOf(), viewModel.zoneItemListener)
        recyclerView.adapter = adapter
        viewModel.getAllZones()
        viewModel.zoneFilterClear()

        viewModel.zones.observe(this, Observer {
            LogUtil.i("zones= ${it.size}, ${it}")
            adapter.setList(it)
        })

    }

    inner class ZoneAdapter(private var list: List<Zone>, listener: OnItemClickListener) :
        CpsBaseAdapter<Zone>(list, listener) {
        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.item_zone
        }

        override fun setList(newList: List<Zone>) {
            list = newList
            super.setList(newList)
        }

    }

}
