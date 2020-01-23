package com.adsale.chinaplas.ui.tools

import com.adsale.chinaplas.R
import com.adsale.chinaplas.adapters.CpsBaseAdapter
import com.adsale.chinaplas.adapters.OnItemClickListener
import com.adsale.chinaplas.data.dao.ExhibitorHistory
import com.adsale.chinaplas.utils.getTodayDate
import com.adsale.chinaplas.utils.getYesterdayDate


/**
 */
class ExhibitorHistoryAdapter(
    private var list: List<ExhibitorHistory>,
    itemClickListener: OnItemClickListener
) : CpsBaseAdapter<ExhibitorHistory>(list, itemClickListener) {
    private var entity: ExhibitorHistory = ExhibitorHistory()
    private val todayDate: String = getTodayDate()
    private val yesterdayDate: String = getYesterdayDate()

    override fun getLayoutIdForPosition(position: Int): Int {
        entity = list[position]
        if (position == 0) {
//            if (entity.time!!.split(" ")[0] == todayDate) {
//                entity.isTypeLabel.set(1)
//            } else if (entity.time!!.split(" ")[0] == yesterdayDate) {
//                entity.isTypeLabel.set(2)
//            } else {
//                entity.isTypeLabel.set(3)
//            }
            setLabel(entity.time!!.split(" ")[0])
        } else if (entity.time!!.split(" ")[0] == list[position - 1].time!!.split(" ")[0]) {
            entity.isTypeLabel.set(-1)
        } else {
            setLabel(entity.time!!.split(" ")[0])
        }
        return R.layout.item_history_exhibitor
    }

    private fun setLabel(date: String) {
        when (date) {
            todayDate -> entity.isTypeLabel.set(1)
            yesterdayDate -> entity.isTypeLabel.set(2)
            else -> entity.isTypeLabel.set(3)
        }
    }

    override fun setList(newList: List<ExhibitorHistory>) {
        list = newList
        super.setList(newList)
    }


}
