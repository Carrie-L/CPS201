package com.adsale.chinaplas.adapters

import android.widget.ImageView
import com.adsale.chinaplas.R

/**
 * Created by Carrie on 2020/1/15.
 */
class MainHelpAdapter(list: List<Int>,listener: OnItemClickListener) : CpsBaseAdapter<Int>(list, listener) {
    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.layout_image
    }

}