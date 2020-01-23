package com.adsale.chinaplas.ui.exhibitors


import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.base.BaseFilterFragment
import com.adsale.chinaplas.data.dao.ExhIndustry
import com.adsale.chinaplas.ui.view.SIDE_INDEUSTRY
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.getCurrLanguage


/**
 * A simple [Fragment] subclass.
 */
class FilterIndustryFragment : BaseFilterFragment() {

    override fun initData() {
        val adapter = IndustryAdapter(listOf(), viewModel.industryItemListener)
        recyclerView.adapter = adapter
        viewModel.getAllIndustries()
        viewModel.industryFilterClear()

        showSideBar=true
        initSideBar(SIDE_INDEUSTRY)

        viewModel.industries.observe(this, Observer {
            LogUtil.i("language = ${getCurrLanguage()}")
            LogUtil.i("industries= ${it.size}, $it")
            adapter.setList(it)
            sideBarView.setIndustryData(it)
        })

    }


//    override fun scrollToPosition(sort: String) {
//        with(viewModel) {
//            industries.value?.let {
//                for ((i, entity) in it.withIndex()) {
//                    if (entity.getSort() == sort) {
//                        rvScrollTo.scroll(i)
//                        LogUtil.i("scroll to: $i, $sort")
//                        break
//                    }
//                }
//            }
//        }
//    }


    inner class IndustryAdapter(
        private var list: List<ExhIndustry>,
        listener: OnItemClickListener
    ) :
        CpsBaseAdapter<ExhIndustry>(list, listener) {
        private lateinit var entity: ExhIndustry
        override fun getLayoutIdForPosition(position: Int): Int {
            entity = list[position]
            if (position == 0) {
                entity.isTypeLabel.set(true)
            } else if (entity.getSort() == list[position - 1].getSort()) {
                entity.isTypeLabel.set(false)
            } else {
                entity.isTypeLabel.set(true)
            }
            return R.layout.item_industry
        }

        override fun setList(newList: List<ExhIndustry>) {
            list = newList
            super.setList(newList)
        }
    }


}
