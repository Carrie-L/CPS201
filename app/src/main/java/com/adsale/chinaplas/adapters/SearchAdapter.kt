package com.adsale.chinaplas.adapters

import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.HtmlText

/**
 * Created by Carrie on 2020/3/5.
 */
class SearchAdapter(private var list: List<HtmlText>, itemClickListener: OnItemClickListener) :
    CpsBaseAdapter<HtmlText>(list, itemClickListener) {
    private lateinit var entity: HtmlText

    override fun getLayoutIdForPosition(position: Int): Int {
        if (list.isNotEmpty()) {
            entity = list[position]
            when {
                position == 0 -> {
                    entity.isTypeLabel = true
                }
                entity.groupID == list[position - 1].groupID -> {
                    entity.isTypeLabel = false
                }
                else -> {
                    entity.isTypeLabel = true
                }
            }
        }
        return R.layout.item_global_search
    }

    override fun setList(newList: List<HtmlText>) {
        this.list = newList
        super.setList(newList)
    }

    override fun onBindViewHolder(holder: CpsBaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
    }


}