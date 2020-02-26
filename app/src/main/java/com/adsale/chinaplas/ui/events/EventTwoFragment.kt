package com.adsale.chinaplas.ui.events


import androidx.fragment.app.Fragment
import com.adsale.chinaplas.utils.DATE_2


/**
 * A simple [Fragment] subclass.
 */
class EventTwoFragment : BaseEventFragment() {

    override fun initedData() {
        super.initedData()
        eventViewModel.getDateEvents("%$DATE_2%")
    }



}
