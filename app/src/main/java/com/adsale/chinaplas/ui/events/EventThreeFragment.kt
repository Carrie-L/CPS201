package com.adsale.chinaplas.ui.events


import androidx.fragment.app.Fragment
import com.adsale.chinaplas.R
import com.adsale.chinaplas.utils.DATE_3

/**
 * A simple [Fragment] subclass.
 */
class EventThreeFragment : BaseEventFragment() {

    override fun initedData() {
        layoutId = R.layout.item_event_part
        super.initedData()
        eventViewModel.getDateEvents("%$DATE_3%")
    }


}
