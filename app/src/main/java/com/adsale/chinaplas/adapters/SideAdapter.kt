package com.adsale.chinaplas.adapters

import android.view.ViewGroup
import com.adsale.chinaplas.R
import com.adsale.chinaplas.utils.LogUtil
import com.adsale.chinaplas.utils.dp2px

/**
 * Created by Carrie on 2020/1/19.
 */
class SideAdapter(private var list: List<String>, itemClickListener: OnItemClickListener) :
    CpsBaseAdapter<String>(list, itemClickListener) {

    private var itemHeight = 0
    private var params: ViewGroup.LayoutParams? = null

    init {

    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.item_side_bar
    }

    fun setItemHeight(height: Int) {
        itemHeight = height
    }

    override fun setList(newList: List<String>) {
        list = newList
        super.setList(newList)
        params = ViewGroup.LayoutParams(dp2px(32f), itemHeight)
        LogUtil.i("itemHeight=${itemHeight}")
    }

    override fun onBindViewHolder(holder: CpsBaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (params != null) {
            holder.itemView.layoutParams = params
        }
    }
}