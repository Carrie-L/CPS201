package com.adsale.chinaplas.adapters

import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.ExhApplication

/**
 * Created by Carrie on 2020/1/8.
 */
class ApplicationAdapter(private var list: List<ExhApplication>, listener: OnItemClickListener) :
    CpsBaseAdapter<ExhApplication>(list, listener) {
    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.item_application
    }

    override fun setList(newList: List<ExhApplication>) {
        this.list = newList
        super.setList(newList)
    }

}