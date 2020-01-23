package com.adsale.chinaplas.ui.exhibitors


import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.base.BaseFilterFragment
import com.adsale.chinaplas.data.dao.Country
import com.adsale.chinaplas.data.dao.ExhIndustry
import com.adsale.chinaplas.ui.view.SIDE_COUNTRY
import com.adsale.chinaplas.utils.LogUtil


/**
 * A simple [Fragment] subclass.
 */
class FilterRegionFragment : BaseFilterFragment() {

    override fun initData() {
        val adapter = RegionAdapter(listOf(), viewModel.regionItemListener)
        recyclerView.adapter = adapter
        viewModel.getAllRegions()
        viewModel.regionFilterClear()

        showSideBar = true
        initSideBar(SIDE_COUNTRY)

        viewModel.regions.observe(this, Observer {
            LogUtil.i("regions= ${it.size}, ${it}")
            adapter.setList(it)
            sideBarView.setCountryData(it)
        })

    }

    inner class RegionAdapter(private var list: List<Country>, listener: OnItemClickListener) :
        CpsBaseAdapter<Country>(list, listener) {
        private lateinit var entity: Country
        override fun getLayoutIdForPosition(position: Int): Int {
            entity = list[position]
            if (position == 0) {
                entity.isTypeLabel.set(true)
            } else if (entity.getSort() == list[position - 1].getSort()) {
                entity.isTypeLabel.set(false)
            } else {
                entity.isTypeLabel.set(true)
            }
            return R.layout.item_region
        }

        override fun setList(newList: List<Country>) {
            list = newList
            super.setList(newList)
        }
    }


}
