package com.adsale.chinaplas.adapters

import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.entity.Property

class ADScrollAdapter(list: List<Property>, itemClickListener: OnItemClickListener) :
    CpsBaseAdapter<Property>(list, itemClickListener) {
    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.item_image
    }
}
