package com.adsale.chinaplas

import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.SeminarInfo

class SeminarAdapter(list: List<SeminarInfo>, itemClickListener: OnItemClickListener) :
    CpsBaseAdapter<SeminarInfo>(list, itemClickListener) {
    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.item_seminar
    }

}