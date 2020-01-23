package com.adsale.chinaplas.adapters

import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.entity.ExhibitorFilter
import com.adsale.chinaplas.utils.LogUtil.i

/**
 * Created by Carrie on 2020/1/8.
 */
class FilterAdapter(private var list: MutableList<ExhibitorFilter>,clearListener:OnItemClickListener) :
    CpsBaseAdapter<ExhibitorFilter>(list, clearListener) {
    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.item_filter
    }

    override fun setList(newList: List<ExhibitorFilter>) {
        this.list = newList as MutableList
        super.setList(newList)
    }

}