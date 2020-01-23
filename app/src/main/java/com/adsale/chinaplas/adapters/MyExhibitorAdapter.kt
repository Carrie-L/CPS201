package com.adsale.chinaplas.adapters

import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.Exhibitor

/**
 * Created by Carrie on 2020/1/16.
 */
class MyExhibitorAdapter(private val itemClickListener: OnItemClickListener) :
    CpsBaseDiffAdapter<Exhibitor>(itemClickListener) {
    private lateinit var entity: Exhibitor
    override fun getLayoutIdForPosition(position: Int): Int {
        entity = getItem(position)
        if (position == 0) {
            entity.isTypeLabel.set(0)
        } else if (entity.getSort() == getItem(position - 1).getSort()) {
            entity.isTypeLabel.set(1)
        } else {
            entity.isTypeLabel.set(0)
        }
        return R.layout.item_my_exhibitor
    }


}