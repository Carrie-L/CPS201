package com.adsale.chinaplas.adapters

import androidx.constraintlayout.widget.ConstraintLayout
import com.adsale.chinaplas.R
import com.adsale.chinaplas.data.dao.WebContent
import com.adsale.chinaplas.data.entity.MyChinaplasEntity
import com.adsale.chinaplas.databinding.ItemMyChinaplasBinding
import com.adsale.chinaplas.databinding.ItemTipBinding
import com.adsale.chinaplas.utils.dp2px
import com.adsale.chinaplas.utils.getScreenWidth
import com.adsale.chinaplas.utils.isTablet

/**
 * Created by Carrie on 2019/12/27.
 */
class MyChinaplasAdapter(list: List<MyChinaplasEntity>, listener: OnItemClickListener) :
    CpsBaseAdapter<MyChinaplasEntity>(list, listener) {
    private var width = 0
    private val param: ConstraintLayout.LayoutParams
    private val margin: Int

    init {
        if (isTablet()) {  // 平板一行3个
            width = (getScreenWidth() - dp2px(16f) * 3) / 3
            param = ConstraintLayout.LayoutParams(width, (width * 0.7).toInt())
        } else {
            width = (getScreenWidth() - dp2px(16f) * 3) / 2
            param = ConstraintLayout.LayoutParams(width, width)
        }

        margin = dp2px(16f)
    }

    override fun onBindViewHolder(holder: CpsBaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if (isTablet()) {
            param.marginStart = margin
        } else {
            if (position % 2 == 0) {
                param.marginStart = margin
                param.marginEnd = margin / 2
            } else {
                param.marginStart = margin / 2
                param.marginEnd = margin
            }
            param.topMargin = dp2px(16f)
        }
        (holder.binding as ItemMyChinaplasBinding).itemCpsLayout.layoutParams = param
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return R.layout.item_my_chinaplas
    }

}