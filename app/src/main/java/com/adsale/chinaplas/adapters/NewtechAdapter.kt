package com.adsale.chinaplas.adapters

import com.adsale.chinaplas.BR
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.NewtechProductInfo
import com.baidu.speech.utils.LogUtil
import com.bumptech.glide.request.RequestOptions

class NewtechAdapter(private var list: List<NewtechProductInfo>,
                     itemClickListener: OnItemClickListener,
                     private val options: RequestOptions) :
    CpsBaseAdapter<NewtechProductInfo>(list, itemClickListener) {


    override fun getLayoutIdForPosition(position: Int): Int {
        LogUtil.i("getLayoutIdForPosition")

        if (list.isNotEmpty()) {
            for (entity in list) {
                LogUtil.i("list=$position ^^^ ${list[position].isAder}^^^${list[position].toString()}")
            }
        }

        return if (list.isNotEmpty() && list[position].isAder) {
            R.layout.item_new_tec_ad
        } else
            R.layout.item_new_tec
    }

    override fun setList(newList: List<NewtechProductInfo>) {
        this.list = newList
        super.setList(newList)
    }

    override fun onBindViewHolder(holder: CpsBaseViewHolder, position: Int) {
        holder.binding.setVariable(BR.options, options)
        super.onBindViewHolder(holder, position)
    }

}