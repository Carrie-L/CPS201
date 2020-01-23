package com.adsale.chinaplas.ui.exhibitors


import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.base.BaseFilterFragment
import com.adsale.chinaplas.data.dao.Hall
import com.adsale.chinaplas.utils.LogUtil


/**
 * A simple [Fragment] subclass.
 */
class FilterHallFragment : BaseFilterFragment() {

    override fun initData() {
        LogUtil.i("this FilterHallFragment=${this},\nparent=${requireParentFragment()}")
        val adapter = HallAdapter(listOf(), viewModel.hallItemListener)
        recyclerView.adapter = adapter
        viewModel.getAllHalls()
        viewModel.hallFilterClear()

        viewModel.halls.observe(this, Observer {
            LogUtil.i("halls= ${it.size}, $it")
            adapter.setList(it)
        })

    }

    inner class HallAdapter(private var list: List<Hall>, listener: OnItemClickListener) :
        CpsBaseAdapter<Hall>(list, listener) {
        override fun getLayoutIdForPosition(position: Int): Int {
            return R.layout.item_hall
        }

        override fun setList(newList: List<Hall>) {
            list = newList
            super.setList(newList)
        }
    }


}
