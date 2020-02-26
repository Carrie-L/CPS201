package com.adsale.chinaplas.ui.events


import androidx.fragment.app.Fragment
import com.adsale.chinaplas.utils.DATE_1

/**
 * A simple [Fragment] subclass.
 */
class EventOneFragment : BaseEventFragment() {

    override fun initedData() {
        super.initedData()
        eventViewModel.getDateEvents("%$DATE_1%")
    }


}
